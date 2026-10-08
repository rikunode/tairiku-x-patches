# 🧩 Tairiku X Patches

> Two standalone Morphe patches for X: **Hide Who to follow** and **Hide Find more**. They are intentionally packaged as a tiny add-on source so they can be selected alongside [Ahmed Yarub's Patches](https://github.com/ahmedyarub/morphe-patches).

## Add to Morphe

This repository is public. In Morphe's remote source field, add the repository URL:

https://github.com/rikunode/tairiku-x-patches

## ❓ About

Patches built with [Morphe Patcher](https://github.com/MorpheApp/morphe-patcher) for apps I
use. Each patch is developed against a specific, decompiled APK version, and only versions
that have actually been verified are declared as compatible.

### How to use these patches

Add this repository to Morphe, keep Ahmed Yarub's Patches as your main X source, and select only the two Tairiku patches you want from this source.

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.12.0-tairiku.1](https://github.com/rikunode/tairiku-x-patches/releases/tag/v1.12.0-tairiku.1)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;2 patches total
<details open>
<summary>📦 X&nbsp;&nbsp;•&nbsp;&nbsp;2 patches</summary>
<br>

**🎯 Supported versions:**

| 12.31.0-prod.01 | 12.32.0-prod.01 |
| :---: | :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Hide Find more](#hide-find-more) | Hides the Find more / top people recommendation module from Search. |  |
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
