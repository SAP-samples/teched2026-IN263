# Getting Started - System Setup

In this exercise, you will prepare your SAP Integration Suite tenant so that you can complete the rest of the session. You will verify the required capabilities and runtime, assign the needed roles, and activate the Artificial Intelligence features used by the memory profiling and script optimization exercises.

> **Reference:** [Artificial Intelligence (SAP Integration Suite)](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/artificial-intelligence)

## Prerequisites

Before you start, make sure the following are in place. In a TechEd hands-on room these are usually pre-provisioned by the instructor; verify them so you do not get blocked later.

- An **SAP Integration Suite** tenant on the **Premium Edition** (or Enhanced Edition where a step says so). The AI features in this session are available only with the Premium and Enhanced Editions, as a free promotion until the January 2027 software release.
  - Service plans and supported feature set: SAP Note [2903776](https://me.sap.com/notes/2903776).
  - AI feature availability by SAP BTP region: SAP Note [3463620](https://me.sap.com/notes/3463620).
- The **Cloud Integration** and **API Management** capabilities activated.
- The **Integration Cell** runtime provisioned (MCP servers can only be deployed and managed on the Integration Cell runtime profile).

## Exercise 0.1 - Verify Capabilities and Assign Role Collections

After completing these steps you will have confirmed that the right capabilities are active and that your user has the required roles.

1. Log on to SAP Integration Suite.

2. Confirm the activated capabilities. In the SAP Integration Suite launchpad, go to **Settings** and verify that **Cloud Integration** and **API Management** are activated and that the **Integration Cell** runtime is available. See [Activating and Managing Capabilities](https://help.sap.com/docs/integration-suite/sap-integration-suite/activating-and-managing-capabilities) and [Activate Integration Cell](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/activate-integration-cell).

3. In the **SAP BTP Cockpit** for your subaccount, assign the following role collections to your user (**Security → Users →** your user **→ Assign Role Collection**):

   | Role collection | Needed for |
   | --- | --- |
   | `PI_Administrator` | Activating AI features; administering Cloud Integration |
   | `PI_Integration_Developer` | Designing integration flows and MCP server artifacts |
   | `AuthGroup.API.ApplicationDeveloper` | Subscribing to products and obtaining credentials in Developer Hub |
   | `WebToolingWorkspace.Write` | Using **Optimize** in the Groovy script editor |

4. Log off and log on again so that the newly assigned roles take effect.

5. Create your own **integration package** to hold the artifacts you build in this session. In **Design → Integrations and APIs**, choose **Create** and name the package **`IN263 User <number>`**, replacing `<number>` with your assigned participant number (for example, `IN263 User 07`). See [Creating an Integration Package](https://help.sap.com/docs/integration-suite/sap-integration-suite/creating-integration-package).

   > **Reference package:** The instructor maintains a reference package named **`IN263 User XX`** that contains completed versions of the artifacts. Use it to compare against your own work. Throughout Exercises 1–3, wherever a step says "your `IN263 User <number>` package", use the package you create here.

## Exercise 0.2 - Activate the Artificial Intelligence Features

After completing these steps you will have activated the AI features used in Exercises 2 and 3. You must be a **tenant administrator** (`PI_Administrator`) to do this.

1. In SAP Integration Suite, go to **Settings → Artificial Intelligence**.

2. Activate the following features (availability depends on your activated capabilities and service plan):

   | Feature | Capability | What it enables in this session |
   | --- | --- | --- |
   | **Memory Profiling** | Cloud Integration | Memory insights derived during integration flow simulation, with improvement suggestions (Exercise 2) |
   | **Script Optimization** | Cloud Integration | Enhancing Groovy scripts using Generative AI (Exercise 3) |
   | **Generation of OpenAPI Specification** | API Management | AI-assisted generation of an OpenAPI specification when creating an API/HTTP MCP source (used optionally in Exercise 1.2) |

   > **Note:** *Flow Step Recommendations* is always active and cannot be deactivated. The other AI features (for example, AI-assisted Error Resolution, Conversational Analytics) are optional for this session.

3. Confirm the activation. The features become available to users who hold the required role collections listed in Exercise 0.1.

## Summary

You have verified the required capabilities and the Integration Cell runtime, assigned the role collections, and activated the AI features (Memory Profiling and Script Optimization).

Continue to - [Exercise 1 - Create and Govern MCP Servers](../ex1/README.md)
