# Exercise 1 — Create and Govern MCP Servers

In this exercise, you will create **MCP (Model Context Protocol) servers** in SAP Integration Suite from two different sources, deploy them to the Integration Cell runtime, inspect and consume them with the **MCP Inspector**, and monitor their usage.

## Overview

An MCP server is an artifact you create in SAP Integration Suite to expose your enterprise APIs and data to AI agents as callable **tools** — through a single, governed endpoint on which you can enforce security and traffic policies, keep an audit trail, and manage the full lifecycle. In this exercise you build one MCP server from an **HTTP endpoint + OpenAPI specification** (Autobahn) and one from a **remote MCP server** (OpenSwissData), then connect an MCP client to prove the governed gateway works end to end.

> [!NOTE]
> Work in your own **`IN263 User 0XX`** package throughout (see [Getting Started](../ex0/README.md)). You can reuse the same package for both MCP servers in this exercise.

> [!IMPORTANT]
> General rules that apply to every MCP server you create:
> - Any OpenAPI specification used as a source must be version **3.0.0–3.0.3** (Swagger 2.0 and earlier are not supported).
> - MCP servers can only be **deployed and managed on the Integration Cell runtime profile**.
> - The **MCP Path** must not be empty or set to only `/`. Keep your tool selection within the per-server limit shown in the wizard.
> - After you choose **Deploy**, **wait for the Runtime Status to become `STARTED`** before navigating away or reloading — leaving the page while a deployment is still submitting can silently abort it.

