# Exercise 2 - AI-based Memory Profiling of an Existing iFlow

In this exercise, you will run **AI-based Memory Profiling** on an existing integration flow. This feature enhances the integration flow **Simulation** by capturing flow-component-level memory allocation behavior during the simulation run and surfacing **AI-driven, guideline-aligned recommendations** for memory optimization — all before you deploy the flow to the runtime.

The recommendations are actionable, validated, and aligned with the official [Integration Flow Design Guidelines](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/integration-flow-design-guidelines).

> **References**
> - [AI-based Memory Profiling](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/ai-based-memory-profiling)
> - [Configure Simulation](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/configure-simulation)
> - [Simulation of an Integration Flow](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/simulation-of-integration-flow)

## Prerequisites

- A **tenant administrator** has enabled the **Memory Profiling** feature in **Settings → Artificial Intelligence** (done in [Getting Started, Exercise 0.2](../ex0/README.md)).
- An existing integration flow that does some non-trivial payload processing (for example, a flow that reads a larger message, maps it, and uses a Content Modifier / Groovy script) so that there is memory behavior worth profiling.
- If the integration flow contains a **Groovy script** step and you want to act on script recommendations:
  - The integration flow must be opened in **edit mode**, because script optimization inputs are applied in the Groovy editor.
  - A tenant administrator must have enabled the **Script Optimization** feature in **Settings → Artificial Intelligence** (see Exercise 3).

> **Note:** Availability of this feature depends on your SAP Integration Suite service plan (SAP Note [2903776](https://me.sap.com/notes/2903776)). The AI features are available with the Premium and Enhanced Editions (free promotion until the January 2027 release).
>
> If your tenant is on the Cloud Foundry environment, the simulation **input payload must be < 100 KB** and the **overall output payload < 1 MB**.

## Exercise 2.1 Configure and Run a Simulation with Memory Profiling

After completing these steps you will have run a simulation of (a subset of) your integration flow with memory profiling enabled.

1. In SAP Integration Suite, choose **Design → Integrations and APIs**, open your integration package, and select the integration flow you want to profile.

2. Locate the **Simulation** tool on the palette. It provides:
   - **Run Simulation** — execute the simulation.
   - **Clear Simulation** — clear a previous simulation (this cannot be undone).

3. Identify the subset of the flow you want to simulate. On a connection, choose the **start point** icon to mark where the simulation begins.

   > You can simulate a maximum of **10 flow steps** per run; for complex flows, keep it at or below 10. Looping process calls with more than 10 iterations are not supported.

4. When you set the start point, a dialog opens to **add simulation input**. Provide the input — **Payload**, **Headers**, and/or **Properties**. You can also upload the input payload from your local file system. See [Using Various Types of Body Files in the Simulation](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/using-various-types-of-body-files-in-simulation).

5. Under **AI configuration**, select **Enable Memory Profiling** to obtain memory profiling insights after a successful run. Choose **OK** to submit the input.

6. On a later connection, choose the **end point** icon to mark where the simulation ends.

   > The **Run Simulation** button stays greyed out until an end point is defined. Simulation also fails if there are check errors in resources such as a mapping.

7. Choose **Run Simulation**. On success, a **message envelope** appears next to each flow component with tracing information; choose an envelope to see the details for that step.

## Exercise 2.2 Review the Memory Profiling Insights and Apply Fixes

After completing these steps you will have reviewed the AI-driven memory recommendations and applied a fix.

1. For each flow step, choose the result icon to view the simulation result. For the **top memory-contributing flow steps**, a **Memory Profiling Insights** tab is shown, containing:
   - Data on the step's **memory allocation behavior**.
   - **AI insights and optimization recommendations**, aligned with the integration flow design guidelines.

2. Review the reported **top memory contributors** and their recommendations.

3. Apply a suggested fix to the relevant flow step. For example, adjust a Content Modifier, streaming/splitter configuration, or data store usage as recommended.

4. If a top memory contributor is a **Groovy script** step:
   - Choose **Optimize in Editor** to navigate to the script editor.
   - Choose **Optimize** to view the recommendations, which also include memory-related insights and optimization suggestions where applicable.
   - See [Exercise 3 - Optimize Groovy Scripts with AI](../ex3/README.md) for the detailed script-optimization flow.

5. **Re-run** the simulation to confirm the improvement and to surface the next most significant memory issue. Repeat until you are satisfied.

   > **Note:** Changing the integration flow mode (Read ↔ Edit) or clearing the simulation discards the simulation configuration and data.

## Summary

You have run an integration flow simulation with AI-based memory profiling, identified the top memory-contributing steps, reviewed guideline-aligned AI recommendations, applied fixes, and re-run the simulation to validate the improvement — proactively optimizing the flow **before** deploying it to the runtime.

Continue to - [Exercise 3 - Optimize Groovy Scripts with AI](../ex3/README.md)
