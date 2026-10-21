# Exercise 2 — AI-based Memory Profiling of an iFlow

In this exercise, you will build a small integration flow and run **AI-based Memory Profiling** on it. This feature enhances the integration flow **Simulation** by capturing flow-component-level memory allocation behavior during the simulation run and surfacing **AI-driven, guideline-aligned recommendations** for memory optimization — all before you deploy the flow to the runtime.

## Overview

Memory problems in an integration flow usually surface only at runtime, under load, when they are expensive to diagnose and fix. AI-based Memory Profiling moves that feedback to **design time**: you simulate a subset of the flow, the profiler records how much memory each step allocates, and Generative AI turns the hotspots into concrete, [Integration Flow Design Guidelines](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/integration-flow-design-guidelines)-aligned recommendations you can act on immediately — including a bridge into Groovy **Script Optimization** (Exercise 3).

> [!NOTE]
> This exercise builds and profiles the integration flow **`SalesOrder_MemoryProfiling_0XX`** in your **`IN263 User 0XX`** package (see [Getting Started](../ex0/README.md)) *(replace `XX` with your assigned participant number)*. The flow reads a larger SalesOrder message and transforms it with a Content Modifier and a Groovy script, so there is memory behavior worth profiling. The instructor's reference package uses **`SalesOrder_MemoryProfiling_XX`**.

> **References**
> - [AI-based Memory Profiling](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/ai-based-memory-profiling)
> - [Configure Simulation](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/configure-simulation)
> - [Simulation of an Integration Flow](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/simulation-of-integration-flow)

## Prerequisites

- The **Memory Profiling** feature is enabled in your tenant (**Settings → Artificial Intelligence**, **Activation Status** toggle **ON**). This is pre-configured for the session — see [Getting Started](../ex0/README.md).
- Your **`IN263 User 0XX`** package (from [Getting Started](../ex0/README.md)), in which you will build the flow.
- The sample payload [`salesOrder_complex.xml`](assets/salesOrder_complex.xml) and the Groovy script [`MemoryHotspot.groovy`](assets/MemoryHotspot.groovy) from this exercise's `assets/` folder.
- To act on **Groovy script** recommendations, the **Script Optimization** feature must also be enabled (same **Settings → Artificial Intelligence** page — see [Exercise 3](../ex3/README.md)).

