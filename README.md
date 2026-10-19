[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/teched2026-IN263)](https://api.reuse.software/info/github.com/SAP-samples/teched2026-IN263)

# IN263 - AI-supported integrations: From design quality to agent readiness

## Description

This repository contains the material for the SAP TechEd 2026 session called **IN263 - AI-supported integrations: From design quality to agent readiness**.

## Overview

Delivering high-quality integrations to AI agents starts at design time. In this hands-on session you will experience how **iFlow Profiling** captures memory snapshots during simulation to surface AI-driven, guideline-aligned fix recommendations before deployment, and how **Script Optimization** applies GenAI best practices to Groovy scripts. You will also see how **MCP (Model Context Protocol)** and a governed gateway ensure that AI agents consume your integrations securely — with policy enforcement, auditability, and full lifecycle control.

By the end of the session you will have:

- Set up your SAP Integration Suite tenant for the AI-based design-time and MCP features.
- Created MCP servers from an HTTP endpoint (Autobahn) and a remote MCP server (OpenSwissData), deployed them, inspected and consumed them with the MCP Inspector, and monitored their usage.
- Run an AI-based memory profiling session on an existing integration flow and applied the recommended fixes.
- Optimized a Groovy script with Generative AI.


## Requirements

To follow the exercises in this repository you need the following. In the TechEd hands-on room this is **pre-provisioned by the instructor** (see [Getting Started - Pre-configured Setup](exercises/ex0/)); the list is what you would need to reproduce the exercises in your own environment:

- An **SAP Integration Suite** tenant with the **Premium Edition** (or Enhanced Edition where indicated). The AI features used in this session are available only with the Premium and Enhanced Editions, offered as a free promotion until the January 2027 software release. See SAP Notes [2903776](https://me.sap.com/notes/2903776) (service plans) and [3463620](https://me.sap.com/notes/3463620) (AI feature availability by region).
- The **Cloud Integration** and **API Management** capabilities activated, and the **Integration Cell** runtime provisioned.
- The following SAP BTP role collections assigned to your user: `PI_Administrator`, `PI_Integration_Developer`, `AuthGroup.API.ApplicationDeveloper`, and `WebToolingWorkspace.Write`.
- Access to an external HTTP/REST service you can expose (for example, the public Autobahn API) and, optionally, a remote MCP server for the remote-source exercise.
- **Node.js** installed locally, to run the **MCP Inspector** (`npx @modelcontextprotocol/inspector`) when consuming your MCP servers.
- (Optional) **Joule Work Desktop**, as an alternative MCP client, if you are in the SAP Early Adopter Care (EAC) program.

## Exercises

- [Getting Started - Pre-configured Setup](exercises/ex0/)
- [Exercise 1 - Create and Govern MCP Servers](exercises/ex1/)
    - [Exercise 1.1 - Create an MCP Server from an HTTP Endpoint (Autobahn)](exercises/ex1#exercise-11--create-an-mcp-server-from-an-http-endpoint-autobahn)
    - [Exercise 1.2 - Create an MCP Server Using a Remote MCP Server as the Source](exercises/ex1#exercise-12--create-an-mcp-server-using-a-remote-mcp-server-as-the-source)
    - [Exercise 1.3 - Inspect and Consume the MCP Servers with MCP Inspector (Optional)](exercises/ex1#exercise-13--inspect-and-consume-the-mcp-servers-with-mcp-inspector-optional)
    - [Exercise 1.4 - Monitor the MCP Servers' Usage](exercises/ex1#exercise-14--monitor-the-mcp-servers-usage)
- [Exercise 2 - AI-based Memory Profiling of an Existing iFlow](exercises/ex2/)
- [Exercise 3 - Optimize Groovy Scripts with AI](exercises/ex3/)


## Additional Relevant Sessions

You can also gain some further knowledge around SAP Integration Suite and its capabilities by attending the following SAP TechEd Hands-on Workshop sessions:

- [IN261 | Model context protocol (MCP) unlocked: Enable governed access to AI agents | Hands-on Workshop](https://www.sap.com/events/teched/berlin/flow/sap/te26/catalog/page/catalog/session/1786027688241001k1kf)
- [IN262 | Build an integration scenario from scratch with AI assistance at every step | Hands-on Workshop](http://sap.com/events/teched/berlin/flow/sap/te26/catalog/page/catalog/session/1786027888165001UXeS)
- [IN163 | Build your own event-driven scenario with advanced event mesh | Hands-on Workshop](https://www.sap.com/events/teched/berlin/flow/sap/te26/catalog/page/catalog/session/1786029069284001qBM4)

## How to obtain support

Support for the content in this repository is available during the actual time of the online session for which this content has been designed. Otherwise, you may request support via the [Issues](../../issues) tab.

## Contributing

Please read the [CONTRIBUTING.md](./CONTRIBUTING.md) to understand the contribution guidelines.

## Code of Conduct

Please read the [SAP Open Source Code of Conduct](https://github.com/SAP-samples/.github/blob/main/CODE_OF_CONDUCT.md).

## License

Copyright 2026 SAP SE or an SAP affiliate company and teched2026-IN263 contributors. Please see our [LICENSE](LICENSE) for copyright and license information. Detailed information including third-party components and their licensing/copyright information is available [via the REUSE tool](https://api.reuse.software/info/github.com/SAP-samples/teched2026-IN263).
