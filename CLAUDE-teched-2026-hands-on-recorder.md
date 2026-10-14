# Hands-On Exercise Recorder

Automated screenshot capture and interactive recording for SAP TechEd hands-on workshop exercises.

## Folder structure

```
.env-example             # Credential template — copy to .env, fill in real values
.env                     # Actual credentials (gitignored)
exercises/
  <exName>/
    README.md            # Exercise content (written by hand or with agent assistance)
    recording.json       # JSON recording (used by hands-on-capture)
    images/              # Screenshots (auto-generated, committed to repo)
    snapshots/           # DOM shadow copies (auto-generated alongside screenshots)
      <exName>_NN.yaml       # Full accessibility tree at time of screenshot
      <exName>_NN.summary.md # Agent-readable summary (URL, tabs, headings, links, annotations)
```

## Setup

```bash
python3 -m venv .venv
.venv/bin/pip install teched-2026-hands-on-recorder
# Or from GitHub:
# .venv/bin/pip install git+https://github.tools.sap/pm-ai-toolkit/teched-2026-hands-on-recorder@main

# Scaffold the project (creates exercises/, .env-example, .gitignore entries, this file)
.venv/bin/hands-on-init
```

The `hands-on-init` command is idempotent — it skips files that already exist. Run it again after upgrading the recorder package to pick up template changes.

## Creating a new exercise (interactive recording)

```bash
# 1. Start interactive recording — opens a Textual TUI + headed browser
hands-on-record exMyExercise

# 2. The TUI shows a timeline (left), action log (right), and key bindings (footer)
#    Use keyboard shortcuts or click the footer bindings:
#      g — goto URL (navigates and settles: handles auth, cookies, dialogs)
#      c — click element (pick target via browser click or fuzzy element search)
#      f — fill text field (pick field, then enter value)
#      s — screenshot + annotate (add annotation bubbles, then capture)
#      w — wait (pause for N seconds)
#      x — custom playwright-cli command
#      e — jump to step (step picker) / in edit mode: edit current step
#      i — import steps from a codegen JS file
#      z — undo last step
#      Escape — cancel current replay operation
#      q — quit & save
#    After each action, you're asked "Screenshot here?" — annotations are auto-positioned
#    Each screenshot also saves a DOM snapshot (snapshots/*.yaml + *.summary.md)

# 3. Quit when done — recording.json is auto-saved after every step

# 4. Resume a previous session (shows timeline of existing steps, then fast-forwards)
#    Steps without screenshots are auto-annotated and captured during fast-forward
hands-on-record --resume exMyExercise

# 5. Resume to a specific step
hands-on-record --resume --step 3 exMyExercise

# 6. Resume with interactive review — pause at each step to confirm annotations
hands-on-record --resume --review exMyExercise
```

## Fast recording with Playwright codegen

For exercises with many clicks, record the browser flow first, then auto-generate annotations:

```bash
# 1. Record clicks (opens a second Chromium with recorder sidebar)
#    Log in manually — login steps are auto-filtered on import
npx playwright codegen --viewport-size=1440,900 https://your-start-url.com

# 2. Copy the generated JS from the codegen panel, then import:
pbpaste | hands-on-import exMyExercise
# Or from a file:
hands-on-import exMyExercise codegen-output.js

# 3. Auto-annotate all steps (screenshots + annotations generated automatically):
hands-on-record --resume exMyExercise

# 4. Review and refine interactively (optional):
hands-on-record --resume --review exMyExercise

# 5. Edit a specific step's annotations:
#    Press 'e' in the TUI to select a step, then in edit mode:
#      e — edit current step (re-pick element for click/fill, change URL for goto, re-annotate)
#      s — re-screenshot with modified annotations
#      ] — advance to next step
#      Enter — exit edit mode, continue appending
#      Escape — cancel a replay in progress
```

Note: `npx playwright codegen` opens its own browser instance with its own login session.
The recorder's `playwright-cli` uses a separate persistent browser profile.
Both tools must be installed independently (see Setup).

## Replaying screenshots (deterministic capture)

```bash
# Replay all exercises
hands-on-capture all

# Replay one exercise from its recording.json
hands-on-capture exMyExercise

# Fast-forward to step N, then capture from there
hands-on-capture --from-step 5 exMyExercise

# Debug mode (browser stays visible)
hands-on-capture --debug exMyExercise
```

