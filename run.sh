#!/usr/bin/env bash
# Run one lesson (or all lessons) on Mac / Linux / Git Bash. Java 21 edition: needs JDK 21 or newer.
# The original Java 8 lessons are in lessons-java8/ and run with ./run-java8.sh
#
#   ./run.sh 1       # run lesson 01
#   ./run.sh 10      # run lesson 10
#   ./run.sh all     # run every lesson (quick health check)
#
# Code is compiled with "--release 21".

set -u
ROOT="$(cd "$(dirname "$0")" && pwd)"
COMMON="$ROOT/lessons/common"

run_one() {
  local dir="$1"
  local name; name="$(basename "$dir")"
  local main; main="$(ls "$dir"/Lesson*.java 2>/dev/null | head -n 1)"
  [ -z "$main" ] && { echo "No Lesson*.java in $name"; return 1; }
  local out="$ROOT/out/$name"
  mkdir -p "$out"
  echo
  echo "=================== $name ==================="
  javac --release 21 -encoding UTF-8 -sourcepath "$COMMON" -d "$out" "$main" || return 1
  java -cp "$out" "$(basename "$main" .java)"
}

if [ "${1:-}" = "" ]; then echo "Usage: ./run.sh <lesson-number | all>"; exit 1; fi

if [ "$1" = "all" ]; then
  failed=0
  for d in "$ROOT"/lessons/[0-9][0-9]-*/; do
    run_one "${d%/}" || { echo "FAILED: $d"; failed=1; }
  done
  [ $failed -eq 0 ] && echo && echo "All lessons ran OK." || exit 1
else
  prefix="$(printf '%02d' "$1")"
  dir="$(ls -d "$ROOT"/lessons/${prefix}-*/ 2>/dev/null | head -n 1)"
  [ -z "$dir" ] && { echo "Lesson '$1' not found."; exit 1; }
  run_one "${dir%/}"
fi
