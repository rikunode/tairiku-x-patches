# Morphe patch release / GitHub CLI recovery — verified 2026-10-12

## Scope
Reusable checklist for Morphe patch bundles (X/Pupil/etc.). Distinguish **bundle build success**, **source import success**, and **patched app UI behavior**. Do not mark the last as verified until observed on device.

## Before publishing
1. Identify the exact target APK/package/version and keep only intended patch names.
2. Run relevant patch-unit/regression tests and actual APK patch tests when available. A mock ID test alone does not confirm the displayed UI is gone.
3. Run `python scripts/test-morphe-metadata.py` (when this repository has it); check the same schema in other repositories.
4. Check `patches-bundle.json`: `created_at` must be a **timezone-free** `LocalDateTime` string (for example `2026-10-12T02:50:02`). Do not append `Z`, `+09:00`, or any offset. Morphe's `MorpheAsset.createdAt` is `kotlinx.datetime.LocalDateTime`; an offset-bearing value caused source metadata to fail.
5. Match manifest `version`, Gradle version, patch listing and GitHub Release tag; ensure `download_url` is an *existing* `.mpp` release asset with precisely that name.
6. Build through CI and inspect the downloaded artifact itself, not just a status badge; hash-check release bytes against the CI output.

## Publishing with GitHub
- Prefer a legitimately authorized GitHub write integration. A 403 `Resource not accessible by integration` on ChatGPT GitHub connector can reflect app/integration write permissions despite the account itself owning the repository. Do not repeat identical denied operations.
- The known-working escape hatch (2026-10-12) was **normal Windows PowerShell under the ordinary signed-in Windows user**, invoking the installed `C:\Program Files\GitHub CLI\gh.exe`, authenticated as `rikunode` through Windows keyring. `repo` and `workflow` scopes were reported. Commit: https://github.com/rikunode/tairiku-x-patches/commit/73391dad35fbcb0d6476c3874baa1dd19e1dea24.
- **DevSpace MCP's portable sandbox is NOT the same identity environment**: `USERPROFILE`/`HOME`/`APPDATA` map to scratch isolation and direct `gh auth status` reported not logged in. It did not mean Windows GH CLI had logged out. Do not copy/expose tokens or defeat sandbox isolation by overriding private directories. Either use an explicitly authorized host-side publish bridge (if independently verified) or have the user run a small, reviewable PowerShell command in their normal Windows session.
- To update a single GitHub text file through the normal-session CLI: use `gh api repos/OWNER/REPO/contents/PATH` to get the current blob SHA and Base64 content; modify only the required JSON fields; PUT new Base64 with that `sha`, branch, and commit message. Fail on nonzero exit and **read back** the public raw file. Do not hard-code or print auth tokens. Never force push.
- For a one-off source fix where `.mpp` remains unchanged, edit only the `main` branch's `patches-bundle.json`; no new Release is necessary.

## Post-release gate (non-negotiable)
1. Independently retrieve `https://raw.githubusercontent.com/OWNER/REPO/main/patches-bundle.json` and parse the timestamp and version.
2. Verify Release tag and matching `.mpp` asset exist, with the expected checksum where available.
3. Import/update that remote source in the Morphe Manager. Verify it shows a meaningful title, target version, and intended patch count (for the verified X release: **two**). If stale, allow cache time and refresh; do not mistake the stale release-attached JSON for the `main` raw file.
4. Test the actual patched APK and the specific target UI separately; only user/device evidence closes that stage.
5. **Do not announce done** for a source/patched-app task before its corresponding verified gate passes.

## Incident evidence (2026-10-12)
- CI build & tests succeeded: https://github.com/rikunode/tairiku-x-patches/actions/runs/38197098250.
- Source repair commit (one JSON field changed): https://github.com/rikunode/tairiku-x-patches/commit/73391dad35fbcb0d6476c3874baa1dd19e1dea24.
- Public raw manifest read-back confirmed `2026-10-12T02:50:02` and release `1.12.0-tairiku.2`.
- User explicitly confirmed the Morphe source was updated successfully after the repair; this **does not** prove Hide Find more's UI behavior is fixed.
