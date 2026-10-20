# Exercise 2 — AI-based Memory Profiling of an Existing iFlow

In this exercise, you will run **AI-based Memory Profiling** on an existing integration flow. This feature enhances the integration flow **Simulation** by capturing flow-component-level memory allocation behavior during the simulation run and surfacing **AI-driven, guideline-aligned recommendations** for memory optimization — all before you deploy the flow to the runtime.

## Overview

Memory problems in an integration flow usually surface only at runtime, under load, when they are expensive to diagnose and fix. AI-based Memory Profiling moves that feedback to **design time**: you simulate a subset of the flow, the profiler records how much memory each step allocates, and Generative AI turns the hotspots into concrete, [Integration Flow Design Guidelines](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/integration-flow-design-guidelines)-aligned recommendations you can act on immediately — including a bridge into Groovy **Script Optimization** (Exercise 3).

> [!NOTE]
> This exercise profiles the integration flow **`SalesOrder_MemoryProfiling_0XX`** in your **`IN263 User 0XX`** package (see [Getting Started](../ex0/README.md)) *(replace `XX` with your assigned participant number)*. It reads a larger SalesOrder message and transforms it with a Content Modifier and a Groovy script, so there is memory behavior worth profiling. The instructor's reference package uses **`SalesOrder_MemoryProfiling_XX`**.

> **References**
> - [AI-based Memory Profiling](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/ai-based-memory-profiling)
> - [Configure Simulation](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/configure-simulation)
> - [Simulation of an Integration Flow](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/simulation-of-integration-flow)

## Prerequisites

- The **Memory Profiling** feature is enabled in your tenant (**Settings → Artificial Intelligence**, **Activation Status** toggle **ON**). This is pre-configured for the session — see [Getting Started](../ex0/README.md).
- The integration flow **`SalesOrder_MemoryProfiling_0XX`** in your **`IN263 User 0XX`** package — a flow that does some non-trivial payload processing (a Content Modifier and a Groovy script over a larger SalesOrder message).
- To act on **Groovy script** recommendations, the **Script Optimization** feature must also be enabled (same **Settings → Artificial Intelligence** page — see [Exercise 3](../ex3/README.md)).

