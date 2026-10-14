@CLAUDE-teched-2026-hands-on-recorder.md

## IN263 repo conventions (override the recorder template defaults)

- **Commit screenshots only.** Only `exercises/<ex>/images/` is tracked in git. `recording.json` and `snapshots/` are gitignored (kept locally for replay/regeneration), despite the recorder template saying they are committed.
- **Python / venv.** The recorder needs Python 3.10+. This repo uses `python3.11` in a venv named **`.venv-recorder`** (not `.venv`), so run the CLIs as `.venv-recorder/bin/hands-on-record` / `.venv-recorder/bin/hands-on-capture`.
- **Recorder package source.** Installed from `git+https://github.tools.sap/pm-ai-toolkit/teched-2026-hands-on-recorder@main` (requires a valid `github.tools.sap` token).
- **Never read `.env` password values into context.** Credentials are redacted to `${KEY}` in recordings.
