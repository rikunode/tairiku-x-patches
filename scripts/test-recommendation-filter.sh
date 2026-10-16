#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
output_dir="$repo_dir/build/test-recommendation-filter"
mkdir -p "$output_dir"

"${JAVAC:-javac}" -d "$output_dir" \
    "$repo_dir/extensions/tairiku-x/src/main/java/app/tairiku/extension/x/RecommendationFilter.java" \
    "$repo_dir/tests/java/com/x/models/timelines/items/TimelineTileFixtures.java" \
    "$repo_dir/tests/java/com/x/models/timelines/items/m1.java" \
    "$repo_dir/tests/java/app/tairiku/extension/x/RecommendationFilterTest.java"
"${JAVA:-java}" -cp "$output_dir" app.tairiku.extension.x.RecommendationFilterTest
