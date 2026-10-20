# Exercise 3 — Optimize Groovy Scripts with AI

In this exercise, you will use the **Script Optimization** feature to improve a Groovy script with Generative AI. Script Optimization applies GenAI best practices to your Groovy scripts in the script editor, focusing on **resource usage** (memory and CPU), and produces an **AI Review Report** with concrete recommendations and an improved version of the script that you can apply.

## Overview

Groovy scripts are where integration flows most often leak memory or burn CPU — a map rebuilt inside a loop, the whole payload held as a `String`, an inefficient parse. **Script Optimization** sends your script to Generative AI, which returns a structured **AI Review Report**: specific recommended changes with code snippets, plus a complete improved script you can drop in. It pairs naturally with Exercise 2 — a memory-profiling hotspot on a Groovy step links straight into this editor.

> [!NOTE]
> This exercise optimizes the Groovy script inside the **`SalesOrder_MemoryProfiling_0XX`** flow in your **`IN263 User 0XX`** package (see [Getting Started](../ex0/README.md)) *(replace `XX` with your assigned participant number; the instructor's reference package uses **`SalesOrder_MemoryProfiling_XX`**)*. If you arrived here from a memory-profiling finding in [Exercise 2](../ex2/README.md), you are already in the right editor — skip to [Exercise 3.1](#exercise-31--run-the-ai-review-on-a-groovy-script) step 2.

> **Reference:** [Optimize Groovy Scripts with AI](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/optimize-groovy-scripts-with-ai)

## Prerequisites

- The **Script Optimization** feature is enabled in your tenant (**Settings → Artificial Intelligence**) — pre-configured for the session, see [Getting Started](../ex0/README.md).
- Your user has the `WebToolingWorkspace.Write` role collection (pre-assigned — see [Getting Started](../ex0/README.md)).
- The integration flow **`SalesOrder_MemoryProfiling_0XX`**, which contains a **Groovy** script step (not JavaScript), in your **`IN263 User 0XX`** package.

> [!IMPORTANT]
> - The Groovy script must be **≤ 50 KB**.
> - **Do not** expose sensitive information in scripts you submit to **Optimize** (SAP Note [3542713](https://me.sap.com/notes/3542713)).
> - **Optimize** can be used repeatedly, but there is a per-minute rate limit and a monthly fair-usage token quota.
> - AI features require the Premium or Enhanced Edition (free promotion until the January 2027 release — SAP Notes [2903776](https://me.sap.com/notes/2903776) and [3463620](https://me.sap.com/notes/3463620)).

## Exercise 3.1 — Run the AI Review on a Groovy Script

In this sub-exercise you will generate an AI Review Report for your Groovy script.

1. In SAP Integration Suite, open the **`SalesOrder_MemoryProfiling_0XX`** integration flow and choose **Edit** to enter edit mode. Double-click the **Groovy Script** step to open its properties, open the **Processing** tab, and choose the script filename shown in the **Script File** field (`/script/MemoryHotspot.groovy`) to open the **script editor**.

   <br>![Open the Groovy script editor in edit mode](/exercises/ex3/images/ex3_01_script_editor.png)

   > [!TIP]
   > You can also reach this script editor from Exercise 2: on the **Memory Profiling Insights** panel, choose **Optimize in Editor** (see [Exercise 2.3](../ex2/README.md#exercise-23--review-the-memory-profiling-insights-and-apply-fixes)). That path opens the editor with a banner noting that *"The Optimization recommendations also include memory profiling insights from Simulation."*

2. Choose **Optimize** from the upper-right toolbar of the script editor.

   <br>![Choose Optimize in the script editor](/exercises/ex3/images/ex3_02_optimize_button.png)

3. Generative AI inspects your script and displays an **AI Review Report**. If there is potential for improvement, the report contains:

   | Section | What it shows |
   |---|---|
   | **Recommended Changes** | Individual areas for improvement, each with a brief explanation and relevant code snippets. |
   | **Improved Script** | A complete script showing how to apply all the recommendations together. |

   <br>![AI Review Report with recommended changes and improved script](/exercises/ex3/images/ex3_03_ai_review_report.png)

   > [!NOTE]
   > If the AI finds no issues, the report confirms: *"The provided Groovy script is well structured and follows best practices for memory and CPU optimization. No further improvements are necessary."*

4. (Optional) Choose **Download** to save the AI report and the original script to your local file system.

## Exercise 3.2 — Apply the Recommendations

In this sub-exercise you will apply the AI's suggestions to your script.

1. Review the **Recommended Changes** and the **Improved Script** in the report.

2. Apply the changes to the original script in one of two ways:
   - **Selective:** copy individual snippets from the **Recommended Changes** section and paste them into the correct lines in the script editor; or
   - **Full replacement:** copy the full script from the **Improved Script** section and paste it into the editor to replace the original.

3. Once you are satisfied with your changes, choose **Apply**. This writes the optimized script back into the script editor.

   <br>![Apply the optimized script](/exercises/ex3/images/ex3_04_apply.png)

4. Choose **Save** to save the integration flow. Applying the script only updates the editor — the change is not persisted until you save the flow.

5. (Recommended) Validate the optimized script by re-running the integration flow **Simulation** (see [Exercise 2](../ex2/README.md)) — ideally with **Memory Profiling** enabled — to confirm the resource-usage improvement before deployment.

## Troubleshooting

If you do not see the **Optimize** button, check that:
- The AI features are enabled (see [Artificial Intelligence](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/artificial-intelligence)).
- You are using the script editor in **edit mode**.
- You are editing a **Groovy** script, not JavaScript.

Common error messages and what to do:

| Error message | Action |
| --- | --- |
| *Generative AI feature not enabled.* | Enable the AI features in Settings → Artificial Intelligence. |
| *You have exceeded the rate limit, please try again later.* | Wait one minute before retrying (per-minute invocation limit). |
| *Authentication failed.* | Open an incident on component `LOD-HCI-PI-OPS`. |
| *You are not authorized to perform this operation...* | Ask your tenant administrator to grant the `WebToolingWorkspace.Write` role. |
| *No Groovy script has been provided.* | Check the contents of the code editor. |
| *The provided Groovy script content is too large.* | Shorten the script to ≤ 50 KB. |
| *...you have reached your fair usage token quota.* | Wait until the monthly quota regenerates. |
| *The AI request cannot be processed at the moment...* | Try again later; if it persists, open an incident on `LOD-HCI-PI-RC`. |

## Summary

You have now used Script Optimization to improve a Groovy script with Generative AI. In doing so you have:

- Generated an **AI Review Report** for a Groovy script.
- Reviewed the GenAI **Recommended Changes** and **Improved Script**, and applied them.
- (Optionally) validated the result through simulation with memory profiling.

This completes the IN263 hands-on exercises. You have experienced design-time quality for the AI era end to end: **governed MCP servers** as a secure gateway for AI agents, **AI-based memory profiling** to catch issues before deployment, and **GenAI-assisted Groovy script optimization**.
