#!/usr/bin/env bash
# Proves that the examples in docs/CHEATSHEET.md really give the results printed there.
#
#   Cheat8.java   : all Java 8 examples. Compiled TWICE: with --release 8 (so no newer API can hide in it)
#                   and with --release 21 (using the real Employee record). Both must pass.
#   Cheat21.java  : the Java 9 to 21 examples, compiled with --release 21.
#
# Needs JDK 21 or newer.   Run:  ./tools/check-cheatsheet.sh
set -u
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
HERE="$ROOT/tools/cheatsheet-check"
OUT="$(mktemp -d)"; trap 'rm -rf "$OUT"' EXIT
fail=0

echo "== Java 8 examples, --release 8 (Employee = plain class with record-style methods) =="
javac --release 8 -Xlint:-options -encoding UTF-8 -sourcepath "$HERE/java8-support:$ROOT/lessons-java8/common" -d "$OUT/a" "$HERE/Cheat8.java" && java -cp "$OUT/a" Cheat8 || fail=1

echo "== Same Java 8 examples, --release 21 (Employee = real record) =="
javac --release 21 -encoding UTF-8 -sourcepath "$ROOT/lessons/common" -d "$OUT/b" "$HERE/Cheat8.java" && java -cp "$OUT/b" Cheat8 || fail=1

echo "== Java 9 to 21 examples, --release 21 =="
javac --release 21 -encoding UTF-8 -sourcepath "$ROOT/lessons/common" -d "$OUT/c" "$HERE/Cheat21.java" && java -cp "$OUT/c" Cheat21 || fail=1

[ $fail -eq 0 ] && echo "ALL CHEAT SHEET EXAMPLES VERIFIED" || { echo "SOMETHING FAILED"; exit 1; }
