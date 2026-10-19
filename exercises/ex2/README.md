# Exercise 2 - AI-based Memory Profiling of an Existing iFlow

In this exercise, you will run **AI-based Memory Profiling** on an existing integration flow. This feature enhances the integration flow **Simulation** by capturing flow-component-level memory allocation behavior during the simulation run and surfacing **AI-driven, guideline-aligned recommendations** for memory optimization — all before you deploy the flow to the runtime.

The recommendations are actionable, validated, and aligned with the official [Integration Flow Design Guidelines](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/integration-flow-design-guidelines).

> **References**
> - [AI-based Memory Profiling](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/ai-based-memory-profiling)
> - [Configure Simulation](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/configure-simulation)
> - [Simulation of an Integration Flow](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/simulation-of-integration-flow)

## Prerequisites

- A **tenant administrator** has enabled the **Memory Profiling** feature. In SAP Integration Suite, go to **Settings → Artificial Intelligence** and confirm that the **Memory Profiling** feature's **Activation Status** toggle is **ON**. Its description reads *"Shows the memory insights derived during simulation and suggests improvements."* (This is pre-configured for the session — see [Getting Started](../ex0/README.md).)
- An existing integration flow that does some non-trivial payload processing (for example, a flow that reads a larger message, maps it, and uses a Content Modifier / Groovy script) so that there is memory behavior worth profiling.
- If the integration flow contains a **Groovy script** step and you want to act on script recommendations:
  - The integration flow must be opened in **edit mode**, because script optimization inputs are applied in the Groovy editor.
  - A tenant administrator must have enabled the **Script Optimization** feature on the same **Settings → Artificial Intelligence** page (see Exercise 3).

> **Note:** Availability of this feature depends on your SAP Integration Suite service plan (SAP Note [2903776](https://me.sap.com/notes/2903776)). The AI features are available with the Premium and Enhanced Editions (free promotion until the January 2027 release).
>
> If your tenant is on the Cloud Foundry environment, the simulation **input payload must be < 100 KB** and the **overall output payload < 1 MB**.

## Exercise 2.1 Configure and Run a Simulation with Memory Profiling

After completing these steps you will have run a simulation of (a subset of) your integration flow with memory profiling enabled.

1. In SAP Integration Suite, choose **Design → Integrations and APIs**, open your <b>IN263 User 0XX</b> package (see [Getting Started](../ex0/README.md)), open the **Artifacts** tab, and select the integration flow you want to profile to open it in the integration flow editor.

2. Choose **Edit** (top right) to open the flow in **edit mode**. The simulation controls are only enabled in edit mode.

   > The simulation controls appear as icons in the **integration flow editor's top toolbar** (not in a separate palette). Before any input is added they read **Run Simulation** / **Clear Simulation**; once you enable memory insights on the input (step 5) the labels change to **Run Simulation and Profiling** / **Clear Simulation and Profiling**.

3. Identify the subset of the flow you want to simulate. On a **connection** (the arrow between two flow steps), click to reveal the simulation marker and set the **start point** where the simulation begins.

   > **Where you place the start point matters:** the uploaded simulation input binds as the message **body at that connection**, and the step immediately after the start point receives it. If you place the start point *after* a Content Modifier whose **Message Body** tab overwrites the body, the body is replaced before the next step sees it. Place the start point immediately **before** the step you want to feed, or make sure any preceding step passes the body through unchanged.
   >
   > You can simulate a maximum of **10 flow steps** per run; for complex flows, keep it at or below 10. Looping process calls with more than 10 iterations are not supported.

4. When you set the start point, the **Add Simulation Input** dialog opens. Provide the input in the **Header**, **Properties**, and **Body** sections. For the body you can type/paste into the **Content** area or use **Upload from File System** to load a payload file (the **File** field then shows the file name).

5. In the **AI Configuration** section at the top of the dialog, select **Enable Memory Insights** to obtain memory profiling insights after a successful run. Choose **OK** to submit the input.

6. On a later connection, set the **end point** where the simulation ends.

   > The run button stays greyed out until both a start point and an end point are defined. Simulation also fails if there are check errors in resources such as a mapping, or if a step throws at runtime (for example, a Groovy script receiving an empty body).

7. Choose **Run Simulation and Profiling**. A **Running simulation** progress dialog appears. On success, result markers appear on the simulated flow steps, and a **Message Content** panel opens at the bottom of the editor with the tabs **Headers**, **Properties**, **Body**, and **Memory Profiling Insights**.

## Exercise 2.2 Review the Memory Profiling Insights and Apply Fixes

After completing these steps you will have reviewed the AI-driven memory recommendations and applied a fix.

1. In the **Message Content** panel, open the **Memory Profiling Insights** tab (marked with an AI/sparkle icon). It shows a **Summary** with the disclaimer *"Created with AI. Verify before use."* and a table of the **top memory-contributing hotspots** with three columns:
   - **Observations** — the **Severity** (High / Medium / Low) and the *Hotspot caused by* description.
   - **Stack Traces** — the processing path to the hotspot (for example, `Message Processing → Groovy Script (script1) → JSON data iteration → Lazy map property access`).
   - **Recommended Solution** — *Why it is a problem* plus concrete *Recommended Mitigations*.

   Use **Show More** / **Show Less** to expand each cell, and **Download** (top right of the panel) to save the report. For example, a High-severity finding on a Groovy script might read: *"Repeated lazy map construction during iteration over JSON data creates excessive memory allocations and increases garbage collection overhead"* with mitigations such as parsing the JSON once and extracting only the required fields before the loop.

2. Review the reported hotspots, starting with the highest severity.

3. Apply a suggested fix to the relevant flow step. For example, adjust a Content Modifier, streaming/splitter configuration, or data store usage as recommended.

4. If a hotspot is in a **Groovy script** step, choose **Optimize in Editor** (top right of the Memory Profiling Insights panel). This opens the Groovy script editor, where a banner confirms *"The Optimization recommendations also include memory profiling insights from Simulation."* There, choose **Optimize** to generate the AI Review Report (which combines the memory profiling insights with GenAI script-optimization suggestions). See [Exercise 3 - Optimize Groovy Scripts with AI](../ex3/README.md) for the detailed script-optimization flow.

5. **Re-run** the simulation (**Run Simulation and Profiling**) to confirm the improvement and to surface the next most significant memory issue. Repeat until you are satisfied.

   > **Note:** Changing the integration flow mode (Read ↔ Edit) or choosing **Clear Simulation and Profiling** discards the simulation configuration and data (including the uploaded input).

## Summary

You have run an integration flow simulation with AI-based memory profiling, identified the top memory-contributing steps, reviewed guideline-aligned AI recommendations, applied fixes, and re-run the simulation to validate the improvement — proactively optimizing the flow **before** deploying it to the runtime.

Continue to - [Exercise 3 - Optimize Groovy Scripts with AI](../ex3/README.md)
