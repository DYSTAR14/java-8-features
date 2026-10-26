#!/usr/bin/env bash
# Verifies "since Java N" claims against the REAL JDK (needs JDK 21 or newer).
#
# For every item we compile a tiny snippet with  javac --release X :
#   kind S / T : must FAIL on release N-1, must PASS on release N and on 21
#   kind R     : (removed) must PASS on release N-1, must FAIL on release N
#   kind P     : (preview) must FAIL on 21 with a "preview" message
#
# Run:   ./tools/check-api-since.sh
# Takes about 2-4 minutes. Prints one line per item and a summary.

set -u
SELF="$(cd "$(dirname "$0")" && pwd)/$(basename "$0")"
TMP="${CHECK_TMP:-$(mktemp -d)}"
IMPORTS='import java.util.*; import java.util.function.*; import java.util.stream.*; import java.util.concurrent.*; import java.time.*; import java.nio.file.*; import java.io.*;'

mkdir -p "$TMP/items"

# item <kind> <id> <release> <code>
item() {
  mkdir -p "$TMP/items/$2"
  printf '%s' "$1" > "$TMP/items/$2/kind"
  printf '%s' "$3" > "$TMP/items/$2/rel"
  printf '%s' "$4" > "$TMP/items/$2/code"
}

# ---------------------------------------------------------------- language
item S var_local        10 'var x = 1;'
item S var_lambda       11 'BiFunction<Integer,Integer,Integer> f = (var a, var b) -> a + b;'
item S diamond_anon      9 'Comparator<String> c = new Comparator<>() { public int compare(String a, String b) { return 0; } };'
item S try_with_var      9 'StringReader r = new StringReader("x"); try (r) { }'
item T private_iface     9 'interface @ { private void h() {} default void d() { h(); } }'
item T record           16 'record @(int a) {}'
item T sealed           17 'sealed interface @ permits @A {} final class @A implements @ {}'
item S text_block       15 $'String s = """\n  hi\n  """;'
item S switch_expr      14 'int k = switch (1) { case 1 -> 10; default -> 0; };'
item S instanceof_pat   16 'Object o = "a"; if (o instanceof String s) { s.length(); }'
item S switch_pattern   21 'Object o = 1; String r = switch (o) { case Integer i -> "i"; default -> "x"; };'
item T record_pattern   21 'record @P(int a) {} class @ { void m(Object o) { if (o instanceof @P(int a)) { } } }'

# ---------------------------------------------------------------- Optional
item S opt_ifPresentOrElse 9  'Optional.of(1).ifPresentOrElse(x -> {}, () -> {});'
item S opt_or              9  'Optional.of(1).or(() -> Optional.of(2));'
item S opt_stream          9  'Optional.of(1).stream();'
item S opt_orElseThrow0    10 'Optional.of(1).orElseThrow();'
item S optint_orElseThrow0 10 'OptionalInt.of(1).orElseThrow();'
item S opt_isEmpty         11 'Optional.of(1).isEmpty();'

# ---------------------------------------------------------------- Streams / Collectors
item S st_takeWhile     9  'Stream.of(1,2,3).takeWhile(x -> x < 3);'
item S st_dropWhile     9  'Stream.of(1,2,3).dropWhile(x -> x < 3);'
item S intst_takeWhile  9  'IntStream.range(0,5).takeWhile(x -> x < 3);'
item S st_ofNullable    9  'Stream.ofNullable("a");'
item S st_iterate3      9  'Stream.iterate(1, x -> x < 5, x -> x + 1);'
item S st_toList        16 'Stream.of(1).toList();'
item S st_mapMulti      16 'Stream.of(1).<Integer>mapMulti((x, sink) -> sink.accept(x));'
item S col_filtering    9  'Stream.of(1).collect(Collectors.filtering(x -> x > 0, Collectors.toList()));'
item S col_flatMapping  9  'Stream.of(1).collect(Collectors.flatMapping(x -> Stream.of(x), Collectors.toList()));'
item S col_toUnmodList  10 'Stream.of(1).collect(Collectors.toUnmodifiableList());'
item S col_toUnmodSet   10 'Stream.of(1).collect(Collectors.toUnmodifiableSet());'
item S col_toUnmodMap   10 'Stream.of(1).collect(Collectors.toUnmodifiableMap(x -> x, x -> x));'
item S col_teeing       12 'Stream.of(1,2).collect(Collectors.teeing(Collectors.counting(), Collectors.summingInt(x -> x), (c, s) -> c + s));'
item S pred_not         11 'Predicate<String> p = Predicate.not(String::isEmpty);'

