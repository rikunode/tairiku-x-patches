package app.tairiku.patches.x

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly

private object HideSportsScoresExtensionFingerprint : Fingerprint(
    definingClass = FILTER_CLASS,
    name = "hideSportsScores",
)

@Suppress("unused")
val hideSportsScoresPatch = bytecodePatch(
    name = "Hide sports scores",
    description = "Experimental: hides live sports score/scoreboard modules in the Home timeline.",
) {
    compatibleWith(COMPATIBILITY_X)
    dependsOn(recommendationFilterPatch)

    execute {
        HideSportsScoresExtensionFingerprint.method.returnEarly(true)
    }
}
