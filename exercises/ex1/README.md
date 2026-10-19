# Exercise 1 — Create and Govern MCP Servers

In this exercise, you will create **MCP (Model Context Protocol) servers** in SAP Integration Suite from two different sources, deploy them to the Integration Cell runtime, inspect and consume them with the **MCP Inspector**, and monitor their usage.

## Overview

An MCP server is an artifact you create in SAP Integration Suite to expose your enterprise APIs and data to AI agents as callable **tools** — through a single, governed endpoint on which you can enforce security and traffic policies, keep an audit trail, and manage the full lifecycle. In this exercise you build one MCP server from an **HTTP endpoint + OpenAPI specification** (Autobahn) and one from a **remote MCP server** (OpenSwissData), then connect an MCP client to prove the governed gateway works end to end.

> [!NOTE]
> Work in your own **`IN263 User 0XX`** package throughout (see [Getting Started](../ex0/README.md)). You can reuse the same package for both MCP servers in this exercise.

> **References**
> - [MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/model-context-protocol-mcp)
> - [Creating an MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/creating-mcp-server)
> - [How Model Context Protocol (MCP) Server Enables AI Integration](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/how-mcp-server-enables-ai-integration)

## Prerequisites

- **API Management** capability and the **Integration Cell** runtime activated (pre-configured for the session — see [Getting Started](../ex0/README.md)).
- The `PI_Integration_Developer` role collection assigned to your user (pre-assigned — see [Getting Started](../ex0/README.md)).
- Your own package named <b>IN263 User 0XX</b> *(replace XX with your assigned participant number)*. See [Creating an Integration Package](https://help.sap.com/docs/integration-suite/sap-integration-suite/creating-integration-package).

> [!IMPORTANT]
> General rules that apply to every MCP server you create:
> - Any OpenAPI specification used as a source must be version **3.0.0–3.0.3** (Swagger 2.0 and earlier are not supported).
> - MCP servers can only be **deployed and managed on the Integration Cell runtime profile**.
> - The **MCP Path** must not be empty or set to only `/`. Keep your tool selection within the per-server limit shown in the wizard.
> - After you choose **Deploy**, **wait for the Runtime Status to become `STARTED`** before navigating away or reloading — leaving the page while a deployment is still submitting can silently abort it.

---

## Exercise 1.1 — Create an MCP Server from an HTTP Endpoint (Autobahn)

In this sub-exercise you will expose an external REST service — the public **Autobahn** road/traffic API — as an MCP server by providing its OpenAPI specification.

> **Reference:** [Create an MCP Server from an HTTP Endpoint](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-mcp-server-from-http-endpoint)

> [!NOTE]
> To connect through a destination (recommended), an **HTTP destination** with the label `IntegrationCell.Include = true` must exist in the SAP BTP Cockpit (**Connectivity → Destinations**). Only destinations with **Proxy Type** `Internet` or `OnPremise` are supported. See [Create HTTP Destinations](https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/create-http-destinations).

1. Log on to SAP Integration Suite and choose **Design → Integrations and APIs**.

2. Select your <b>IN263 User 0XX</b> integration package and choose **Edit**.

   <br>![Open your package and choose Edit](/exercises/ex1/images/ex1_01_edit_package.png)

3. On the package details page, choose the **Artifacts** tab, and under **Add** select **MCP Server**. The **Create MCP Server** dialog opens.

   <br>![Add → MCP Server](/exercises/ex1/images/ex1_02_add_mcp_server.png)

4. Select **HTTP Endpoint with OpenAPI Specification** as the source type and click **Next**.

   <br>![Select the HTTP Endpoint source type](/exercises/ex1/images/ex1_03_source_type_http.png)

5. In the **Provide MCP Details** step, choose **Method: Upload** and upload the Autobahn OpenAPI specification — the file [`Autobahn.json`](assets/Autobahn.json) from this exercise's `assets` folder. Then fill in the remaining fields:

   | Field | Value |
   |---|---|
   | **Method** | `Upload` |
   | **File Name** | Upload the `Autobahn.json` OpenAPI specification file |
   | **Source** | `URL` |
   | **URL** | `https://verkehr.autobahn.de/o/autobahn` |
   | **Name** | <b>Autobahn_MCP_0XX</b> *(replace XX with your assigned participant number)*. Alphanumeric and `_` only. |
   | **ID** | Auto-populated |
   | **MCP Path** | <b>/autobahn_mcp_0XX</b> *(replace XX with your assigned participant number)*. Must not be empty or just `/`. |
   | **Runtime Profile** | `Integration Cell` |
   | **Virtual Host** | Review the pre-filled virtual host URL (combined with the MCP Path to form the public endpoint). |

   <br>![Provide MCP Details — upload the Autobahn OpenAPI spec](/exercises/ex1/images/ex1_04_mcp_details_http.png)

   Click **Next**.

6. In the **Create Tools** step, select the following four tools, then choose **Create** to finalize the MCP server — it opens in the editor view:

   | Tool (path) | What it returns |
   |---|---|
   | `/` | List of available Autobahn roads |
   | `/{roadId}/services/roadworks` | Current roadworks on a road |
   | `/{roadId}/services/warning` | Current traffic warnings on a road |
   | `/{roadId}/services/closure` | Current closures on a road |

   <br>![Create Tools — select the four Autobahn tools](/exercises/ex1/images/ex1_05_create_tools_http.png)

7. In the editor, open the **Policies** tab and edit the **Authentication** policy so the MCP server accepts **Basic authentication only** (remove the OAuth/OData-based authentication). This keeps consumption in [Exercise 1.3](#exercise-13--inspect-and-consume-the-mcp-servers-with-mcp-inspector-optional) simple — a username and password instead of an OAuth token flow.

   <br>![Policies tab — set Authentication to Basic only](/exercises/ex1/images/ex1_06_policies_basic_auth.png)

8. Choose **Save** (top right), then choose **Deploy** (top right). See [Deploy an MCP Server](#deploy-an-mcp-server) below, and wait for **Runtime Status: STARTED**.

---

## Exercise 1.2 — Create an MCP Server Using a Remote MCP Server as the Source

In this sub-exercise you will create an MCP server that **proxies a remote (third-party) MCP server** — the public **OpenSwissData** MCP server — so that SAP Integration Suite acts as a managed governance layer in front of it, applying security and traffic policies while leaving the remote server unchanged.

> **Reference:** [Create an MCP Server Using Remote MCP Server as the Source](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-mcp-server-using-remote-mcp-server-as-source)

<details>
<summary><strong>📖 About the remote server: OpenSwissData</strong></summary>

[OpenSwissData](https://www.openswissdata.com/) exposes an MCP server at `https://mcp.openswissdata.com/jsonrpc` that serves normalised Swiss federal reference data:
- the Swiss customs tariff (**TARES**, 8-digit tariff numbers with duties and restrictions),
- the **FINMA** register of supervised institutions and the FINMA warnings list,
- correspondences between the **NOGA / NACE / ISIC** activity classifications.

It is free with no key or sign-up (rate-limited to 100 calls per hour per IP) and requires **no authentication**, which makes it a convenient public remote MCP server to use for this exercise.
</details>

> [!NOTE]
> The destination this sub-exercise needs — **`openswissdata`** (HTTP, Proxy Type `Internet`, `NoAuthentication`, URL `https://mcp.openswissdata.com/jsonrpc`, labels `IntegrationCell.Include = true` and `IntegrationCell.Provides = mcp`) — is **already set up for you**. See [Getting Started → The OpenSwissData destination](../ex0/README.md#the-openswissdata-destination-for-exercise-12). The remote server must support the **Streamable HTTP** transport (OpenSwissData does); only destinations with **Proxy Type** `Internet` can be used as a remote MCP source.

1. Log on to SAP Integration Suite and choose **Design → Integrations and APIs**.

2. Select your <b>IN263 User 0XX</b> integration package and choose **Edit**.

3. On the package details page, choose the **Artifacts** tab, and under **Add** select **MCP Server**. The **Create MCP Server** dialog opens.

4. Select **Remote MCP Servers** as the source type and click **Next**.

   <br>![Select the Remote MCP Servers source type](/exercises/ex1/images/ex1_07_source_type_remote.png)

5. Select the **`openswissdata`** destination, verify the following, and click **Next**:

   | Field | Value |
   |---|---|
   | **Name** | `openswissdata` |
   | **URL** | `https://mcp.openswissdata.com/jsonrpc` |
   | **Authentication** | `NoAuthentication` |

   <br>![Select the openswissdata destination](/exercises/ex1/images/ex1_08_select_destination.png)

6. In the **Provide MCP Details** step, fill in the fields:

   | Field | Value |
   |---|---|
   | **Name** | <b>OpenSwissData_MCP_0XX</b> *(replace XX with your assigned participant number)*. Alphanumeric and `_` only. |
   | **ID** | Auto-populated |
   | **Destination** | `openswissdata` *(selected in the previous step; used to connect to the remote server and, by default, for runtime execution)* |
   | **Relative Path** | *Optional.* Leave empty — the destination URL already points at the JSON-RPC endpoint. |
   | **MCP Path** | <b>/openswissdata_mcp_0XX</b> *(replace XX with your assigned participant number)*. Must not be empty or just `/`. |
   | **Version** | `1.0.0` |
   | **Runtime Profile** | `Integration Cell` |
   | **Virtual Host** | Review the pre-filled virtual host URL. |

   <br>![Provide MCP Details for the remote MCP server](/exercises/ex1/images/ex1_09_mcp_details_remote.png)

7. In the **Create Tools** step, select the checkboxes for the two tools this exercise uses, then choose **Add** to finalize and create the MCP server:

   | Tool | What it does |
   |---|---|
   | `tariff_lookup` | Swiss customs tariff (TARES) line by tariff number |
   | `company_check` | Swiss company lookup by UID (`CHE-xxx.xxx.xxx`) |

   <br>![Select the OpenSwissData tools](/exercises/ex1/images/ex1_10_create_tools_remote.png)

8. In the editor, open the **Policies** tab and edit the **Authentication** policy so the MCP server accepts **Basic authentication only** (remove the OAuth/OData-based authentication). This keeps consumption in [Exercise 1.3](#exercise-13--inspect-and-consume-the-mcp-servers-with-mcp-inspector-optional) simple.

   <br>![Policies tab — set Authentication to Basic only](/exercises/ex1/images/ex1_11_policies_basic_auth_remote.png)

9. Choose **Save** (top right), then choose **Deploy** (top right). See [Deploy an MCP Server](#deploy-an-mcp-server) below, and wait for **Runtime Status: STARTED**. You will inspect and invoke its proxied tools in [Exercise 1.3](#exercise-13--inspect-and-consume-the-mcp-servers-with-mcp-inspector-optional) — for example, `tariff_lookup` with `hs8 = 8471.3000`.

---

### Deploy an MCP Server

Both MCP servers you create in 1.1 and 1.2 are deployed the same way: from the artifact editor, choose **Save** (top right) and then **Deploy** (top right).

> **Reference:** [Deploy an MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/deploy-mcp-server)

1. With the MCP server open in the editor, choose **Save** (top right), then **Deploy** (top right).

   > Alternatively, from **Design → Integrations and APIs**, open the package, choose the **Action** icon next to the MCP server artifact, and select **Deploy**.

2. In the **Confirmation** dialog, verify the selected **Runtime Profile** (the design-time one is preselected) and, optionally, a **Virtual Host**, then confirm.

   <br>![Deploy confirmation dialog](/exercises/ex1/images/ex1_12_deploy.png)

3. Wait until the artifact's header shows **`Deployed on [date], Runtime Status: STARTED`**. The MCP server is now available for consumption.

   <br>![Runtime Status: STARTED](/exercises/ex1/images/ex1_13_deployed_started.png)

   > [!IMPORTANT]
   > Only proceed once the status shows **STARTED**. If it still shows *Not Deployed*, wait a moment and refresh the page — do not navigate away while the deployment is still submitting.

   > [!NOTE]
   > If the MCP server invokes an HTTPS backend whose certificate is not trusted by the runtime keystore, invocations fail with an internal server error (visible in **Manage Processing Logs**). See [Uploading a Keystore](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/uploading-keystore).

---

## Exercise 1.3 — Inspect and Consume the MCP Servers with MCP Inspector (Optional)

In this optional sub-exercise you will connect the **MCP Inspector** — the official developer tool for the Model Context Protocol — to one of your deployed MCP servers, list its tools, and invoke one to confirm the end-to-end flow through the governed Integration Cell endpoint.

> **References**
> - [Invoke the MCP Server Endpoint URL](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/invoke-mcp-server-endpoint-url)
> - [MCP Inspector (modelcontextprotocol/inspector)](https://github.com/modelcontextprotocol/inspector)

> [!NOTE]
> Prerequisites: an MCP server **deployed** with runtime status **STARTED** (from 1.1–1.2) whose **Authentication** policy is set to **Basic** (configured in 1.1 / 1.2), and **Node.js** installed locally (the Inspector runs via `npx` — no separate install needed).

### Step 1: Get the endpoint URL and your Basic credentials

1. Open your deployed MCP server and copy its **endpoint URL** — shown on the MCP server's **Overview** tab as the **MCP URL** (also available in **Monitor → Integrations and APIs → Manage Integration Content → Endpoints**, see [Exercise 1.4](#exercise-14--monitor-the-mcp-servers-usage)).

   <br>![Copy the MCP server endpoint URL](/exercises/ex1/images/ex1_14_mcp_url.png)

2. Because the server's **Authentication** policy is set to **Basic**, you authenticate with your workshop **username and password** — the same `User0XX` / password you logged in with (see [Getting Started](../ex0/README.md#accessing-the-system)). No Developer Hub subscription or OAuth token is needed.

### Step 2: Launch MCP Inspector

3. Open a terminal and run the Inspector. It starts a small local web app and opens it in your browser.

   - **macOS:** open **Terminal** (press `⌘ + Space`, type `Terminal`, press `Enter`).
   - **Windows:** open **Windows Terminal** or **Command Prompt** (press the `Windows` key, type `cmd`, press `Enter`).

   Then run:

   ```bash
   npx @modelcontextprotocol/inspector
   ```

   <br>![MCP Inspector running in the browser](/exercises/ex1/images/ex1_15_inspector_launch.png)

### Step 3: Connect and invoke a tool

4. In the Inspector UI, fill in the connection details:

   | Field | Value |
   |---|---|
   | **Transport Type** | `Streamable HTTP` |
   | **URL** | Your MCP server's **endpoint URL** (the MCP URL from Step 1) |

5. Under **Authentication**, add a request **header** so the Inspector sends your Basic credentials:

   | Field | Value |
   |---|---|
   | **Header Name** | `Authorization` |
   | **Value** | `Basic <base64(User0XX:password)>` |

   The value is the word `Basic`, a space, then the Base64 encoding of `User0XX:password`. You can generate it in the terminal:

   - **macOS / Linux:**
     ```bash
     printf 'User0XX:your-password' | base64
     ```
   - **Windows (PowerShell):**
     ```powershell
     [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("User0XX:your-password"))
     ```

   <br>![Configure the MCP Inspector connection with a Basic Authorization header](/exercises/ex1/images/ex1_16_inspector_connect.png)

6. Choose **Connect**. Once connected, the server shows **Connected** and the protocol handshake (initialize, tools/list) appears in the message log. Use **List Tools** / **List Resources** to see what the server exposes.

   <br>![MCP Inspector connected to the MCP server](/exercises/ex1/images/ex1_16b_inspector_connected.png)

7. Select a tool and **invoke** it with sample arguments — for example, the OpenSwissData `company_check` tool with `uid = CHE-109.815.753` — and confirm the Inspector shows the response returned through the Integration Cell endpoint.

   <br>![Invoke a tool and view the response](/exercises/ex1/images/ex1_17_inspector_invoke.png)

> [!TIP]
> If you have access to the SAP **Early Adopter Care (EAC)** program, you can instead consume the MCP server from **Joule Work Desktop** by adding it as a connector with the same endpoint URL and credentials. See [Invoke an MCP Server from Joule Work Desktop](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/invoke-mcp-server-from-joule-work-desktop).

---

## Exercise 1.4 — Monitor the MCP Servers' Usage

In this sub-exercise you will find where to check the deployment status, runtime logs, endpoints, and usage analytics of your MCP servers. If you didn't take the previous sub-execise (Optional), you can explore other MCP server's usage data deployed in the same tenant.

> **References**
> - [Monitor APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/monitor-apis)
> - [Analyze APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/analyze-apis-and-mcp-servers)

### Step 1: Check status, endpoints, and logs

1. Go to **Monitor → Integrations and APIs**, and under **Overview** select **Integration Cell** from the **Runtime** drop-down.

2. In the **Manage Integration Content** area, choose the tile that lists deployed artifacts. Each entry shows its **Name** (with artifact type) and **Status** (see [Runtime Status](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/runtime-status)).

   <br>![Deployed APIs and MCP servers in Manage Integration Content](/exercises/ex1/images/ex1_18_manage_integration_content.png)

3. Search for your MCP server by name or ID, or filter by **Status** / **Type** via **Table Settings**.

4. Select your MCP server. On the right, the **Integration Content Details** show:
   - Header info: **Deployed on**, **Deployed by**, **Version**, **ID**, **Package**, and actions **Restart / Undeploy / Download**.
   - **Endpoints** — the exposed URLs; use **Copy** to copy the endpoint (the one you used in [Exercise 1.3](#exercise-13--inspect-and-consume-the-mcp-servers-with-mcp-inspector-optional)).
   - **Status Details** — whether the artifact is deployed or undeployed.
   - **Log Configuration** — set the **Log Level** (None, Info, Error, Debug, Trace). Use **Trace**/**Debug** only in test environments; both expire automatically (Debug after 24 hours, Trace after ~10 minutes).

5. To investigate individual invocations and errors, use **Monitor Message Processing** and **Manage Processing Logs**.

### Step 2: Analyze usage trends

6. Choose **Analyze** from the left navigation to open the analytics dashboards. At the top, set **Runtime** to **Integration Cell** and **Artifact Type** to **MCP Server** so the view focuses on your MCP servers.

   <br>![Analyze — Runtime: Integration Cell, Artifact Type: MCP Server](/exercises/ex1/images/ex1_19_analyze.png)

7. Explore the KPI tiles (**Total MCP Calls**, **MCP Response Time**, **Total MCP Errors**) and the **MCP Charts** below them. Use the tabs (**Overview**, **Health**, **Usage**, **Anomalies**, **Predictions**) and the chart filters (**All MCP Servers** or **Select MCP Servers from the list**) to review usage, performance, and traffic for your deployed MCP servers. See [Analyze APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/analyze-apis-and-mcp-servers).

## Summary

You have now created and governed MCP servers in SAP Integration Suite. In doing so you have:

- Created an MCP server from an **HTTP endpoint + OpenAPI specification** (Autobahn).
- Created an MCP server from a **remote MCP server** (OpenSwissData), with Integration Suite as the governance layer in front of it.
- Set each server's **Authentication** policy to **Basic**, **Saved**, and **Deployed** both to the Integration Cell runtime (**Runtime Status: STARTED**).
- **Inspected and invoked** the tools with the MCP Inspector using Basic authentication.
- Located the **monitoring and analytics** for status, endpoints, logs, and usage.

Through this you experienced how SAP Integration Suite acts as a **governed gateway** between AI agents and your enterprise integrations.

Continue to - [Exercise 2 — AI-based Memory Profiling of an Existing iFlow](../ex2/README.md)
