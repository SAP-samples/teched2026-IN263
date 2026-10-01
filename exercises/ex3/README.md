# Exercise 3 - Optimize Groovy Scripts with AI

In this exercise, you will use the **Script Optimization** feature to improve a Groovy script with Generative AI. Script Optimization applies GenAI best practices to your Groovy scripts in the script editor, focusing on **resource usage** (memory and CPU), and produces an **AI Review Report** with concrete recommendations and an improved version of the script that you can apply.

> **Reference:** [Optimize Groovy Scripts with AI](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/optimize-groovy-scripts-with-ai)

## Prerequisites

- The **Script Optimization** feature is enabled in your tenant (**Settings → Artificial Intelligence** — done in [Getting Started, Exercise 0.2](../ex0/README.md)). See [Artificial Intelligence](https://help.sap.com/docs/integration-suite/isuite-integrations-and-apis/artificial-intelligence).
- Your user has the `WebToolingWorkspace.Write` role collection.
- An integration flow that contains a **Groovy** script step (not JavaScript).

> **Notes and limits:**
> - This feature is available only with the **Premium** service plan of SAP Integration Suite (AI features free promotion until the January 2027 release). See SAP Notes [2903776](https://me.sap.com/notes/2903776) and [3463620](https://me.sap.com/notes/3463620).
> - The Groovy script must be **≤ 50 KB**.
> - **Do not** expose sensitive information in scripts you submit to **Optimize**. See SAP Note [3542713](https://me.sap.com/notes/3542713).
> - `Optimize` can be used repeatedly, but there is a per-minute rate limit and a monthly fair-usage token quota.

## Exercise 3.1 Run the AI Review on a Groovy Script

After completing these steps you will have generated an AI Review Report for your Groovy script.

1. In SAP Integration Suite, open the integration flow that contains the Groovy script, and open the **script editor in edit mode** (double-click the Groovy script step, then edit the script).

2. Choose **Optimize** from the upper-right menu of the script editor.

3. Generative AI inspects your script and displays an **AI Review Report**. If there is potential for improvement, the report contains:
   - **Recommended Changes** — individual areas for improvement, each with a brief explanation and relevant code snippets.
   - **Improved Script** — a complete script showing how to apply all the recommendations together.

   > If the AI finds no issues, the report confirms: *"The provided Groovy script is well structured and follows best practices for memory and CPU optimization. No further improvements are necessary."*

4. (Optional) Choose **Download** to save the AI report and the original script to your local file system.

## Exercise 3.2 Apply the Recommendations

After completing these steps you will have applied the AI's suggestions to your script.

1. Review the **Recommended Changes** and the **Improved Script** in the report.

2. Apply the changes to the original script in one of two ways:
   - **Selective:** copy individual snippets from the **Recommended Changes** section and paste them into the correct lines in the script editor; or
   - **Full replacement:** copy the full script from the **Improved Script** section and paste it into the editor to replace the original.

3. Once you are satisfied with your changes, choose **Apply**.

4. (Recommended) Validate the optimized script by re-running the integration flow **Simulation** (see [Exercise 2](../ex2/README.md)) — ideally with **Memory Profiling** enabled — to confirm the resource-usage improvement before deployment.

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

You have used Script Optimization to generate an AI Review Report for a Groovy script, reviewed the GenAI recommendations and improved script, applied them, and (optionally) validated the result through simulation with memory profiling.

This completes the IN263 hands-on exercises. You have experienced design-time quality for the AI era end to end: governed MCP servers as a secure gateway for AI agents, AI-based memory profiling to catch issues before deployment, and GenAI-assisted Groovy script optimization.