## Recording JSON format

Each step in `recording.json` has an `action` and optional annotations/screenshot:

```json
{
  "exercise": "exName",
  "section": "main",
  "steps": [
    {
      "action": "goto|click|fill|wait|screenshot|custom",
      "url": "https://...",
      "locator": "getByRole('tab', { name: 'Instances (2)' })",
      "description": "human-readable element description",
      "value": "text to fill (fill action only)",
      "clickCoords": [480, 168],
      "annotations": [
        {
          "num": 1,
          "label": "Click on \"Instances (2)\"",
          "locator": "getByRole('tab', { name: 'Instances (2)' })",
          "targetBox": [393, 152, 174, 32],
          "bubblePos": [600, 98],
          "bubbleOffset": [120, -70],
          "arrowTo": [480, 168],
          "labelPos": "right"
        }
      ],
      "screenshot": "exName_01_descriptive_name",
      "snapshotFile": "snapshots/exName_01_descriptive_name.yaml",
      "summaryFile": "snapshots/exName_01_descriptive_name.summary.md"
    }
  ]
}
```

Key fields:
- **`locator`** — Playwright locator string (e.g., `getByRole(...)`, `getByText(...)`). Used for reliable replay; coordinates are fallback only.
- **`targetBox`** — Bounding box `[x, y, width, height]` of the annotated element (informational, not used by replay).
- **`bubblePos`** — Absolute `[x, y]` position of the annotation bubble. Used as fallback when `bubbleOffset` cannot be resolved.
- **`bubbleOffset`** — Annotation bubble position `[dx, dy]` relative to target element. Re-resolved at replay time via the locator.
- **`arrowTo`** — Absolute `[x, y]` position the annotation arrow points to. Used as fallback when the locator cannot be resolved.
- **`snapshotFile` / `summaryFile`** — DOM shadow copies saved alongside the screenshot.

## Understanding screenshots without vision

Every screenshot has two companion files in `snapshots/`:

- **`<name>.yaml`** — Full accessibility tree (300-400 lines). Contains all visible text, element roles, states, links with hrefs, form values, table content.
- **`<name>.summary.md`** — Compact agent-readable markdown with:
  - Page URL and title
  - Navigation breadcrumbs
  - Tabs (with active tab marked)
  - Headings
  - Links with URLs
  - Annotations on this screenshot (numbered labels)

To understand what a screenshot shows without looking at the image, read the `.summary.md` file. For detailed element-level inspection, read the `.yaml` file.

## Writing exercise READMEs from snapshots

When writing or updating exercise README.md files:
1. Read the `.summary.md` files to understand what each screenshot shows
2. Match the exercise text to what's actually visible in the screenshots
3. Reference UI elements by their exact text as seen in the snapshot (button labels, menu items, tab names)
4. If screenshots change (e.g., after re-capture), update the README text to match

## Annotation system

- Annotations are DOM-injected red (#C62828) numbered bubbles with labels and dashed SVG arrows
- Viewport is fixed at **1440x900**
- Only annotate action screenshots (click/interact steps). Data displays stay clean
- Use `labelPos: left` when target is near the right edge (x > 1200)
- During recording, bubble position is auto-calculated (diagonal offset from target). There is no manual positioning step.

## Image paths and screenshot display

- Use **relative paths** from the exercise README: `images/exTest_01_foo.png`, not `/exercises/exTest/images/...`
- Main flow screenshots are **always inline** — never folded:
  ```html
  <br><img src="images/exTest_01_cockpit_subaccount.png" />
  ```
- Non-essential screenshots (examples, supplementary detail, alternative views) use **collapsible blocks**:
  ```html
  <details>
      <summary>📷💡 View screenshot</summary>
      <br><img src="images/exTest_99_optional_detail.png" />
  </details>
  ```

## Image naming convention

Pattern: `<exName>_NN_descriptive_name.png` — exercise prefix, step number, short description.

## Credentials

- **Never read `.env` password values directly into context**
- The `.env` file is gitignored — only `.env-example` is committed
- During recording, credential values are automatically redacted in recording.json (replaced with `${KEY_NAME}`)
- During replay, `${KEY_NAME}` placeholders are resolved from the `.env` file
