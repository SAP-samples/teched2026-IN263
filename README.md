[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/teched2026-IN263)](https://api.reuse.software/info/github.com/SAP-samples/teched2026-IN263)

# IN263 - Design Trustworthy Integrations and APIs for the AI Era

## Description

This repository contains the material for the SAP TechEd 2026 session called **IN263 - Design Trustworthy Integrations and APIs for the AI Era**.

## Overview

Delivering high-quality integrations to AI agents starts at design time. In this hands-on session you will experience how **iFlow Profiling** captures memory snapshots during simulation to surface AI-driven, guideline-aligned fix recommendations before deployment, and how **Script Optimization** applies GenAI best practices to Groovy scripts. You will also see how **MCP (Model Context Protocol)** and a governed gateway ensure that AI agents consume your integrations securely — with policy enforcement, auditability, and full lifecycle control.

By the end of the session you will have:

- Set up your SAP Integration Suite tenant for the AI-based design-time and MCP features.
- Created MCP servers from an API artifact (S/4HANA Cloud), an HTTP endpoint (Autobahn), and a remote MCP server (federation), deployed them, consumed them from an AI client (Joule Work Desktop), and monitored their usage.
- Run an AI-based memory profiling session on an existing integration flow and applied the recommended fixes.
- Optimized a Groovy script with Generative AI.

> **Disclaimer**
> These exercises are a first draft based on the official SAP Integration Suite product documentation (see links in each exercise). SAP Integration Suite evolves continuously — always cross-check the live documentation at [help.sap.com](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/) and your tenant's UI, as labels, availability, and steps may change. Screenshots will be added in a later revision.

## Requirements

To follow the exercises in this repository you need:

- An **SAP Integration Suite** tenant with the **Premium Edition** (or Enhanced Edition where indicated). The AI features used in this session are available only with the Premium and Enhanced Editions, offered as a free promotion until the January 2027 software release. See SAP Notes [2903776](https://me.sap.com/notes/2903776) (service plans) and [3463620](https://me.sap.com/notes/3463620) (AI feature availability by region).
- The **Cloud Integration** and **API Management** capabilities activated, and the **Integration Cell** runtime provisioned.
- The following SAP BTP role collections assigned to your user: `PI_Administrator`, `PI_Integration_Developer`, `AuthGroup.API.ApplicationDeveloper`, and `WebToolingWorkspace.Write`.
- Access to a system you can expose through an API/HTTP endpoint (for example, an SAP S/4HANA Cloud sandbox API from SAP Business Accelerator Hub) and, optionally, a remote MCP server for the federation exercise.
- (Optional) **Joule Work Desktop** installed, if you want to consume the MCP servers from an AI client. Joule Work Desktop is part of an SAP Early Adopter Care (EAC) program.

## Exercises

- [Getting Started - System Setup](exercises/ex0/)
- [Exercise 1 - Create and Govern MCP Servers](exercises/ex1/)
    - [Exercise 1.1 - Create an MCP Server from an API Artifact (S/4HANA Cloud)](exercises/ex1#exercise-11-create-an-mcp-server-from-an-api-artifact-s4hana-cloud)
    - [Exercise 1.2 - Create an MCP Server from an HTTP Endpoint (Autobahn)](exercises/ex1#exercise-12-create-an-mcp-server-from-an-http-endpoint-autobahn)
    - [Exercise 1.3 - Create an MCP Server Using a Remote MCP Server as the Source (Federation)](exercises/ex1#exercise-13-create-an-mcp-server-using-a-remote-mcp-server-as-the-source-federation)
    - [Exercise 1.4 - Configure and Use the MCP Servers in Joule Work Desktop](exercises/ex1#exercise-14-configure-and-use-the-mcp-servers-in-joule-work-desktop)
    - [Exercise 1.5 - Monitor the MCP Servers' Usage](exercises/ex1#exercise-15-monitor-the-mcp-servers-usage)
- [Exercise 2 - AI-based Memory Profiling of an Existing iFlow](exercises/ex2/)
- [Exercise 3 - Optimize Groovy Scripts with AI](exercises/ex3/)

> **Note on structure:** The session's topics map to the folders as follows — *System setup* → `ex0`; *MCP servers creation* (sub-exercises 2.1–2.5) → `ex1`; *AI-based Memory Profiling* → `ex2`; *Optimize Groovy Scripts with AI* → `ex3`.

## How to obtain support

Support for the content in this repository is available during the actual time of the online session for which this content has been designed. Otherwise, you may request support via the [Issues](../../issues) tab.

## Contributing

Please read the [CONTRIBUTING.md](./CONTRIBUTING.md) to understand the contribution guidelines.

## Code of Conduct

Please read the [SAP Open Source Code of Conduct](https://github.com/SAP-samples/.github/blob/main/CODE_OF_CONDUCT.md).

## License

Copyright 2026 SAP SE or an SAP affiliate company and teched2026-IN263 contributors. Please see our [LICENSE](LICENSE) for copyright and license information. Detailed information including third-party components and their licensing/copyright information is available [via the REUSE tool](https://api.reuse.software/info/github.com/SAP-samples/teched2026-IN263).
