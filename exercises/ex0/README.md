# Getting Started — Pre-configured Setup

## Overview

This section describes the **pre-configured setup** that supports the hands-on exercises. In the TechEd hands-on room, the instructor has already prepared all of this for you — it is **not** part of the exercises you perform. It is documented here for your information, so that you understand what the environment relies on and can **reproduce the same setup in your own projects** after the session.

> **Reference:** [Artificial Intelligence (SAP Integration Suite)](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/artificial-intelligence)

## Tenant, capabilities, and runtime (already provisioned)

The hands-on tenant has been provisioned with the following. To run these exercises in your own environment, you would need the same:

- An **SAP Integration Suite** tenant on the **Premium Edition** (or Enhanced Edition where a step says so). The AI features in this session require one of these editions.
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
| `AuthGroup.Content.Admin` | Creating and publishing a **Product** (and adding MCP servers to it) in the Developer Hub **Admin Center → Content** (Exercise 1.3) |
| `AuthGroup.API.ApplicationDeveloper` | Registering in the Developer Hub, subscribing to a product, and obtaining the application/subscription **credentials** (Token URL / Key / Secret) (Exercise 1.3) |
| `WebToolingWorkspace.Write` | Using **Optimize** in the Groovy script editor |

> The `AuthGroup.API.ApplicationDeveloper` role is granted through Developer Hub registration (self-registration / Add User) or IdP role-collection mapping rather than assigned manually in the BTP Cockpit. Newly assigned roles only take effect after logging off and on again.

> You can confirm the active capabilities any time in the SAP Integration Suite launchpad under **Settings** — **Cloud Integration** and **API Management** are activated and the **Integration Cell** runtime is available.

## Artificial Intelligence features (already activated)

A tenant administrator (`PI_Administrator`) has already activated the AI features used in Exercises 2 and 3, under **Settings → Artificial Intelligence**. To reproduce this in your own tenant, activate the same features (availability depends on your activated capabilities and service plan):

| Feature | Capability | What it enables in this session |
| --- | --- | --- |
| **Memory Profiling** | Cloud Integration | Memory insights derived during integration flow simulation, with improvement suggestions (Exercise 2) |
| **Script Optimization** | Cloud Integration | Enhancing Groovy scripts using Generative AI (Exercise 3) |
| **Generation of OpenAPI Specification** | API Management | AI-assisted generation of an OpenAPI specification when creating an API/HTTP MCP source (used optionally in Exercise 1.1) |

> **Note:** *Flow Step Recommendations* is always active and cannot be deactivated. The other AI features (for example, AI-assisted Error Resolution, Conversational Analytics) are optional for this session. These features are available to users who hold the role collections listed above.

## The OpenSwissData destination (for Exercise 1.2)

[Exercise 1.2](../ex1/README.md#exercise-12--create-an-mcp-server-using-a-remote-mcp-server-as-the-source) uses a public remote MCP server. The destination it needs — **`openswissdata`** — has already been created for you in the **SAP BTP Cockpit** of the subaccount that hosts SAP Integration Suite (**Connectivity → Destinations → Create Destination → From Scratch**). To reproduce it in your own subaccount, create a destination with these values:

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

## Summary

You now know the pre-configured setup behind this session — the tenant edition, the **Cloud Integration** / **API Management** capabilities, the **Integration Cell** runtime, the role collections, the activated AI features (**Memory Profiling** and **Script Optimization**), and the **`openswissdata`** destination used by the remote MCP server exercise — and how to reproduce it all in your own projects.

Continue to - [Exercise 1 — Create and Govern MCP Servers](../ex1/README.md)