> [!IMPORTANT]
> If your tenant is on the Cloud Foundry environment, the simulation **input payload must be < 100 KB** and the **overall output payload < 1 MB**. AI features require the Premium or Enhanced Edition (free promotion until the January 2027 release — SAP Note [2903776](https://me.sap.com/notes/2903776)).

## Exercise 2.1 — Build the Integration Flow

In this sub-exercise you will create the integration flow you will profile: **Start → Content Modifier → Groovy Script → End**.

1. In SAP Integration Suite, choose **Design → Integrations and APIs**, open your <b>IN263 User 0XX</b> package, choose **Edit**, open the **Artifacts** tab, and choose **Add → Integration Flow**. In the dialog, choose **Add Integrations manually**.

   <br>![Add an Integration Flow artifact](/exercises/ex2/images/ex2_01_add_artifact.png)

   <br>![Choose Add Integrations manually](/exercises/ex2/images/ex2_01b_add_integrations_manually.png)

2. Keep **Create** selected and enter the name <b>SalesOrder_MemoryProfiling_0XX</b> *(replace XX with your participant number)*; the **ID** fills in automatically. Leave **Runtime Profile** as **Cloud Integration** and choose **Add and Open in Editor**.

   <br>![Name the integration flow](/exercises/ex2/images/ex2_02_create_iflow.png)

3. Choose **Edit** to open the flow in **edit mode**. Using the connection between **Start** and **End**, add two steps using the **+** button so the flow reads **Start → Content Modifier → Groovy Script → End**:
   - a **Content Modifier** (**Transformation → Content Modifier**), and
   - a **Groovy Script** (**Message Transformers → Script → Groovy Script**).

   <br>![Add a flow step from the palette](/exercises/ex2/images/ex2_03a_add_flow_step.png)

   <br>![Flow with Content Modifier and Groovy Script](/exercises/ex2/images/ex2_03_flow_built.png)

4. Double-click the **Content Modifier**, open the **Message Header** tab, and choose **Add** to set a header (for example, **Name** `SourceSystem`, **Source Type** `Constant`, **Source Value** `SalesOrderXML`). Leave the **Message Body** tab empty so the incoming SalesOrder payload passes through unchanged to the Groovy script.

   <br>![Configure the Content Modifier](/exercises/ex2/images/ex2_04_content_modifier_config.png)

5. Double-click the **Groovy Script** step, open the **Processing** tab, and choose **Select → Upload from File System** to assign the script [`MemoryHotspot.groovy`](assets/MemoryHotspot.groovy). The **Script File** field then shows `/script/MemoryHotspot.groovy`.

   <br>![Open the processing screen](/exercises/ex2/images/ex2_05b_open_processing.png)

   <br>![Select Resource dialog with Upload from File System](/exercises/ex2/images/ex2_05a_upload_from_file_system.png)

   <br>![Assign the Groovy script file](/exercises/ex2/images/ex2_05_groovy_script_setup.png)

   > [!NOTE]
   > `MemoryHotspot.groovy` parses the SalesOrder XML with the standard Java DOM parser (`javax.xml.parsers`) and flattens every order line into a CSV report in a **deliberately memory-inefficient** way — building the report with the String-concatenation anti-pattern inside a loop and keeping parallel copies in memory. This gives the profiler a clear, high-severity hotspot to find. It uses only classes available on the Integration Cell Groovy runtime (it does **not** use `XmlSlurper`).

6. Open the script (choose the script filename shown in the **Script File** field) to review it in the editor.

   <br>![Review the Groovy script in the editor](/exercises/ex2/images/ex2_06_groovy_script_content.png)

7.  **Save** the integration flow.

## Exercise 2.2 — Configure and Run a Simulation with Memory Profiling

In this sub-exercise you will run a simulation of a subset of your integration flow with memory profiling enabled.

> [!NOTE]
> The simulation controls appear as icons in the integration flow editor's **top toolbar** (not in a separate palette). Before any input is added they read **Run Simulation** / **Clear Simulation**; once you enable memory insights on the input (step 3) the labels change to **Run Simulation and Profiling** / **Clear Simulation and Profiling**.

1. With the flow open in **edit mode**, set the **start point** where the simulation begins. Hover over the flow step *before* which the simulation should start (here, the **Content Modifier**) and choose **Simulation Start Point** from the step's action palette — a start marker appears on the incoming connection.

   <br>![Set the simulation start point](/exercises/ex2/images/ex2_07_set_start_point.png)

   > [!IMPORTANT]
   > **Where you place the start point matters.** The uploaded simulation input binds as the message **body at that connection**, and the step immediately after the start point receives it. If you place the start point *after* a Content Modifier whose **Message Body** tab overwrites the body, the body is replaced before the next step sees it. Place the start point immediately **before** the step you want to feed, or make sure any preceding step passes the body through unchanged.
   >
   > You can simulate a maximum of **10 flow steps** per run; looping process calls with more than 10 iterations are not supported.

2. Set the **end point** where the simulation ends. Hover over the last step you want to include (here, **Groovy Script 1**) and choose **Simulation End Point** from its action palette — an end marker appears on the outgoing connection. The simulated subset in this example is the **Content Modifier** and the **Groovy Script**.

   <br>![Set the simulation end point](/exercises/ex2/images/ex2_08_set_end_point.png)

   > [!NOTE]
   > The run button stays greyed out until **both** a start point and an end point are defined. Simulation also fails if there are check errors in a resource (such as a mapping), or if a step throws at runtime (for example, a Groovy script receiving an empty body).

3. Click the **Simulation Start Point** marker to open the **Add Simulation Input** dialog, and provide the input the simulated subset will run against.

   First, at the **top** of the dialog in the **AI Configuration** section, select **Enable Memory Insights** to obtain memory profiling insights after a successful run. Then fill in the fields below:

   | Field | Value |
   |---|---|
   | **Header** | *Optional.* Any headers the simulated subset needs. |
   | **Properties** | *Optional.* Any exchange properties the simulated subset needs. |
   | **Body** | The message payload. Choose **Upload from File System** to load the sample payload [`salesOrder_complex.xml`](assets/salesOrder_complex.xml) (the **File** field then shows the file name and the **Content** area shows the XML), or type/paste the payload directly into **Content**. |

   <br>![Add Simulation Input with Enable Memory Insights](/exercises/ex2/images/ex2_09_simulation_input.png)

4. Choose **OK** to submit the input, then choose **Run Simulation and Profiling**. A **Running simulation** progress dialog appears. On success, result markers appear on the simulated flow steps, and a **Message Content** panel opens at the bottom of the editor with the tabs **Headers**, **Properties**, **Body**, and **Memory Profiling Insights**.

   <br>![Run Simulation and Profiling — result markers and Message Content panel](/exercises/ex2/images/ex2_10_run_simulation.png)

## Exercise 2.3 — Review the Memory Profiling Insights and Apply Fixes

In this sub-exercise you will review the AI-driven memory recommendations and apply a fix.

1. In the **Message Content** panel, open the **Memory Profiling Insights** tab (marked with an AI/sparkle icon). It shows a **Summary** with the disclaimer *"Created with AI. Verify before use."* and a table of the **top memory-contributing hotspots** with three columns:

   | Column | What it shows |
   |---|---|
   | **Observations** | The **Severity** (High / Medium / Low) and the *Hotspot caused by* description. |
   | **Stack Traces** | The processing path to the hotspot (for example, `Message Processing → Groovy Script (MemoryHotspot) → String concatenation using plus operator → Buffer expansion → Byte array allocation`). |
   | **Recommended Solution** | *Why it is a problem* plus concrete *Recommended Mitigations*. |

   <br>![Memory Profiling Insights panel with hotspots](/exercises/ex2/images/ex2_11_memory_insights.png)

   > [!TIP]
   > Use **Show More** / **Show Less** to expand each cell, and **Download** (top right of the panel) to save the report.

2. Review the reported hotspots, starting with the highest severity.

3. Apply a suggested fix to the relevant flow step — for example, adjust a Content Modifier, a streaming/splitter configuration, or data store usage as recommended.

4. If a hotspot is in a **Groovy script** step, choose **Optimize in Editor** (top right of the Memory Profiling Insights panel). This opens the Groovy script editor with a banner that reads *"The Optimization recommendations also include memory profiling insights from Simulation."* From here you would run **Optimize** to generate the AI Review Report — but **don't trigger it yet**: the script-optimization flow is covered step by step in the next exercise.

   <br>![Optimize in Editor opens the Groovy editor with the memory-insights banner](/exercises/ex2/images/ex2_12_optimize_in_editor.png)

   > Continue with [Exercise 3 — Optimize Groovy Scripts with AI](../ex3/README.md), which explains the **Optimize** action and the AI Review Report in detail.

5. Optionally you can **Re-run** the simulation (**Run Simulation and Profiling**) to confirm the improvement and surface the next most significant memory issue. Repeat until you are satisfied.

   > [!NOTE]
   > Changing the integration flow mode (Read ↔ Edit) or choosing **Clear Simulation and Profiling** discards the simulation configuration and data (including the uploaded input).

## Summary

You have now run an integration flow simulation with AI-based memory profiling. In doing so you have:

- Configured a simulation subset and provided an input payload with **Enable Memory Insights** turned on.
- Reviewed the **Memory Profiling Insights** — the top memory-contributing hotspots with guideline-aligned AI recommendations.
- Applied a fix and re-run the simulation to validate the improvement — proactively optimizing the flow **before** deploying it to the runtime.

Continue to - [Exercise 3 — Optimize Groovy Scripts with AI](../ex3/README.md)
