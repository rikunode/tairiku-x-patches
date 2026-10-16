# X Home live sports ScoreCard — static APK evidence and release gate

## Target
The teal sports score/game pill shown immediately below the Home tab bar (example: "TB対CLE / Top 1"), **not** ordinary posts discussing sports.

## Actual X APK inspection (2026-10-16)
The locally available X 12.31.0-prod.01 and 12.32.0-prod.01 dex files were inspected read-only. Both include a GraphQL `TimelineTileFragment` with `TimelineTileContentScoreCard`, containing `score_event_summary.score_event`. Its Kotlin serialization identifier is `com.x.models.timelines.items.TileContent.ScoreCard`.

The *same* class and field chain occurs in both APK versions:
- `com.x.models.timelines.items.w1` implements the timeline-item interface and has a public `c()` entry-ID accessor; its field `a` has type `d0`.
- `com.x.models.timelines.items.d0` has field `a` of interface type `s` (tile content).
- `com.x.models.timelines.items.o` implements `s`; its `toString()` references `ScoreCard(event=`. The serializer `m` declares `com.x.models.timelines.items.TileContent.ScoreCard`.

The `Hide sports scores` extension tests precisely the `w1.a -> d0.a -> o` chain, with reflective field access. It does *not* match generic sports or score-related entry-ID prefixes. If a class or field is missing, it fails open to preserve user posts.

## Verification
- Java regression fixture mirrors the observed class hierarchy, including a real scorecard tile, non-score tile, missing fields, and text containing `ScoreCard`.
- Bundle tests require four named patches and protect the existing three (including external post links).
- CI runs Java regression, metadata validation, link navigation tests, and Gradle bundle tests.

**The APK inspection supports structural targeting but does not prove the screenshot's score bar is rendered from that exact hooked model on a logged-in device.** Do not mark actual UI removal as verified without a patched X Home screenshot.

## Public experimental beta source

The user authorized publishing a beta **before** on-device confirmation. This is a distinct prerelease (`1.12.0-tairiku.4-beta.1`) with its own `patches-bundle-beta.json` hosted on this feature branch. The stable `main/patches-bundle.json` stays at v1.12.0-tairiku.3. A successful CI build and published prerelease do not count as verification that the teal Home tile disappears.

Morphe beta source (temporary): `https://raw.githubusercontent.com/rikunode/tairiku-x-patches/feat/x-hide-sports-scores-20261016/patches-bundle-beta.json`.
Disable the overlapping stable source for the X app during beta testing, to avoid duplicate patch classes/names. Confirm the new package displays four patch names, and choose `Hide sports scores`.

## Stable release gate
Keep this PR as an experimental draft until:
1. X 12.31/12.32 APK patch application and launch succeeds.
2. An active Home sports scorebar disappears when `Hide sports scores` is selected.
3. Regular sports tweets, post details, deep links, and other timelines still work.
4. Stable `main` manifest is only updated after the above checks and a release asset actually exists.

Changing supported X versions requires rechecking the obfuscated names `w1`, `d0`, `o` against those versions' dex files.
