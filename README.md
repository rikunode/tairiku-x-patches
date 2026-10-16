# 🧩 Tairiku X Patches

> Three Morphe patches for X: **Fix external post links**, **Hide Find more** and **Hide Who to follow**. They can be selected alongside [Ahmed Yarub's Patches](https://github.com/ahmedyarub/morphe-patches).

## Add to Morphe

[**Add Tairiku X Patches to Morphe (one tap)**](https://morphe.software/add-source?github=rikunode/tairiku-x-patches)

Or paste this public repository into Morphe Manager's remote patch-source field:

https://github.com/rikunode/tairiku-x-patches

The public source provides [patches-bundle.json](patches-bundle.json) and [patches-list.json](patches-list.json) at the repository root, with a downloadable `.mpp` in the [stable v1.12.0-tairiku.3 release](https://github.com/rikunode/tairiku-x-patches/releases/tag/v1.12.0-tairiku.3).

## Stable external-post-link fix (v1.12.0-tairiku.3)

The stable source now includes the **Fix external post links** patch, which routes
recognized external post URLs directly into X's post-detail navigation rather
than Home/For You. On 2026-10-12, the user confirmed on an actual Android phone
that an external post opens directly with For You hidden. It retains the two
recommendation filters. Tested for X 12.31.0-prod.01 and 12.32.0-prod.01.

Use the **same existing stable source URL above**; refresh it to see all three
patches. You do not need to add another source.

**Retire the duplicate beta source** in Morphe after refreshing the stable source;
otherwise overlapping patch names can conflict. Keep Ahmed Yarub's separate source
for its features. Select the stable **Fix external post links** patch with any of
the other Tairiku patches, rebuild and install. Existing APKs do not update simply
because the remote source changes. The earlier `v1.12.0-tairiku.2` stable Release and
`v1.12.0-tairiku.3-rc.1` pre-release remain archived for recovery.

## ❓ About

Patches built with [Morphe Patcher](https://github.com/MorpheApp/morphe-patcher) for apps I
use. Each patch is developed against a specific, decompiled APK version, and only versions
that have actually been verified are declared as compatible.

### How to use these patches

Add this repository to Morphe, keep Ahmed Yarub's Patches as your main X source, and select whichever of the three Tairiku patches you want from this source.

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.12.0-tairiku.3](https://github.com/rikunode/tairiku-x-patches/releases/tag/v1.12.0-tairiku.3)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;3 patches total
<details open>
<summary>📦 X&nbsp;&nbsp;•&nbsp;&nbsp;3 patches</summary>
<br>

**🎯 Supported versions:**

| 12.31.0-prod.01 | 12.32.0-prod.01 |
| :---: | :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Fix external post links](#fix-external-post-links) | Opens external X post URLs directly, preserving a hidden For You tab. |  |
| [Hide Find more](#hide-find-more) | Hides Discover more / もっと見つける under posts and top people recommendations in Search. |  |
| [Hide Who to follow](#hide-who-to-follow) | Hides Who to follow / recommended-account modules from timelines. |  |

</details>

<!-- PATCHES_END -->

&nbsp;

## 🚀 Building

The Morphe patches Gradle plugin is published to GitHub Packages, so a GitHub token with
`read:packages` is required to build:

```sh
GITHUB_ACTOR=<username> GITHUB_TOKEN=<token> ./gradlew build
```

Or put `gpr.user` and `gpr.key` in `~/.gradle/gradle.properties`.

## 🧪 Testing

`./gradlew :patches:test` checks the bundle itself and runs on every pull request.

The patches only mean something against the app builds they target, so the main tests apply
them to real APKs, which are not in the repository:

```sh
./gradlew :patches:apkTest -Pmorphe.apks=x=<X base.apk or .apkm>
```

This applies every patch for each app and fails when a patch throws, when a fingerprint
matches anything but exactly one method, or when patched code refers to a method or field
that does not exist. Add `-Pmorphe.isolated=true` to also apply each patch on its own, which
catches a patch that only works because another one was selected with it.

The patched APKs are left in `patches/build/patched`. `scripts/verify-dex.sh` runs ART's
verifier over one on a rooted emulator, which catches register and type mistakes that only
fail when the app loads the class.

## 📜 License

GNU General Public License v3.0. See [LICENSE](LICENSE).
