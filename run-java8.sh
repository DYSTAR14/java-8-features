#!/usr/bin/env bash
# Run one ORIGINAL Java 8 lesson (or all) from lessons-java8 on Mac / Linux / Git Bash.
#
#   ./run-java8.sh 1       # run lesson 01
#   ./run-java8.sh 10      # run lesson 10
#   ./run-java8.sh all     # run every lesson (quick health check)
#
# Code is compiled with "--release 8", so using anything newer than Java 8
# gives a compile error. Needs JDK 8 or newer.

set -u
ROOT="$(cd "$(dirname "$0")" && pwd)"
COMMON="$ROOT/lessons-java8/common"

run_one() {
  local dir="$1"
  local name; name="$(basename "$dir")"
  local main; main="$(ls "$dir"/Lesson*.java 2>/dev/null | head -n 1)"
  [ -z "$main" ] && { echo "No Lesson*.java in $name"; return 1; }
  local out="$ROOT/out/java8/$name"
  mkdir -p "$out"
  echo
  echo "=================== $name ==================="
  javac --release 8 -Xlint:-options -encoding UTF-8 -sourcepath "$COMMON" -d "$out" "$main" || return 1
  java -cp "$out" "$(basename "$main" .java)"
}

if [ "${1:-}" = "" ]; then echo "Usage: ./run-java8.sh <lesson-number | all>"; exit 1; fi

if [ "$1" = "all" ]; then
  failed=0
  for d in "$ROOT"/lessons-java8/[0-9][0-9]-*/; do
    run_one "${d%/}" || { echo "FAILED: $d"; failed=1; }
  done
  [ $failed -eq 0 ] && echo && echo "All lessons ran OK." || exit 1
else
  prefix="$(printf '%02d' "$1")"
  dir="$(ls -d "$ROOT"/lessons-java8/${prefix}-*/ 2>/dev/null | head -n 1)"
  [ -z "$dir" ] && { echo "Lesson '$1' not found."; exit 1; }
  run_one "${dir%/}"
fi