# ---------------------------------------------------------------- Collections
item S list_of          9  'List.of(1, 2);'
item S set_of           9  'Set.of(1, 2);'
item S map_of           9  'Map.of("a", 1);'
item S map_entry        9  'Map.entry("a", 1);'
item S map_ofEntries    9  'Map.ofEntries(Map.entry("a", 1));'
item S list_copyOf      10 'List.copyOf(new ArrayList<Integer>());'
item S set_copyOf       10 'Set.copyOf(new HashSet<Integer>());'
item S map_copyOf       10 'Map.copyOf(new HashMap<String,Integer>());'
item S toArray_fn       11 'new ArrayList<String>().toArray(String[]::new);'
item S entry_copyOf     17 'Map.Entry.copyOf(Map.entry("a", 1));'
item S seq_getFirst     21 'new ArrayList<Integer>().getFirst();'
item S seq_reversed     21 'new ArrayList<Integer>().reversed();'
item S seq_addFirst     21 'new ArrayList<Integer>().addFirst(1);'
item S seq_lhm_first    21 'new LinkedHashMap<String,Integer>().firstEntry();'

# ---------------------------------------------------------------- Objects / Arrays / Math
item S obj_reqNonNullElse 9 'Objects.requireNonNullElse(null, "x");'
item S obj_checkIndex     9 'Objects.checkIndex(0, 1);'
item S arr_mismatch       9 'Arrays.mismatch(new int[]{1}, new int[]{1});'
item S arr_equals_range   9 'Arrays.equals(new int[]{1}, 0, 1, new int[]{1}, 0, 1);'
item S math_absExact      15 'Math.absExact(-1);'
item S math_ceilDiv       18 'Math.ceilDiv(7, 2);'
item S math_clamp         21 'Math.clamp(5L, 1, 3);'
item S rng_generator      17 'java.util.random.RandomGenerator.getDefault().nextInt(5);'

# ---------------------------------------------------------------- Strings / chars
item S str_isBlank        11 '"a".isBlank();'
item S str_strip          11 '"a".strip();'
item S str_lines          11 '"a".lines();'
item S str_repeat         11 '"a".repeat(2);'
item S chr_toString       11 'Character.toString(65);'
item S str_indent         12 '"a".indent(2);'
item S str_transform      12 '"a".transform(s -> s);'
item S str_formatted      15 '"%s".formatted("a");'
item S str_stripIndent    15 '"a".stripIndent();'
item S str_translateEsc   15 '"a".translateEscapes();'
item S str_splitWithDelim 21 '"a,b".splitWithDelimiters(",", 0);'
item S sb_repeat          21 'new StringBuilder().repeat("a", 2);'
item S chr_isEmoji        21 'Character.isEmoji(0x1F600);'

# ---------------------------------------------------------------- IO / Files
item S is_transferTo      9  'new ByteArrayInputStream(new byte[0]).transferTo(new ByteArrayOutputStream());'
item S is_readAllBytes    9  'new ByteArrayInputStream(new byte[0]).readAllBytes();'
item S path_of            11 'Path.of("x");'
item S files_readString   11 'Files.readString(Path.of("x"));'
item S files_writeString  11 'Files.writeString(Path.of("x"), "a");'
item S files_mismatch     12 'Files.mismatch(Path.of("a"), Path.of("b"));'
item S hexformat          17 'HexFormat.of().formatHex(new byte[]{1});'

# ---------------------------------------------------------------- Date / Time
item S ld_datesUntil      9  'LocalDate.now().datesUntil(LocalDate.now().plusDays(2));'
item S ld_ofInstant       9  'LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());'
item S dur_toSeconds      9  'Duration.ofMinutes(1).toSeconds();'
item S dur_toDaysPart     9  'Duration.ofDays(2).toDaysPart();'
item S dur_dividedByDur   9  'Duration.ofMinutes(4).dividedBy(Duration.ofMinutes(2));'
item S clock_tickMillis   9  'Clock.tickMillis(ZoneId.systemDefault());'
item S instantsource      17 'InstantSource.system().instant();'
item S dtf_localizedPat   19 'java.time.format.DateTimeFormatter.ofLocalizedPattern("yMMMd");'

