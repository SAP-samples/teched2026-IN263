# Exercise 1 - Create and Govern MCP Servers

In this exercise, you will create **MCP (Model Context Protocol) servers** in SAP Integration Suite from three different sources, deploy them, consume them from an AI client, and monitor their usage. An MCP server is an artifact you create in SAP Integration Suite to expose your enterprise APIs and data to AI agents as callable **tools** — through a single, governed endpoint on which you can enforce security and traffic policies, keep an audit trail, and manage the full lifecycle.

> **References**
> - [MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/model-context-protocol-mcp)
> - [Creating an MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/creating-mcp-server)
> - [How Model Context Protocol (MCP) Server Enables AI Integration](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/how-mcp-server-enables-ai-integration)

## Prerequisites

Common prerequisites for all sub-exercises (see also [Getting Started](../ex0/README.md)):

- **API Management** capability activated. See [Activate and Configure the API Management Capability](https://help.sap.com/docs/integration-suite/sap-integration-suite/activate-and-configure-api-management-capability).
- **Integration Cell** runtime activated. See [Activate Integration Cell](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/activate-integration-cell).
- The `PI_Integration_Developer` role collection assigned to your user.
- An **integration package** to hold your artifacts. See [Creating an Integration Package](https://help.sap.com/docs/integration-suite/sap-integration-suite/creating-integration-package). You can reuse one package for all three MCP servers in this exercise.

> **General notes that apply to every MCP server you create:**
> - The OpenAPI specification used as a source must be a version between **3.0.0 and 3.0.3**. Swagger 2.0 and earlier are not supported. (When you import an OData `$metadata` (EDMX) file into an API artifact, Integration Suite converts it to OpenAPI 3.0.0 automatically.)
> - MCP servers can only be **deployed and managed on the Integration Cell runtime profile**.
> - There is a per-server limit on the number of tools you can expose. Check the current limit in the tool-selection step of the wizard and keep your selection at or below it.
> - The **MCP Path** (base path) must not be empty or set to only `/`.
> - After you choose **Deploy** for an API artifact or MCP server, **wait for the Runtime Status to change to `STARTED`** before navigating away or reloading the page. Leaving the page while a deployment is still being submitted can silently abort it, leaving the artifact in `Not Deployed`.

---

## Exercise 1.1 Create an MCP Server from an API Artifact (S/4HANA Cloud)

After completing these steps you will have exposed a deployed API artifact (here, the S/4HANA Cloud **Sales Order** OData API `API_SALES_ORDER_SRV`) as an MCP server, so that AI agents can discover and invoke its operations as tools.

This sub-exercise has two parts: first you **create and deploy the API artifact** from an S/4HANA Cloud OData service (1.1.1), then you **create and deploy the MCP server** from that API artifact (1.1.2).

> **References**
> - [Create an API Artifact Using a Destination](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-api-artifact-by-referring-to-destination)
> - [Create an MCP Server from an API Artifact](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-mcp-server-from-api-artifact)

**Additional prerequisites for this method:**

- Only **OData and REST** API artifacts **deployed on the Integration Cell runtime** can be exposed as an MCP server.
- Access to an S/4HANA Cloud OData service (here `API_SALES_ORDER_SRV`), reachable from SAP BTP, with a communication user/arrangement that permits the operations you want to expose. See [SAP Business Accelerator Hub](https://api.sap.com/) for the API and its metadata.
- A **BTP destination** to the S/4HANA Cloud system, created in the **same subaccount that hosts your SAP Integration Suite / Integration Cell**, with the label `IntegrationCell.Include = true` so the Integration Cell can discover it. (This is the same kind of destination used in Exercise 1.2; see the destination steps there.)

> **Note on authentication:** The official documentation states that only APIs with **OAuth-based authentication** can be used to create an MCP server. In practice, depending on your tenant edition and release, an API artifact backed by a **Basic authentication** destination may also work. If MCP-server creation rejects your API, reconfigure the destination to use OAuth 2.0 against the S/4HANA Cloud token endpoint (the SAP_COM scenario on the S/4 side supports OAuth) and redeploy the API.

> **Note on discovering a full API catalog ("Discoverable Systems"):** The Add API wizard also offers a *Discoverable Systems* tab that lets you browse an entire S/4HANA Cloud API catalog instead of a single endpoint. That tab is populated from a **BTP global-account formation (system landscape)** that groups your Integration Suite and S/4HANA Cloud systems — it is **not** the in-app *Settings → Integrations → System* tab (which is only for SAP Process Orchestration systems). Setting up a formation requires global-account administrator rights and is out of scope here; this exercise uses the simpler **destination** route, which produces the same kind of deployable API artifact. See [Create an API Artifact Using a Discoverable System](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-api-artifact-by-referring-to-discoverable-system) if your landscape already has a formation.

### Exercise 1.1.1 Create and Deploy the API Artifact

1. Log on to SAP Integration Suite and choose **Design → Integrations and APIs**.

2. Open (or create) your integration package and choose **Edit**. On the **Artifacts** tab, choose **Add → API**.

3. In the **Add API** wizard:
   - **Select Runtime Profile:** choose **Integration Cell** (not Cloud Integration), then **Next**. (Selecting Integration Cell removes the "Select API Type" step.)
   - **Select a Method:** choose **API Provider**, then **Next**.
   - **Select API Provider:** open the **Destinations** tab. Your labeled destination (e.g. `IN263_S4HC_SalesOrder`) appears here because it carries `IntegrationCell.Include = true`. Select it and choose **Next**.

4. In **Provide API Details**, fill in:

   | Field | Value |
   | --- | --- |
   | **Name / ID** | e.g. `IN263_SalesOrder_API` |
   | **API Provider** | Pre-filled with the selected destination |
   | **Relative URL** | The OData service path, e.g. `/sap/opu/odata/sap/API_SALES_ORDER_SRV` |
   | **Service Type** | **ODATA** |
   | **API Base Path** | Defaults from the name, e.g. `/IN263_SalesOrder_API` |
   | **Runtime Profile / Virtual Host** | **Integration Cell** / the pre-filled Integration Cell virtual host |

   > **Tip (avoid a doubled path):** The effective backend call is the destination's base URL plus this **Relative URL**. If the destination URL already includes the full service path, keep the destination at the **host only** (e.g. `https://my-s4-system.s4hana.cloud.sap`) and put the service path in the Relative URL, so you don't end up calling `.../API_SALES_ORDER_SRV/sap/opu/odata/sap/API_SALES_ORDER_SRV`.

5. Choose **Next**. The optional **Generate API Specification** step (AI) appears — you can skip it. Choose **Add** to create the API artifact. It opens in the API editor with **Status: Not Deployed**.

6. The new API has **no resources** yet (it is just a proxy to the destination). To populate operations, open the **Resources** tab, choose **Edit**, then **Import**, and upload the OData service's **`$metadata` (EDMX)** file. Integration Suite generates all entity sets as resources (GET/POST/PUT/DELETE and navigation properties) and converts the artifact to OpenAPI 3.0.0. Review and **Save**.

   > You can obtain the EDMX from the service's `$metadata` endpoint (`<service-url>/$metadata`) in an authenticated session, or download it from SAP Business Accelerator Hub.

7. Choose **Deploy**, confirm the **Integration Cell** runtime profile and virtual host, and choose **Deploy**. **Wait** until the header shows **Status: Deployed … Runtime Status: STARTED** (do not reload/navigate while it is submitting).

### Exercise 1.1.2 Create and Deploy the MCP Server

1. Go back to your integration package (**Design → Integrations and APIs → your package**) and choose **Edit**.

2. On the **Artifacts** tab, choose **Add → MCP Server**. The **Add MCP Server** dialog opens.

3. **Select Source Type:** choose **API** — *"Create an MCP server from an API artifact deployed on Integration Cell"* — and choose **Next**.

4. In **Provide MCP Details**, choose the value-help icon next to the **API** field and select your deployed API (e.g. `IN263_SalesOrder_API`). Then fill in:

   | Field | Value |
   | --- | --- |
   | **API** | The deployed Integration Cell API artifact |
   | **Name / ID** | e.g. `IN263_SalesOrder_MCP` (alphanumeric and `_`) |
   | **MCP Path** | The path prefix under which the server is exposed, e.g. `/SalesOrderMCP`. Must not be empty or just `/`. |
   | **Version** | Optional, e.g. `1.0.0` |
   | **Runtime Profile** | **Integration Cell** |
   | **Virtual Host** | The pre-filled Integration Cell virtual host |

5. Choose **Next** to proceed to **Create Tools**. Select the API resources (operations) you want to expose as MCP tools by checking each one; keep the selection within the per-server tool limit shown.

6. Choose **Add** to create the MCP server. It opens in the editor view.

7. (Optional) Choose **Edit** to refine the configuration. Use the **MCP Configuration** tab to adjust **Source, Tools, Resources, and Prompts**, and the **Policies** tab to configure traffic-management and security policies. See [Configure an MCP Server Created from an API Artifact](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/configure-mcp-server-created-from-api-artifact).

8. **Deploy** the MCP server (see [Deploy an MCP Server](#deploy-an-mcp-server) below) and wait for **Runtime Status: STARTED**. The **Overview** tab then shows the public **MCP URL** (virtual host + MCP Path, e.g. `https://<virtual-host>/SalesOrderMCP`) — this is the endpoint AI clients connect to in Exercise 1.4.

---

## Exercise 1.2 Create an MCP Server from an HTTP Endpoint (Autobahn)

After completing these steps you will have exposed an external REST-based service (here referred to as **Autobahn** — for example, a public road/traffic HTTP API) as an MCP server by providing its OpenAPI specification.

> **Reference:** [Create an MCP Server from an HTTP Endpoint](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-mcp-server-from-http-endpoint)

**Additional prerequisites for this method:**

- To connect through a destination (recommended), set up an **HTTP destination** in SAP BTP Cockpit (**Connectivity → Destinations**). See [Create HTTP Destinations](https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/create-http-destinations). In the **Additional Properties → Labels** section, add the label `IntegrationCell.Include` with the value `true`.
- Only destinations with **Proxy Type** `Internet` or `OnPremise` are supported (see the supported authentication mechanisms in the reference page).

**Steps:**

1. Log on to SAP Integration Suite.

2. From the left navigation pane, choose **Design → Integrations and APIs**.

3. Select your integration package and choose **Edit**.

4. On the package details page, choose **Artifacts**, and under **Add**, select **MCP Server**. The **Create MCP Server** dialog opens.

5. Select **HTTP Endpoint with OpenAPI Specification** as the source type and choose **Next**.

6. In the **Provide MCP Details** step, choose **Create** (to define the source) or **Upload** (to upload an OpenAPI JSON file), then provide the details. Select the source type **URL** or **Destination**:

   **For Source Type = URL:**

   | Field | Value |
   | --- | --- |
   | **URL** | The target HTTPS endpoint, e.g. the Autobahn API base URL `https://verkehr.autobahn.de/o/autobahn`. |
   | **Name** | An intuitive name, e.g. `Autobahn_Traffic`. Alphanumeric and `_` only. |
   | **ID** | Identifier shown on the home screen. |
   | **MCP Path** | Path prefix, e.g. `/Autobahn`. Must not be empty or just `/`. |
   | **Runtime Profile** | The **Integration Cell** runtime node. |
   | **Virtual Host** | Review the virtual host URL (combined with the base path for the public endpoint). |

   **For Source Type = Destination:**

   | Field | Value |
   | --- | --- |
   | **Destination** | Choose the icon, select the HTTP destination you created, and choose **Add**. The destination holds the base URL. |
   | **Relative URL** | The resource path appended to the destination base URL, e.g. `/A1/services`. |
   | **Name / ID / MCP Path / Runtime Profile / Virtual Host** | As above. |

7. **If the OpenAPI Specification Generator is enabled** (in **Settings → Artificial Intelligence**), a **Next** button takes you to the **Generate API Specification** step. There you can either generate an OpenAPI specification with AI, or skip generation and choose **Create** directly. **If it is not enabled**, a **Create** button is available directly in the Provide MCP Details step.

   > **Note:** AI-assisted OpenAPI generation requires the Premium or Enhanced Edition (free promotion until the January 2027 release).

8. In the **Create Tools** step, select the API resources you want to expose as MCP tools (up to 30). If no resources are available, you can skip this step.

9. Choose **Create** to finalize the MCP server. It opens in the editor view.

10. (Optional) Choose **Edit** to adjust **Source, Tools, Resources, Prompts** (MCP Configuration tab) and **Policies**. See [Configure an MCP Server Created from an HTTP Endpoint](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/configure-mcp-server-created-from-http-endpoint).

11. **Deploy** the MCP server (see [Deploy an MCP Server](#deploy-an-mcp-server) below).

---

## Exercise 1.3 Create an MCP Server Using a Remote MCP Server as the Source (Federation)

After completing these steps you will have created an MCP server that **proxies a remote (third-party) MCP server**, so that SAP Integration Suite acts as a managed governance layer in front of it — applying security and traffic policies while leaving the remote server unchanged.

> **Reference:** [Create an MCP Server Using Remote MCP Server as the Source](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-mcp-server-using-remote-mcp-server-as-source)

**Additional prerequisites for this method:**

- Set up a **destination** in SAP BTP Cockpit pointing to the remote MCP server. See [Configure a Destination to Connect an MCP Server to a Remote MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/configure-destination-to-connect-mcp-server-to-remote-mcp-server).
- Only destinations with **Proxy Type** `Internet` are currently supported (authentication: OAuth2ClientCredentials, NoAuthentication, BasicAuthentication, OAuth2Password, ClientCertificate).
- The remote MCP server must support the **Streamable HTTP transport** — only servers exposed through Streamable HTTP can be proxified.

**Steps:**

1. Log on to SAP Integration Suite.

2. From the left navigation pane, choose **Design → Integrations and APIs**.

3. Select your integration package and choose **Edit**.

4. On the package details page, choose **Artifacts**, and under **Add**, select **MCP Server**. The **Create MCP Server** dialog opens.

5. Select **Remote MCP Servers** as the source type and choose **Next**.

6. Select the destination that points to the remote MCP server. Verify the following and choose **Next**:
   - **Name** — destination identifier
   - **URL** — remote MCP server endpoint
   - **Authentication** — OAuth2 / Basic / None / ClientCertificate

7. In the **Provide MCP Details** step, fill in the details:

   | Field | Value |
   | --- | --- |
   | **Name** | An intuitive name, e.g. `Partner_MCP_Proxy`. Alphanumeric and `_` only. |
   | **ID** | Identifier shown on the home screen. |
   | **Destination** | The destination selected in the previous step (used to connect to the remote server; by default also used for runtime execution). |
   | **Relative Path** | *Optional.* The relative path of the remote MCP discovery/runtime endpoint, combined with the destination URL. |
   | **MCP Path** | Path prefix, e.g. `/PartnerTools`. Must not be empty or just `/`. |
   | **Version** | Optional. |
   | **Runtime Profile** | The **Integration Cell** runtime node. |
   | **Virtual Host** | Review the virtual host URL. |

8. In the **Create Tools** step, select the checkboxes next to the tools you want to expose from the remote MCP server (up to 30).

9. Choose **Add** to finalize and create the MCP server.

10. (Optional) Choose **Edit** to adjust the configuration and policies. If the remote server needs different authentication for tool/resource/prompt execution, you can configure a separate runtime destination in the **Source** tab. See [Configure an MCP Server Created by Connecting to a Remote MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/configure-mcp-server-created-by-connecting-to-remote-mcp-server).

11. **Deploy** the MCP server (see below).

---

### Deploy an MCP Server

Every MCP server you create in 1.1–1.3 must be deployed to the Integration Cell runtime before it can be consumed.

> **Reference:** [Deploy an MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/deploy-mcp-server)

1. In **Design → Integrations and APIs**, open the package that contains the MCP server.

2. Choose the **Action** icon next to the MCP server artifact and select **Deploy** (alternatively, open the artifact's details page and choose **Deploy**).

3. In the **Confirmation** dialog, verify the selected **Runtime Profile** (the one chosen at design time is preselected). Optionally select a **Virtual Host** from the drop-down.

4. Confirm the deployment. Once deployed, the MCP server becomes available for consumption.

   > **Note:** If the MCP server invokes an HTTPS backend, the backend certificate must be trusted by the runtime keystore. If it is missing, invocations fail with an internal server error (visible in **Manage Processing Logs**). See [Uploading a Keystore](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/uploading-keystore).

---

## Exercise 1.4 Configure and Use the MCP Servers in Joule Work Desktop

After completing these steps you will have connected an AI client (**Joule Work Desktop**) to one of your deployed MCP servers so that Joule can invoke its tools during conversations.

> **References**
> - [Invoke the MCP Server Endpoint URL](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/invoke-mcp-server-endpoint-url)
> - [Invoke an MCP Server Artifact by Obtaining Credentials via Developer Hub](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/invoke-mcp-server-artifact-by-obtaining-credentials-via-developer-hub)
> - [Invoke an MCP Server from Joule Work Desktop](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/invoke-mcp-server-from-joule-work-desktop)

> **Note:** Joule Work Desktop is part of an SAP **Early Adopter Care (EAC)** program and is not available to users outside that program. If you do not have access, you can instead invoke the MCP endpoint from any REST/MCP client using the credentials obtained below.

**Additional prerequisites for this method:**

- An MCP server **created and deployed** on the Integration Cell runtime (from 1.1–1.3).
- **Joule Work Desktop** installed on your machine. See [Setting Up Joule Work Desktop](https://help.sap.com/docs/joule).
- Your user is registered in the authorization server (for example, SAP Cloud Identity Services – Identity Authentication), because Joule Work Desktop triggers a user-token fetch flow when connecting. See [Accessing an MCP Server Using an IdP User Token](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/consume-mcp-server-using-idp-user-token).

**Step A - Obtain the endpoint URL and credentials (via Developer Hub, recommended):**

1. Make sure a content administrator has added your MCP server artifact to a **product** and **published** it on Developer Hub. See [Discover and Publish APIs from SAP Integration Suite](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/discover-and-publish-apis-from-integration-suite).

2. Log on to **Developer Hub** (your user needs the `AuthGroup.API.ApplicationDeveloper` role collection).

3. **Subscribe** to the product that contains your MCP server artifact. See [Subscribe to a Product to Consume APIs and Events in an Application](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/subscribe-to-product).

4. Once the subscription is created, open the **Overview** tab of your application and copy the **Key** (client ID), **Secret** (client secret), and **Token URL**. These are used to generate the OAuth (JWT) token required to call the MCP server.

5. Note the MCP server **endpoint URL** exposed by your Integration Cell runtime (also visible in **Monitor → Integrations and APIs → Manage Integration Content → Endpoints** — see Exercise 1.5).

   > Obtaining credentials via the **Process Integration Runtime** is also possible but is considered an anti-pattern; prefer Developer Hub.

**Step B - Add the MCP server as a connector in Joule Work Desktop:**

6. In Joule Work Desktop, add a connector following [Adding a Connector](https://help.sap.com/docs/joule) in the Joule Work Desktop guide.

7. When prompted, enter the **MCP server endpoint URL** from Step A.

8. Provide the authentication details:
   - For **XSUAA** authentication, both **Client ID** and **Client Secret** are required.
   - For **external** authentication methods (for example, Okta, IAS), **Client ID** is required and **Client Secret** is optional.

9. Save the connector. The MCP server's tools are now available in Joule Work Desktop and can be invoked by Joule during conversations.

10. Test it: ask Joule a question that should trigger one of your tools (for example, "look up business partner 1000000" against the S/4HANA Cloud MCP server from 1.1) and confirm that Joule calls the tool and returns the result.

---

## Exercise 1.5 Monitor the MCP Servers' Usage

After completing these steps you will know where to check the deployment status, runtime logs, endpoints, and usage analytics of your MCP servers.

> **References**
> - [Monitor APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/monitor-apis)
> - [Analyze APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/analyze-apis-and-mcp-servers)

**Step A - Check status, endpoints, and logs:**

1. In SAP Integration Suite, go to **Monitor → Integrations and APIs**, and under **Overview** select **Integration Cell** from the **Runtime** drop-down.

2. In the **Manage Integration Content** area, choose the tile that lists deployed artifacts. The list of deployed APIs and MCP servers appears. Each entry shows its **Name** (with artifact type) and **Status** (see [Runtime Status](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/runtime-status)).

3. Search for your MCP server by name or ID, or filter by **Status** or **Type** using **Table Settings**.

4. Select your MCP server. On the right, the **Integration Content Details** show:
   - Header info: **Deployed on**, **Deployed by**, **Version**, **ID**, **Package**, and actions **Restart / Undeploy / Download**.
   - **Endpoints** — the exposed URLs; use the **Copy** button to copy the endpoint (the one you used in Exercise 1.4).
   - **Status Details** — whether the artifact is deployed or undeployed.
   - **Log Configuration** — set the **Log Level** (None, Info, Error, Debug, Trace) to control the detail recorded in the message processing log. Use **Trace**/**Debug** only in test environments; both expire automatically (Debug after 24 hours, Trace after ~10 minutes).

5. To investigate individual invocations and errors, use **Monitor Message Processing** and **Manage Processing Logs**.

**Step B - Analyze usage trends:**

6. Open the **Analyze** / analytics dashboards for APIs and MCP servers to review usage, performance, and traffic. See [Analyze APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/analyze-apis-and-mcp-servers). If **Conversational Analytics** is enabled, you can query usage and performance in natural language via the AI Assistant. See [Analyze APIs with AI Assistance](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/analyze-apis-with-ai-assistance).

## Summary

You have created MCP servers from an API artifact, an HTTP endpoint, and a remote MCP server (federation); deployed them to the Integration Cell runtime; obtained credentials and consumed one from Joule Work Desktop; and monitored their status, endpoints, logs, and usage. In doing so you experienced how SAP Integration Suite acts as a **governed gateway** between AI agents and your enterprise integrations.

Continue to - [Exercise 2 - AI-based Memory Profiling of an Existing iFlow](../ex2/README.md)