> [!IMPORTANT]
> If your tenant is on the Cloud Foundry environment, the simulation **input payload must be < 100 KB** and the **overall output payload < 1 MB**. AI features require the Premium or Enhanced Edition (free promotion until the January 2027 release — SAP Note [2903776](https://me.sap.com/notes/2903776)).

## Exercise 2.1 — Configure and Run a Simulation with Memory Profiling

In this sub-exercise you will run a simulation of a subset of your integration flow with memory profiling enabled.

1. In SAP Integration Suite, choose **Design → Integrations and APIs**, open your <b>IN263 User 0XX</b> package, open the **Artifacts** tab, and select the integration flow **`SalesOrder_MemoryProfiling_0XX`** to open it in the integration flow editor.

   <br>![Open the integration flow from your package Artifacts tab](/exercises/ex2/images/ex2_01_open_iflow.png)

2. Choose **Edit** (top right) to open the flow in **edit mode**. The simulation controls are only enabled in edit mode.

   <br>![Open the integration flow in edit mode](/exercises/ex2/images/ex2_02_edit_mode.png)

   > [!NOTE]
   > The simulation controls appear as icons in the integration flow editor's **top toolbar** (not in a separate palette). Before any input is added they read **Run Simulation** / **Clear Simulation**; once you enable memory insights on the input (step 6) the labels change to **Run Simulation and Profiling** / **Clear Simulation and Profiling**.

3. Identify the subset of the flow you want to simulate, and set the **start point** where the simulation begins. Hover over the flow step *before* which the simulation should start (here, the **Content Modifier**) and choose **Simulation Start Point** from the step's action palette — a start marker appears on the incoming connection.

   <br>![Set the simulation start point](/exercises/ex2/images/ex2_03_set_start_point.png)

   > [!IMPORTANT]
   > **Where you place the start point matters.** The uploaded simulation input binds as the message **body at that connection**, and the step immediately after the start point receives it. If you place the start point *after* a Content Modifier whose **Message Body** tab overwrites the body, the body is replaced before the next step sees it. Place the start point immediately **before** the step you want to feed, or make sure any preceding step passes the body through unchanged.
   >
   > You can simulate a maximum of **10 flow steps** per run; looping process calls with more than 10 iterations are not supported.

4. Set the **end point** where the simulation ends. Hover over the last step you want to include (here, **Groovy Script 1**) and choose **Simulation End Point** from its action palette — an end marker appears on the outgoing connection. The simulated subset in this example is the **Content Modifier** and the **Groovy Script**.

   <br>![Set the simulation end point](/exercises/ex2/images/ex2_04_set_end_point.png)

   > [!NOTE]
   > The run button stays greyed out until **both** a start point and an end point are defined. Simulation also fails if there are check errors in a resource (such as a mapping), or if a step throws at runtime (for example, a Groovy script receiving an empty body).

5. Click the **Simulation Start Point** marker to open the **Add Simulation Input** dialog, and provide the input the simulated subset will run against:

   | Field | Value |
   |---|---|
   | **Header** | *Optional.* Any headers the simulated subset needs. |
   | **Properties** | *Optional.* Any exchange properties the simulated subset needs. |
   | **Body** | The message payload. Choose **Upload from File System** to load the sample payload [`salesOrder_complex.xml`](assets/salesOrder_complex.xml) (the **File** field then shows the file name and the **Content** area shows the XML), or type/paste the payload directly into **Content**. |

   <br>![Add Simulation Input dialog with the body loaded](/exercises/ex2/images/ex2_05_add_simulation_input.png)

6. In the **AI Configuration** section at the top of the dialog, select **Enable Memory Insights** to obtain memory profiling insights after a successful run. Choose **OK** to submit the input.

   <br>![Enable Memory Insights in the AI Configuration section](/exercises/ex2/images/ex2_06_enable_memory_insights.png)

7. Choose **Run Simulation and Profiling**. A **Running simulation** progress dialog appears. On success, result markers appear on the simulated flow steps, and a **Message Content** panel opens at the bottom of the editor with the tabs **Headers**, **Properties**, **Body**, and **Memory Profiling Insights**.

   <br>![Run Simulation and Profiling — result markers and Message Content panel](/exercises/ex2/images/ex2_07_run_simulation.png)

## Exercise 2.2 — Review the Memory Profiling Insights and Apply Fixes

In this sub-exercise you will review the AI-driven memory recommendations and apply a fix.

1. In the **Message Content** panel, open the **Memory Profiling Insights** tab (marked with an AI/sparkle icon). It shows a **Summary** with the disclaimer *"Created with AI. Verify before use."* and a table of the **top memory-contributing hotspots** with three columns:

   | Column | What it shows |
   |---|---|
   | **Observations** | The **Severity** (High / Medium / Low) and the *Hotspot caused by* description. |
   | **Stack Traces** | The processing path to the hotspot (for example, `Message Processing → Groovy Script (script1) → JSON data iteration → Lazy map property access`). |
   | **Recommended Solution** | *Why it is a problem* plus concrete *Recommended Mitigations*. |

   <br>![Memory Profiling Insights panel with hotspots](/exercises/ex2/images/ex2_08_memory_insights.png)

   > [!TIP]
   > Use **Show More** / **Show Less** to expand each cell, and **Download** (top right of the panel) to save the report.

2. Review the reported hotspots, starting with the highest severity.

3. Apply a suggested fix to the relevant flow step — for example, adjust a Content Modifier, a streaming/splitter configuration, or data store usage as recommended.

4. If a hotspot is in a **Groovy script** step, choose **Optimize in Editor** (top right of the Memory Profiling Insights panel). This opens the Groovy script editor with a banner that reads *"The Optimization recommendations also include memory profiling insights from Simulation."* There, choose **Optimize** to generate the AI Review Report.

   <br>![Optimize in Editor opens the Groovy editor with the memory-insights banner](/exercises/ex2/images/ex2_09_optimize_in_editor.png)

   > See [Exercise 3 — Optimize Groovy Scripts with AI](../ex3/README.md) for the detailed script-optimization flow.

5. **Re-run** the simulation (**Run Simulation and Profiling**) to confirm the improvement and surface the next most significant memory issue. Repeat until you are satisfied.

   > [!NOTE]
   > Changing the integration flow mode (Read ↔ Edit) or choosing **Clear Simulation and Profiling** discards the simulation configuration and data (including the uploaded input).

## Summary

You have now run an integration flow simulation with AI-based memory profiling. In doing so you have:

- Configured a simulation subset and provided an input payload with **Enable Memory Insights** turned on.
- Reviewed the **Memory Profiling Insights** — the top memory-contributing hotspots with guideline-aligned AI recommendations.
- Applied a fix and re-run the simulation to validate the improvement — proactively optimizing the flow **before** deploying it to the runtime.

Continue to - [Exercise 3 — Optimize Groovy Scripts with AI](../ex3/README.md)