# ---------------------------------------------------------------- Concurrency
item S cf_orTimeout          9  'new CompletableFuture<String>().orTimeout(1, TimeUnit.SECONDS);'
item S cf_completeOnTimeout  9  'new CompletableFuture<String>().completeOnTimeout("x", 1, TimeUnit.SECONDS);'
item S cf_delayedExecutor    9  'CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS);'
item S cf_failedFuture       9  'CompletableFuture.failedFuture(new RuntimeException());'
item S cf_completedStage     9  'CompletableFuture.completedStage(1);'
item S cf_copy               9  'CompletableFuture.completedFuture(1).copy();'
item S flow_publisher        9  'new SubmissionPublisher<String>();'
item S processhandle         9  'ProcessHandle.current().pid();'
item S stackwalker           9  'StackWalker.getInstance();'
item S onSpinWait            9  'Thread.onSpinWait();'
item S httpclient            11 'java.net.http.HttpClient.newHttpClient();'
item S cf_exceptionallyAsync 12 'CompletableFuture.completedFuture(1).exceptionallyAsync(e -> 0);'
item S cf_exceptionallyComp  12 'CompletableFuture.completedFuture(1).exceptionallyCompose(e -> CompletableFuture.completedFuture(0));'
item S future_resultNow      19 'CompletableFuture.completedFuture(1).resultNow();'
item S future_state          19 'CompletableFuture.completedFuture(1).state();'
item S exec_close            19 'Executors.newFixedThreadPool(1).close();'
item S thread_sleepDuration  19 'Thread.sleep(Duration.ofMillis(1));'
item S vthread_ofVirtual     21 'Thread.ofVirtual().start(() -> {});'
item S vthread_executor      21 'Executors.newVirtualThreadPerTaskExecutor();'

# ---------------------------------------------------------------- removed / preview
item R jaxb_hidden_default    9  'new javax.xml.bind.JAXBException("x");'
item R nashorn_removed       15 'new jdk.nashorn.api.scripting.NashornScriptEngineFactory();'
item P structured_conc       21 'new StructuredTaskScope.ShutdownOnFailure();'

# ------------------------------------------------------------------ engine
try_compile() {   # try_compile <release> <id>  -> 0 = compiles, 1 = fails
  local rel="$1" id="$2" kind code dir cls="I_$2"
  kind="$(cat "$TMP/items/$id/kind")"; code="$(cat "$TMP/items/$id/code")"
  dir="$TMP/work/$rel/$id"; mkdir -p "$dir"
  case "$kind" in
    T) printf '%s\n%s\n' "$IMPORTS" "${code//@/$cls}" > "$dir/$cls.java" ;;
    *) printf '%s\nclass %s { void m() throws Exception { %s } }\n' "$IMPORTS" "$cls" "$code" > "$dir/$cls.java" ;;
  esac
  javac --release "$rel" -Xlint:-options -proc:none -encoding UTF-8 -d "$dir/out" "$dir/$cls.java" > "$dir/log" 2>&1
}

check_one() {
  local id="$1" kind rel before pass_at pass21 verdict="OK" note=""
  kind="$(cat "$TMP/items/$id/kind")"; rel="$(cat "$TMP/items/$id/rel")"; before=$((rel - 1))
  case "$kind" in
    S|T)
      try_compile "$before" "$id" && { verdict="MISMATCH"; note="already compiles on $before"; }
      try_compile "$rel"    "$id" || { verdict="MISMATCH"; note="$note; fails on $rel"; }
      try_compile 21        "$id" || { verdict="MISMATCH"; note="$note; fails on 21"; }
      ;;
    R)
      try_compile "$before" "$id" || { verdict="MISMATCH"; note="does not compile on $before"; }
      try_compile "$rel"    "$id" && { verdict="MISMATCH"; note="$note; still compiles on $rel"; }
      ;;
    P)
      if try_compile 21 "$id"; then verdict="MISMATCH"; note="compiles on 21 (not preview)";
      elif ! grep -qi preview "$TMP/work/21/$id/log"; then verdict="MISMATCH"; note="fails on 21 but not because of preview"; fi
      ;;
  esac
  printf '%s|%s|%s|%s|%s\n' "$verdict" "$kind" "$rel" "$id" "$note"
}

if [ "${1:-}" = "--one" ]; then check_one "$2"; exit 0; fi

echo "JDK: $(javac -version 2>&1)   (checking $(ls "$TMP/items" | wc -l) items, please wait...)"
# Run items in parallel: each worker re-creates the item table from this script, then checks one id.
RESULTS="$TMP/results.txt"
ls "$TMP/items" | xargs -P 8 -I{} env CHECK_TMP="$TMP" bash "$SELF" --one {} > "$RESULTS"
sort -t'|' -k3,3n -k4,4 "$RESULTS" | awk -F'|' '{ printf "%-9s %s  Java %-2s  %-24s %s\n", $1, $2, $3, $4, $5 }'
bad=$(grep -c '^MISMATCH' "$RESULTS")
total=$(wc -l < "$RESULTS")
echo
echo "Checked $total items. Mismatches: $bad"
[ "$bad" -eq 0 ]
