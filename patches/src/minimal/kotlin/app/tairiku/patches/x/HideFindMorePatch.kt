package app.tairiku.patches.x

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly

private object HideFindMoreExtensionFingerprint : Fingerprint(
    definingClass = FILTER_CLASS,
    name = "hideFindMore",
)

@Suppress("unused")
val hideFindMorePatch = bytecodePatch(
    name = "Hide Find more",
    description = "Hides the Find more / top people recommendation module from Search.",
) {
    compatibleWith(COMPATIBILITY_X)
    dependsOn(recommendationFilterPatch)

    execute {
        HideFindMoreExtensionFingerprint.method.returnEarly(true)
    }
}
