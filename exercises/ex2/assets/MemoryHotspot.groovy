import com.sap.gateway.ip.core.customdev.util.Message
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.NodeList

/*
 * SalesOrder memory-profiling hotspot.
 *
 * Parses the incoming SalesOrder XML (sal:MT_SalesOrder_Source / Order / Header + Item)
 * and flattens every order line into a CSV report. It is written in a deliberately
 * memory-inefficient way so that AI-based Memory Profiling has a clear, high-severity
 * hotspot to report and so that the "Optimize" GenAI review (Exercise 3) has something
 * concrete to improve:
 *
 *   - the whole DOM tree is materialised in memory (no streaming parse)
 *   - the report is built with the String-concatenation anti-pattern (report = report + ...),
 *     which reallocates the entire String on every line (quadratic memory churn / GC pressure)
 *   - a parallel List keeps a second full copy of every row
 *   - a per-item Map is allocated for every line and retained in a List
 *   - a full copy of the growing report is snapshotted and retained on every line,
 *     so retained heap grows quadratically with the number of items
 *   - the grand total tax is recomputed by re-scanning the entire item list once per
 *     item (O(n^2) inefficient loop) instead of being summed in a single pass
 *
 * It uses only classes available on the Integration Cell Groovy runtime: the SAP Message
 * API and the standard Java DOM parser (javax.xml.parsers). It does NOT use XmlSlurper,
 * which is not on this runtime's classpath.
 */
def Message processData(Message message) {

    // Whole payload held in memory as a String, then parsed into a full DOM tree (no streaming).
    String body = message.getBody(String)
    def factory = DocumentBuilderFactory.newInstance()
    factory.setNamespaceAware(true)
    def builder = factory.newDocumentBuilder()
    def doc = builder.parse(new ByteArrayInputStream(body.getBytes("UTF-8")))
    doc.documentElement.normalize()

    // Anti-pattern 1: build the report by String concatenation in a loop.
    String report = "SalesOrderNumber,CustomerName,MaterialDescription,Quantity,UnitPrice,Currency,TaxAmount\n"

    // Anti-pattern 2: also keep a second full copy of every row, plus a Map per item.
    List<String> allRows = new ArrayList<String>()
    List<Map> allItems = new ArrayList<Map>()

    // Anti-pattern 3: retain a bulky snapshot of the whole report-so-far on every line,
    // so the heap keeps a growing chain of ever-larger String copies that are never released.
    List<String> reportSnapshots = new ArrayList<String>()

    NodeList orders = doc.getElementsByTagName("Order")
    for (int o = 0; o < orders.getLength(); o++) {
        Element order  = (Element) orders.item(o)
        Element header = (Element) order.getElementsByTagName("Header").item(0)
        String soNumber = text(header, "SalesOrderNumber")
        String customer = text(header, "CustomerName")

        NodeList items = order.getElementsByTagName("Item")
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i)
            Map row = [
                salesOrderNumber   : soNumber,
                customerName       : customer,
                materialDescription: text(item, "MaterialDescription"),
                quantity           : text(item, "Quantity"),
                unitPrice          : text(item, "UnitPrice"),
                currency           : text(item, "Currency"),
                taxAmount          : text(item, "TaxAmount")
            ]
            allItems.add(row)

            String line = row.salesOrderNumber + "," + row.customerName + "," +
                          row.materialDescription + "," + row.quantity + "," +
                          row.unitPrice + "," + row.currency + "," + row.taxAmount
            report = report + line + "\n"   // reallocates the whole String every iteration
            allRows.add(line)

            // Anti-pattern 3 (cont.): keep a full copy of the report as it grows, line by line.
            // Each retained snapshot is bigger than the last, so overall retained memory grows
            // quadratically with the number of items.
            reportSnapshots.add(new String(report))
        }
    }

    // Inefficient loop: recompute the grand total of every tax amount by re-scanning the
    // entire item list once per item (O(n^2)) instead of summing once in the loop above.
    double grandTotalTax = 0.0d
    for (int a = 0; a < allItems.size(); a++) {
        double runningForA = 0.0d
        for (int b = 0; b < allItems.size(); b++) {
            Map other = allItems.get(b)
            String tax = String.valueOf(other.taxAmount)
            if (tax != null && tax.length() > 0) {
                runningForA = runningForA + Double.parseDouble(tax)
            }
        }
        // only the last pass actually matters, but we redo the full scan every iteration
        grandTotalTax = runningForA
    }

    message.setBody(report)
    message.setHeader("X-Order-Count", String.valueOf(orders.getLength()))
    message.setHeader("X-Item-Count",  String.valueOf(allItems.size()))
    message.setHeader("X-Grand-Total-Tax", String.valueOf(grandTotalTax))
    message.setHeader("X-Report-Snapshots", String.valueOf(reportSnapshots.size()))
    return message
}

private static String text(Element parent, String tag) {
    NodeList nl = parent.getElementsByTagName(tag)
    if (nl == null || nl.getLength() == 0) return ""
    return nl.item(0).getTextContent()
}
