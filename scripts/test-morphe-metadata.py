#!/usr/bin/env python3
"""Validate Morphe remote-source manifests before publishing.

Morphe's created_at uses kotlinx.datetime.LocalDateTime: never include a timezone suffix.
Stable and opt-in beta manifests may target different released bundle versions.
"""
import datetime
import json
import pathlib
import re
from urllib.parse import urlparse

root = pathlib.Path(__file__).resolve().parents[1]
patches = json.loads((root / "patches-list.json").read_text(encoding="utf-8"))
gradle = (root / "gradle.properties").read_text(encoding="utf-8")
match = re.search(r"(?m)^version\s*=\s*(\S+)\s*$", gradle)
assert match is not None, "gradle.properties must declare a version"
build_version = match.group(1)
assert patches["version"] == build_version, "Patches list and Gradle versions differ"

beta = root / "patches-bundle-beta.json"
manifest_paths = [root / "patches-bundle.json"] + ([beta] if beta.is_file() else [])
for filename in manifest_paths:
    manifest = json.loads(filename.read_text(encoding="utf-8"))
    version = manifest["version"]
    created = manifest["created_at"]
    assert re.fullmatch(r"\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d{1,9})?", created), (
        f"{filename.name}: created_at must be timezone-free LocalDateTime, got {created!r}"
    )
    datetime.datetime.strptime(created.split(".")[0], "%Y-%m-%dT%H:%M:%S")
    parsed = urlparse(manifest["download_url"])
    assert parsed.scheme == "https" and parsed.netloc == "github.com", filename
    assert parsed.path == (
        f"/rikunode/tairiku-x-patches/releases/download/v{version}/patches-{version}.mpp"
    ), f"{filename.name}: download URL does not match version"
    assert isinstance(manifest.get("description"), str), filename
    assert manifest.get("signature_download_url") in (None, ""), filename
    print(f"{filename.name}: {version} metadata PASS")

stable = json.loads((root / "patches-bundle.json").read_text(encoding="utf-8"))
candidate = stable if stable["version"] == build_version else (
    json.loads(beta.read_text(encoding="utf-8")) if beta.is_file() else None
)
assert candidate is not None and candidate["version"] == build_version, (
    "Candidate stable/beta manifest, patch list and Gradle versions must agree"
)
if candidate is not stable:
    assert "-beta." in build_version, "A nonstable manifest must use a distinct beta version"
    assert stable["version"] != build_version, "Do not overwrite an existing stable version with a beta"
    assert len(patches["patches"]) == 5, "Experimental beta must contain all five X patches"
    assert {p["name"] for p in patches["patches"]} == {
        "Hide sports scores", "Hide Who to follow", "Hide Find more", "Fix external post links", "Hide search relevance prompt"
    }, "Beta patch list must preserve all previous patches"
print(f"Candidate bundle {build_version}: PASS")
