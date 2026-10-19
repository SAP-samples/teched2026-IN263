# Getting Started — Pre-configured Setup

## Overview

This section describes the **pre-configured setup** that supports the hands-on exercises. In the TechEd hands-on room, the instructor has already prepared all of this for you — it is **not** part of the exercises you perform. It is documented here for your information, so that you understand what the environment relies on and can **reproduce the same setup in your own projects** after the session.

The two things you do yourself are **logging in** ([Accessing the system](#accessing-the-system)) and **creating your package** ([Your integration package](#your-integration-package)).

> **Reference:** [Artificial Intelligence (SAP Integration Suite)](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/artificial-intelligence)

## Accessing the system

To reach the SAP Integration Suite tenant used in this session:

1. Open [https://url.sap/vutrsb](https://url.sap/vutrsb) in your browser.

2. Log on with the workshop user assigned to you:

   | Field | Value |
   | --- | --- |
   | **User** | <b>User0XX</b> — replace `XX` with your assigned participant number, zero-padded to three digits (for example, `User007`) |
   | **Password** | Provided by the instructor |

   <br>![SAP Integration Suite login](/exercises/ex0/images/ex0_01_login.png)

   > [!IMPORTANT]
   > Use the same participant number throughout the session — for your login user, for your **`IN263 User 0XX`** package, and in the artifact names (for example, `OpenSwissData_MCP_0XX`). Your participant number is assigned to you by the instructor.

## Tenant, capabilities, and runtime (already provisioned)

The hands-on tenant has been provisioned with the following. To run these exercises in your own environment, you would need the same:

- An **SAP Integration Suite** tenant on the **Premium Edition** (or Enhanced Edition where a step says so). The AI features in this session are available only with the Premium and Enhanced Editions, as a free promotion until the January 2027 software release.
  - Service plans and supported feature set: SAP Note [2903776](https://me.sap.com/notes/2903776).
  - AI feature availability by SAP BTP region: SAP Note [3463620](https://me.sap.com/notes/3463620).
- The **Cloud Integration** and **API Management** capabilities activated.
- The **Integration Cell** runtime provisioned (MCP servers can only be deployed and managed on the Integration Cell runtime profile).

See [Activating and Managing Capabilities](https://help.sap.com/docs/integration-suite/sap-integration-suite/activating-and-managing-capabilities) and [Activate Integration Cell](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/activate-integration-cell) for how this is done.

## Capabilities and role collections (already assigned)

Your workshop user has already been given the role collections below (assigned in the **SAP BTP Cockpit** under **Security → Users →** your user **→ Assign Role Collection**). To reproduce this in your own subaccount, assign the same collections to your user:

| Role collection | Needed for |
| --- | --- |
| `PI_Administrator` | Activating AI features; administering Cloud Integration |
| `PI_Integration_Developer` | Designing integration flows and MCP server artifacts |
| `AuthGroup.API.ApplicationDeveloper` | Subscribing to products and obtaining credentials in Developer Hub |
| `WebToolingWorkspace.Write` | Using **Optimize** in the Groovy script editor |

> You can confirm the active capabilities any time in the SAP Integration Suite launchpad under **Settings** — **Cloud Integration** and **API Management** are activated and the **Integration Cell** runtime is available. (Newly assigned roles only take effect after logging off and on again.)

## Artificial Intelligence features (already activated)

A tenant administrator (`PI_Administrator`) has already activated the AI features used in Exercises 2 and 3, under **Settings → Artificial Intelligence**. To reproduce this in your own tenant, activate the same features (availability depends on your activated capabilities and service plan):

| Feature | Capability | What it enables in this session |
| --- | --- | --- |
| **Memory Profiling** | Cloud Integration | Memory insights derived during integration flow simulation, with improvement suggestions (Exercise 2) |
| **Script Optimization** | Cloud Integration | Enhancing Groovy scripts using Generative AI (Exercise 3) |
| **Generation of OpenAPI Specification** | API Management | AI-assisted generation of an OpenAPI specification when creating an API/HTTP MCP source (used optionally in Exercise 1.1) |

> **Note:** *Flow Step Recommendations* is always active and cannot be deactivated. The other AI features (for example, AI-assisted Error Resolution, Conversational Analytics) are optional for this session. These features are available to users who hold the role collections listed above.

## The OpenSwissData destination (for Exercise 1.2)

[Exercise 1.2](../ex1/README.md#exercise-12--create-an-mcp-server-using-a-remote-mcp-server-as-the-source-federation) federates a public remote MCP server. The destination it needs — **`openswissdata`** — has already been created for you in the **SAP BTP Cockpit** of the subaccount that hosts SAP Integration Suite (**Connectivity → Destinations → Create Destination → From Scratch**). To reproduce it in your own subaccount, create a destination with these values:

| Field | Value |
| --- | --- |
| **Name** | `openswissdata` |
| **Type** | `HTTP` |
| **URL** | `https://mcp.openswissdata.com/jsonrpc` |
| **Proxy Type** | `Internet` |
| **Authentication** | `NoAuthentication` |

Add the following two **Labels** (under **Additional Properties → Labels** / **New Property**):

| Label | Value |
| --- | --- |
| `IntegrationCell.Include` | `true` |
| `IntegrationCell.Provides` | `mcp` |

> The OpenSwissData MCP server is public, free (100 calls/hour/IP), and requires no key — so this destination needs no credentials. Only destinations with **Proxy Type** `Internet` can be used as a remote MCP source. The `IntegrationCell.Include = true` label makes the Integration Cell discover the destination, and `IntegrationCell.Provides = mcp` marks it as a remote **MCP** source so it appears in the **Remote MCP Servers** step of the Add MCP Server wizard.

## Your integration package

This is the one setup step **you** perform — the rest of the exercises build artifacts inside your own package.

1. In SAP Integration Suite, go to **Design → Integrations and APIs**, choose **Create**, and name the package <b>IN263 User 0XX</b> *(replace XX with your assigned participant number)*. See [Creating an Integration Package](https://help.sap.com/docs/integration-suite/sap-integration-suite/creating-integration-package).

   <br>![Create your integration package](/exercises/ex0/images/ex0_02_create_package.png)

> [!TIP]
> **Reference package:** The instructor maintains a reference package named **`IN263 User XX`** that contains completed versions of the artifacts. Use it to compare against your own work. Throughout Exercises 1–3, wherever a step says "your `IN263 User 0XX` package", use the package you create here.

## Summary

You now know the pre-configured setup behind this session — the tenant edition, the **Cloud Integration** / **API Management** capabilities, the **Integration Cell** runtime, the role collections, the activated AI features (**Memory Profiling** and **Script Optimization**), and the **`openswissdata`** destination used by the federation exercise — and how to reproduce it all in your own projects. You have also logged in and created your personal **`IN263 User 0XX`** package to work in.

Continue to - [Exercise 1 — Create and Govern MCP Servers](../ex1/README.md)