## Exercises
- [Exercise 1.1 — Create an MCP Server from an HTTP Endpoint (Autobahn)](#exercise-11--create-an-mcp-server-from-an-http-endpoint-autobahn)
- [Exercise 1.2 — Create an MCP Server Using a Remote MCP Server as the Source](#exercise-12--create-an-mcp-server-using-a-remote-mcp-server-as-the-source)
- [Exercise 1.3 — Obtain Credentials via Developer Hub](#exercise-13--obtain-credentials-via-developer-hub)
- [Exercise 1.4 — Inspect and Consume the MCP Servers with MCP Inspector](#exercise-14--inspect-and-consume-the-mcp-servers-with-mcp-inspector)
- [Exercise 1.5 — Monitor the MCP Servers' Usage](#exercise-15--monitor-the-mcp-servers-usage)

---

## Exercise 1.1 — Create an MCP Server from an HTTP Endpoint (Autobahn)

In this sub-exercise you will expose an external REST service — the public **Autobahn** road/traffic API — as an MCP server by providing its OpenAPI specification.

> [!NOTE]
> To connect through a destination (recommended), an **HTTP destination** with the label `IntegrationCell.Include = true` must exist in the SAP BTP Cockpit (**Connectivity → Destinations**). Only destinations with **Proxy Type** `Internet` or `OnPremise` are supported. See [Create HTTP Destinations](https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/create-http-destinations).

1. In SAP Integration Suite, choose **Design → Integrations and APIs**, choose **Create**, and name the package <b>IN263 User 0XX</b> *(replace XX with your assigned participant number)*. This is the package you will work in throughout Exercise 1.

   <br>![Create your integration package](/exercises/ex1/images/ex1_00_create_package.png)

2. Open your <b>IN263 User 0XX</b> package and choose **Edit**.

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

   The MCP servers keep their **default Authentication and Authorization policies** — no policy change is needed.

7. Choose **Save** (top right), then choose **Deploy** (top right). See [Deploy an MCP Server](#deploy-an-mcp-server) below, and wait for **Runtime Status: STARTED**.

---

## Exercise 1.2 — Create an MCP Server Using a Remote MCP Server as the Source

In this sub-exercise you will create an MCP server that **proxies a remote (third-party) MCP server** — the public **OpenSwissData** MCP server — so that SAP Integration Suite acts as a managed governance layer in front of it, applying security and traffic policies while leaving the remote server unchanged.

<details>
<summary><strong>📖 About the remote server: OpenSwissData</strong></summary>

[OpenSwissData](https://www.openswissdata.com/) exposes an MCP server at `https://mcp.openswissdata.com/jsonrpc` that serves normalised Swiss federal reference data:
- the Swiss customs tariff (**TARES**, 8-digit tariff numbers with duties and restrictions),
- the **FINMA** register of supervised institutions and the FINMA warnings list,
- correspondences between the **NOGA / NACE / ISIC** activity classifications.

It is free with no key or sign-up (rate-limited to 100 calls per hour per IP) and requires **no authentication**, which makes it a convenient public remote MCP server to use for this exercise. This is provided by a private company which is not linked to any Swiss federal government departments. All rights reserved to them.
</details>

> [!NOTE]
> The destination this sub-exercise needs — **`openswissdata`** is **already set up for you**. See [Getting Started → The OpenSwissData destination](../ex0/README.md#the-openswissdata-destination-for-exercise-12). The remote server must support the **Streamable HTTP** transport (OpenSwissData does); only destinations with **Proxy Type** `Internet` can be used as a remote MCP source.

1. Log on to SAP Integration Suite and choose **Design → Integrations and APIs**.

2. Select your <b>IN263 User 0XX</b> integration package and choose **Edit**.

3. On the package details page, choose the **Artifacts** tab, and under **Add** select **MCP Server**. The **Create MCP Server** dialog opens.

4. Select **Remote MCP Servers** as the source type and click **Next**.

   <br>![Select the Remote MCP Servers source type](/exercises/ex1/images/ex1_06_source_type_remote.png)

5. Select the **`openswissdata`** destination, verify the following, and click **Next**:

   | Field | Value |
   |---|---|
   | **Name** | `openswissdata` |
   | **URL** | `https://mcp.openswissdata.com/jsonrpc` |
   | **Authentication** | `NoAuthentication` |

   <br>![Select the openswissdata destination](/exercises/ex1/images/ex1_07_select_destination.png)

6. In the **Provide MCP Details** step, fill in the fields:

   | Field | Value |
   |---|---|
   | **Name** | <b>OpenSwissData_MCP_0XX</b> *(replace XX with your assigned participant number)*. Alphanumeric and `_` only. |
   | **ID** | Auto-populated |
   | **Destination** | `openswissdata` *(selected in the previous step; used to connect to the remote server and, by default, for runtime execution)* |
   | **Relative Path** | *Optional.* Leave empty — the destination URL already points at the JSON-RPC endpoint. |
   | **MCP Path** | <b>/openswissdata_mcp_0XX</b> *(replace XX with your assigned participant number)*. Must not be empty or just `/`. |
   | **Version** | `1.0.0` (Optional) |
   | **Runtime Profile** | `Integration Cell` |
   | **Virtual Host** | Review the pre-filled virtual host URL. |

   <br>![Provide MCP Details for the remote MCP server](/exercises/ex1/images/ex1_08_mcp_details_remote.png)

7. In the **Create Tools** step, select the checkboxes for the two tools this exercise uses, then choose **Add** to finalize and create the MCP server:

   | Tool | What it does |
   |---|---|
   | `tariff_lookup` | Swiss customs tariff (TARES) line by tariff number |
   | `company_check` | Swiss company lookup by UID (`CHE-xxx.xxx.xxx`) |

   <br>![Select the OpenSwissData tools](/exercises/ex1/images/ex1_09_create_tools_remote.png)

8. Choose **Save** (top right), then choose **Deploy** (top right). See [Deploy an MCP Server](#deploy-an-mcp-server) below, and wait for **Runtime Status: STARTED**. You will inspect and invoke its proxied tools in [Exercise 1.4](#exercise-14--inspect-and-consume-the-mcp-servers-with-mcp-inspector) — for example, `tariff_lookup` with `hs8 = 8471.3000`.

---

### Deploy an MCP Server

Both MCP servers you create in 1.1 and 1.2 are deployed the same way: from the artifact editor, choose **Save** (top right) and then **Deploy** (top right).

1. With the MCP server open in the editor, choose **Save** (top right), then **Deploy** (top right).

   > Alternatively, from **Design → Integrations and APIs**, open the package, choose the **Action** icon next to the MCP server artifact, and select **Deploy**.

2. In the **Confirmation** dialog, verify the selected **Runtime Profile** (the design-time one is preselected) and, optionally, a **Virtual Host**, then confirm.

   <br>![Deploy confirmation dialog](/exercises/ex1/images/ex1_10_deploy.png)

3. Wait until the artifact's header shows **`Deployed on [date], Runtime Status: STARTED`**. The MCP server is now available for consumption.

   <br>![Runtime Status: STARTED](/exercises/ex1/images/ex1_11_deployed_started.png)

   > [!IMPORTANT]
   > Only proceed once the status shows **STARTED**. If it still shows *Not Deployed*, wait a moment and refresh the page — do not navigate away while the deployment is still submitting.

   > [!NOTE]
   > If the MCP server invokes an HTTPS backend whose certificate is not trusted by the runtime keystore, invocations fail with an internal server error (visible in **Manage Processing Logs**). See [Uploading a Keystore](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/uploading-keystore).

---

## Exercise 1.3 — Obtain Credentials via Developer Hub

In this sub-exercise you will publish your two deployed MCP servers as a **Product** in the **Developer Hub**, subscribe to it, and obtain the OAuth2 **credentials** (**Token URL**, **Key**, **Secret**) used to mint the bearer token that calls the governed MCP endpoints.

> **Note:** The whole flow happens in the **Developer Hub** — the centralized catalog for APIs, Events, and MCP Servers built into SAP Integration Suite. You reach it from the SAP Integration Suite shell.

### Publish your MCP servers as a Product

1. Open the **Developer Hub** from the SAP Integration Suite shell: use the navigation grid (top-right) and select **Developer Hub** — the "Centralized catalog for APIs, Events, and MCP Servers."

   <br>![Open Developer Hub from the navigation grid](/exercises/ex1/images/ex1_12_devhub_navigate.png)


2. In the Developer Hub top navigation, choose **Admin Center → Content**.

   <br>![Developer Hub home — the APIs, Events, and MCP Servers catalog](/exercises/ex1/images/ex1_13_devhub_home.png)

   2.1 This opens the **Manage Content** page with the **Business Systems** tab active by default.

   <br>![Admin Center → Content — Manage Content page](/exercises/ex1/images/ex1_14_devhub_manage_content.png)

3. Select the business system for your tenant — **workshop03a** (Type: Integration Suite). Then choose the **AI Artifacts** tab — it lists the MCP servers deployed on that system (Type: **MCP Server**, Authentication Provider: **Tenant XSUAA**). Use **Search by Name** to find yours. Select the checkboxes for both of your MCP servers (<b>Autobahn_MCP_0XX</b> and <b>OpenSwissData_MCP_0XX</b>) *(replace XX with your assigned participant number)*; verify it reads **2 Selected AI artifacts**, then choose **Create Product**.

   <br>![Select both MCP servers and choose Create Product](/exercises/ex1/images/ex1_15_devhub_select_mcp_servers.png)

4. In the **Create Product** dialog, fill in the details, confirm the **Selected AI Artifacts (2)** shows both MCP servers, then choose **Publish**:

   | Field | Value |
   |---|---|
   | **Name** | <b>IN263 MCP Product 0XX</b> *(replace XX with your assigned participant number)* |
   | **ID** | <b>IN263_MCP_Product_0XX</b> *(replace XX with your assigned participant number)* |
   | **Short Text** | A short description, e.g. `Autobahn and OpenSwissData MCP servers bundled as a governed product for IN263.` |
   | **Description** | *Optional.* |

   <br>![Create Product dialog with both MCP servers selected](/exercises/ex1/images/ex1_16_devhub_create_product.png)

5. Publishing runs in the background. Monitor it in **Admin Center → Scheduled Requests** — your product appears there; wait until **Status** shows **Success**.

   <br>![Admin Center → Scheduled Requests showing Success status](/exercises/ex1/images/ex1_17_devhub_scheduled_requests.png)

   > **Note:** Once the status shows **Success**, your product appears in the Developer Hub catalog.

### Subscribe and obtain credentials

1. From the Developer Hub catalog (or **Explore → Products**), open your product <b>IN263 MCP Product 0XX</b>. Its detail page has **Overview / AI Artifacts / Associated Subscriptions** tabs and a **Subscribe** action.

   Choose **Subscribe → Create New Subscription for Agent**. (MCP/AI products are consumed through an **Agent** subscription.)

   <br>![Product detail page with Subscribe action](/exercises/ex1/images/ex1_18_devhub_product_subscribe.png)

2. In the **Create New Subscription for Agent** dialog fill in:

   | Field | Value |
   |---|---|
   | **Name** | <b>IN263_MCP_App_0XX</b> *(replace XX with your assigned participant number)* |
   | **Short Text** | *Optional.* |
   | **Description** | *Optional.* |
   | **Callback URL** | *Optional.* |

   Choose **Create**.

   <br>![Create New Subscription for Agent dialog](/exercises/ex1/images/ex1_19_devhub_create_subscription.png)

3. The subscription is created and may show "Your subscription is being finalized. Please refresh the page after some time." Wait, then **refresh**. Once finalized, the subscription's **Overview** tab shows a **Credentials** section with three values, each with a reveal (eye) icon and a copy button:

   | Credential | Use |
   |---|---|
   | **Token URL** | The OAuth2 token endpoint used to request a bearer token. |
   | **Key** | The client identifier (OAuth2 client ID). |
   | **Secret** | The client secret. |

   Copy all three — you will use them in the next step and in [Exercise 1.4](#exercise-14--inspect-and-consume-the-mcp-servers-with-mcp-inspector).

   <br>![Subscription credentials — Token URL, Key, and Secret](/exercises/ex1/images/ex1_20_devhub_credentials.png)

   > **Important:** Treat the **Key** and **Secret** like a password — do not share or commit them.

### Generate a bearer token

The MCP endpoints are protected by OAuth2. You exchange **Key** + **Secret** at the **Token URL** for a short-lived **bearer token** (`access_token`) using the **client-credentials** grant.

**Option A — command line:**

- **macOS / Linux:**
  ```bash
  curl -s -X POST '<TOKEN_URL>' \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d 'grant_type=client_credentials' \
    -d 'client_id=<KEY>' \
    -d 'client_secret=<SECRET>'
  ```
- **Windows (PowerShell):**
  ```powershell
  curl.exe -s -X POST '<TOKEN_URL>' -H 'Content-Type: application/x-www-form-urlencoded' -d 'grant_type=client_credentials' -d 'client_id=<KEY>' -d 'client_secret=<SECRET>'
  ```

The response JSON contains an `access_token` field — that value is your bearer token.

<br>![curl command and the JSON response containing access_token](/exercises/ex1/images/ex1_21_token_curl.png)

**Option B — Bruno app:**

1. Create a new request in Bruno and set its **URL** to your MCP server endpoint URL (`<MCP_URL>`). Open the **Auth** tab, choose **OAuth 2.0**, and set **Grant Type** to **Client Credentials**. Fill in the configuration:

   | Field | Value |
   |---|---|
   | **Access Token URL** | `<TOKEN_URL>` |
   | **Client ID** | `<KEY>` |
   | **Client Secret** | `<SECRET>` |

   <br>![Bruno — OAuth 2.0 Client Credentials configuration](/exercises/ex1/images/ex1_22_token_bruno_01.png)

2. Scroll down and choose **Get Access Token**.

   <br>![Bruno — Get Access Token](/exercises/ex1/images/ex1_23_token_bruno_02.png)

3. Scroll back up and copy the generated **Access Token** — this is your bearer token.

   <br>![Bruno — copy the generated access token](/exercises/ex1/images/ex1_24_token_bruno_03.png)

> **Note:** The token is short-lived. If the MCP Inspector later returns 401, regenerate the token and reconnect.

---

## Exercise 1.4 — Inspect and Consume the MCP Servers with MCP Inspector

In this optional sub-exercise you will connect the **MCP Inspector** — the official developer tool for the Model Context Protocol — to one of your deployed MCP servers, list its tools, and invoke one to confirm the end-to-end flow through the governed Integration Cell endpoint.

> [!NOTE]
> Prerequisites: an MCP server **deployed** with runtime status **STARTED** (from 1.1–1.2).

### Step 1: Get the endpoint URL and your credentials

1. Open your deployed MCP server and copy its **endpoint URL** — shown on the MCP server's **Overview** tab as the **MCP URL** (also available in **Monitor → Integrations and APIs → Manage Integration Content → Endpoints**, see [Exercise 1.5](#exercise-15--monitor-the-mcp-servers-usage)).

   <br>![Copy the MCP server endpoint URL](/exercises/ex1/images/ex1_25_mcp_url.png)

2. Generate a **bearer token** as described in [Exercise 1.3](#exercise-13--obtain-credentials-via-developer-hub) (the `access_token` from the client-credentials request). You will paste it as an `Authorization: Bearer` header in the Inspector.

### Step 2: Open MCP Inspector

3. Open the **MCP Inspector**. You can use the hosted instance (preferred) or run it locally — either way you land on the same Inspector UI, and the remaining steps are identical.

   **Option A — Hosted Inspector (preferred):** Open [https://url.sap/vn4lob](https://url.sap/vn4lob) in your browser and log on with the same workshop user and password you used to access SAP Integration Suite (see [Accessing the system](../../README.md#accessing-the-system)). No local installation is required.

   **Option B — Run it locally:** Requires **Node.js**. Open a terminal and run the Inspector — it starts a small local web app and opens it in your browser.

   - **macOS:** open **Terminal** (press `⌘ + Space`, type `Terminal`, press `Enter`).
   - **Windows:** open **Windows Terminal** or **Command Prompt** (press the `Windows` key, type `cmd`, press `Enter`).

   Then run:

   ```bash
   npx @modelcontextprotocol/inspector
   ```

### Step 3: Add the MCP server

4. In the Inspector, choose **Add Servers**.

   <br>![Choose Add Servers](/exercises/ex1/images/ex1_26_inspector_01.png)

5. Choose **+ Add manually**.

   <br>![Choose Add manually](/exercises/ex1/images/ex1_27_inspector_02.png)

6. In the **Add server** dialog, fill in the details and choose **Add**:

   | Field | Value |
   |---|---|
   | **Server ID** | A name for the server, e.g. <b>OpenSwissData_MCP_0XX</b> *(replace XX …)* |
   | **Transport** | `streamable-http` |
   | **URL** | Your MCP server's endpoint URL (`<MCP_URL>` from Step 1) |

   <br>![Add server — Server ID, Transport, and URL](/exercises/ex1/images/ex1_28_inspector_03.png)

### Step 4: Add the bearer token as a custom header

7. On the server card, choose **Settings**.

   <br>![Open the server Settings](/exercises/ex1/images/ex1_29_inspector_04.png)

8. Collapse the **Options** section.

   <br>![Collapse the Options section](/exercises/ex1/images/ex1_30_inspector_05.png)

9. Expand the **Custom Headers** section.

   <br>![Expand the Custom Headers section](/exercises/ex1/images/ex1_31_inspector_06.png)

10. Choose **+ Add Header**, add the header below, then close the dialog:

    | Field | Value |
    |---|---|
    | **Header Name** | `Authorization` |
    | **Value** | `Bearer <access_token>` *(the token from [Exercise 1.3](#exercise-13--obtain-credentials-via-developer-hub))* |

    <br>![Add an Authorization: Bearer custom header](/exercises/ex1/images/ex1_32_inspector_07.png)

### Step 5: Connect and invoke a tool

11. Toggle the server **Connect** switch. The server shows **Connected** and the protocol handshake appears in the message log.

    <br>![Toggle the Connect switch](/exercises/ex1/images/ex1_33_inspector_08.png)

12. Choose the **Tools** tab at the top.

    <br>![Open the Tools tab](/exercises/ex1/images/ex1_34_inspector_09.png)

13. Select the **Swiss company check by UID** (`company_check`) tool, enter the **uid** `CHE-109.815.753`, and choose **Execute Tool**.

    <br>![Select company_check, enter the UID, and execute](/exercises/ex1/images/ex1_35_inspector_10.png)

14. Review the result returned through the governed Integration Cell endpoint.

    <br>![Tool execution result](/exercises/ex1/images/ex1_36_inspector_11.png)

---

## Exercise 1.5 — Monitor the MCP Servers' Usage

In this sub-exercise you will find where to check the deployment status, runtime logs, endpoints, and usage analytics of your MCP servers. If you didn't take the previous sub-execise (Optional), you can explore other MCP server's usage data deployed in the same tenant.

### Step 1: Check status, endpoints, and logs

1. Go to **Monitor → Integrations and APIs**, and under **Overview** select **Integration Cell** from the **Runtime** drop-down.

2. In the **Manage Integration Content** area, choose the tile that lists deployed artifacts. Each entry shows its **Name** (with artifact type) and **Status** (see [Runtime Status](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/runtime-status)).

   <br>![Deployed APIs and MCP servers in Manage Integration Content](/exercises/ex1/images/ex1_37_manage_integration_content.png)

3. Search for your MCP server by name or ID, or filter by **Status** / **Type** via **Table Settings**.

4. Select your MCP server. On the right, the **Integration Content Details** show:
   - Header info: **Deployed on**, **Deployed by**, **Version**, **ID**, **Package**, and actions **Restart / Undeploy / Download**.
   - **Endpoints** — the exposed URLs; use **Copy** to copy the endpoint (the one you used in [Exercise 1.4](#exercise-14--inspect-and-consume-the-mcp-servers-with-mcp-inspector)).
   - **Status Details** — whether the artifact is deployed or undeployed.
   - **Log Configuration** — set the **Log Level** (None, Info, Error, Debug, Trace). Use **Trace**/**Debug** only in test environments; both expire automatically (Debug after 24 hours, Trace after ~10 minutes).

5. To investigate individual invocations and errors, use **Monitor Message Processing** and **Manage Processing Logs**.

### Step 2: Analyze usage trends

6. Choose **Analyze** from the left navigation to open the analytics dashboards. At the top, set **Runtime** to **Integration Cell** and **Artifact Type** to **MCP Server** so the view focuses on your MCP servers.

   <br>![Analyze — Runtime: Integration Cell, Artifact Type: MCP Server](/exercises/ex1/images/ex1_38_analyze.png)

7. Explore the KPI tiles (**Total MCP Calls**, **MCP Response Time**, **Total MCP Errors**) and the **MCP Charts** below them. Use the tabs (**Overview**, **Health**, **Usage**, **Anomalies**, **Predictions**) and the chart filters (**All MCP Servers** or **Select MCP Servers from the list**) to review usage, performance, and traffic for your deployed MCP servers. See [Analyze APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/analyze-apis-and-mcp-servers).

---

## Reference and Prerequisites

### Prerequisites

- **API Management** capability and the **Integration Cell** runtime activated (pre-configured for the session — see [Getting Started](../ex0/README.md)).
- The `PI_Integration_Developer` role collection assigned to your user (pre-assigned — see [Getting Started](../ex0/README.md)).
- Your own package named <b>IN263 User 0XX</b> *(replace XX with your assigned participant number)* — created as the first step of Exercise 1.1.

### References

- [MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/model-context-protocol-mcp)
- [Creating an MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/creating-mcp-server)
- [How Model Context Protocol (MCP) Server Enables AI Integration](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/how-mcp-server-enables-ai-integration)
- [Create an MCP Server from an HTTP Endpoint](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-mcp-server-from-http-endpoint)
- [Create an MCP Server Using Remote MCP Server as the Source](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/create-mcp-server-using-remote-mcp-server-as-source)
- [Deploy an MCP Server](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/deploy-mcp-server)
- [Invoke the MCP Server Endpoint URL](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/invoke-mcp-server-endpoint-url)
- [MCP Inspector (modelcontextprotocol/inspector)](https://github.com/modelcontextprotocol/inspector)
- [Monitor APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/monitor-apis)
- [Analyze APIs and MCP Servers](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/analyze-apis-and-mcp-servers)

## Summary

You have now created and governed MCP servers in SAP Integration Suite. In doing so you have:

- Created an MCP server from an **HTTP endpoint + OpenAPI specification** (Autobahn).
- Created an MCP server from a **remote MCP server** (OpenSwissData), with Integration Suite as the governance layer in front of it.
- Left each server's **default Authentication and Authorization policies** in place, **Saved**, and **Deployed** both to the Integration Cell runtime (**Runtime Status: STARTED**).
- Published the MCP servers as a **Product** and obtained **OAuth2 credentials** (Client ID / Secret / Token URL) via the **Developer Hub**.
- **Inspected and invoked** the tools with the MCP Inspector using a bearer token.
- Located the **monitoring and analytics** for status, endpoints, logs, and usage.

Through this you experienced how SAP Integration Suite acts as a **governed gateway** between AI agents and your enterprise integrations.

Continue to - [Exercise 2 — AI-based Memory Profiling of an Existing iFlow](../ex2/README.md)
