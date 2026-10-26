#!/usr/bin/env bash
# Run a "modern" lesson (Java 9 to 21 features). Needs JDK 21 or newer.
#
#   ./run-modern.sh 1     # Java 9-11
#   ./run-modern.sh 2     # Java 12-17
#   ./run-modern.sh 3     # Java 18-21
#   ./run-modern.sh all

set -u
ROOT="$(cd "$(dirname "$0")" && pwd)"
COMMON="$ROOT/lessons/common"

run_one() {
  local dir="$1" name; name="$(basename "$1")"
  local main; main="$(ls "$dir"/Modern*.java 2>/dev/null | head -n 1)"
  [ -z "$main" ] && { echo "No Modern*.java in $name"; return 1; }
  local out="$ROOT/out/modern/$name"
  mkdir -p "$out"
  echo
  echo "=================== modern/$name ==================="
  javac --release 21 -encoding UTF-8 -sourcepath "$COMMON" -d "$out" "$main" || return 1
  java -cp "$out" "$(basename "$main" .java)"
}

if [ "${1:-}" = "" ]; then echo "Usage: ./run-modern.sh <1|2|3|all>"; exit 1; fi

if [ "$1" = "all" ]; then
  failed=0
  for d in "$ROOT"/modern/[0-9][0-9]-*/; do run_one "${d%/}" || { echo "FAILED: $d"; failed=1; }; done
  [ $failed -eq 0 ] && echo && echo "All modern lessons ran OK." || exit 1
else
  prefix="$(printf '%02d' "$1")"
  dir="$(ls -d "$ROOT"/modern/${prefix}-*/ 2>/dev/null | head -n 1)"
  [ -z "$dir" ] && { echo "Modern lesson '$1' not found."; exit 1; }
  run_one "${dir%/}"
fi
