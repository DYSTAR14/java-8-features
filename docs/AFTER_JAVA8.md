# Java 8 Features After Java 8: What Changed in Java 9 to 21

You learned Java 8. Java did not stop there. This page answers one question:

> **"For each Java 8 feature, what changed or got better in Java 9, 10, 11 ... 21?"**

## How this page was checked (so you can trust it)

| Method | What it proves |
|---|---|
| `tools/check-api-since.sh` compiles **104 small code snippets** with `javac --release N` on a real JDK 21 | The **exact version** where each API or language feature first compiles (must fail on N-1, pass on N and on 21) |
| Official JEP pages and "JDK N" release pages on **openjdk.org** | JEP numbers, titles, and release numbers |
| Official **Java SE 21 Javadoc** (docs.oracle.com) | `@since` values and API notes (for example the `Stream.count()` note) |
| OpenJDK bug tracker | Behaviour fixes (for example JDK-8075939 for `flatMap`) |
| Running every lesson of this course on JDK 21 | The Java 8 code in this repo still compiles and runs |

Result of the snippet check: **104 of 104 agree** (one item, the JAXB module, needed a wording fix: see "Removed things").

> **Limits:** I could run only JDK 21 here, not a real Java 8 runtime. Statements about *old Java 8 runtime behaviour* come from the Javadoc or the bug tracker, and I say so where it matters.

---

## 1. Short answer

1. **Nothing you learned in the Java 8 lessons became wrong.** The 20 original lessons (now in `lessons-java8/`, compiled with `--release 8`) also compile and run on Java 21 without changes. The main course in `lessons/` is now the Java 21 edition.
2. Java 9-21 mostly **adds convenience** around Java 8 features: shorter code, fewer traps.
3. A few **behaviours changed** (`peek` + `count`, `flatMap` laziness, default charset). See section 5.
4. A few things were **removed** (Nashorn, Java EE modules). See section 6.
5. New **language features** (`var`, records, switch expressions, text blocks, pattern matching, sealed) change how you write the code *around* lambdas and streams. See section 7.

## 2. Which versions matter (LTS)

| Version | LTS? | In one line |
|---|---|---|
| 8 | yes | Lambdas, streams, `Optional`, `java.time`, `CompletableFuture` |
| 9 | no | Modules, JShell, many small API additions |
| 10 | no | `var` |
| **11** | **yes** | HTTP client, `var` in lambdas, string helpers, Java EE removed |
| 12-16 | no | `teeing`, switch expressions, text blocks, records |
| **17** | **yes** | Sealed classes, strong encapsulation |
| 18-20 | no | UTF-8 default, `ExecutorService` closeable |
| **21** | **yes** | Virtual threads, sequenced collections, pattern matching for `switch` |

Oracle's support roadmap lists Java SE 8, 11, 17, 21 and 25 as LTS releases (Java 25 reached GA on 16 September 2025 according to the openjdk.org JDK 25 page). Support dates change: check <https://www.oracle.com/java/technologies/java-se-support-roadmap.html> before you plan an upgrade.

## 3. Feature by feature

### 3.1 Lambdas, functional interfaces, method references

| What | Change | Since | How verified |
|---|---|---|---|
| Lambda syntax and semantics | **Unchanged** | | whole course runs on 21 |
| `var` in lambda parameters: `(var a, var b) -> a + b` | new (lets you add annotations) | 11 (JEP 323) | compile test + JDK 11 JEP list |
| Negate a method reference: `Predicate.not(String::isBlank)` | new | 11 | compile test |
| Diamond with anonymous classes: `new Comparator<>() {...}` | new | 9 | compile test |
| `private` methods inside interfaces | new (share code between default methods) | 9 | compile test |
| Effectively-final variable in try-with-resources: `try (reader)` | new | 9 | compile test |

### 3.2 Optional

| Java 8 | Better in newer Java | Since |
|---|---|---|
| `if (o.isPresent()) a else b` | `o.ifPresentOrElse(a, b)` | 9 |
| `o.isPresent() ? o : other` | `o.or(() -> other)` | 9 |
| `.filter(Optional::isPresent).map(Optional::get)` | `.flatMap(Optional::stream)` | 9 |
| `!o.isPresent()` | `o.isEmpty()` | 11 |
| `o.get()` | `o.orElseThrow()` (the Java 21 Javadoc says: "The preferred alternative to this method is `orElseThrow()`") | 10 |

(`OptionalInt`, `OptionalLong`, `OptionalDouble` also got `orElseThrow()`, 10.) The Javadoc confirms `ifPresentOrElse`, `or`, `stream` = 9, `isEmpty` = 11, `orElseThrow()` = 10.

### 3.3 Streams

| Feature | Change | Since |
|---|---|---|
| `takeWhile`, `dropWhile` | new: stop or skip based on a test | 9 |
| `Stream.iterate(seed, hasNext, next)` | new: a for-loop as a stream | 9 |
| `Stream.ofNullable(x)` | new | 9 |
| `Stream.toList()` | new; result is **unmodifiable** (differs from `Collectors.toList()`) | 16 |
| `Stream.mapMulti(...)` | new: alternative to `flatMap` | 16 |
| `parallel()`, `sequential()`, `sorted`, `distinct`... | **Unchanged** API | |
| `Stream.Gatherers` (custom intermediate operations) | **Not in 21.** Not covered here (I did not verify it) | |

### 3.4 Collectors

| Collector | Since |
|---|---|
| `Collectors.filtering(...)`, `Collectors.flatMapping(...)` | 9 |
| `Collectors.toUnmodifiableList/Set/Map` (reject `null`) | 10 |
| `Collectors.teeing(c1, c2, merger)` | 12 |
| `groupingBy`, `partitioningBy`, `toMap`, `joining`... | **Unchanged** |

### 3.5 Collections

| Feature | Since |
|---|---|
| `List.of`, `Set.of`, `Map.of`, `Map.entry`, `Map.ofEntries` (immutable, **no nulls**) | 9 |
| `List.copyOf`, `Set.copyOf`, `Map.copyOf` | 10 |
| `collection.toArray(String[]::new)` | 11 |
| `Map.Entry.copyOf` | 17 |
| `SequencedCollection`, `SequencedSet`, `SequencedMap`: `getFirst`, `getLast`, `reversed`, `addFirst`... (JEP 431) | 21 |

Java 8's `Map.merge`, `computeIfAbsent`, `removeIf` etc. are **unchanged**.

### 3.6 Strings, text and files (used in Lessons 17 and 19)

| Feature | Since |
|---|---|
| `isBlank`, `strip`, `lines`, `repeat`, `Character.toString(int)` | 11 |
| `Files.readString`, `Files.writeString`, `Path.of` | 11 |
| `InputStream.transferTo`, `readAllBytes` | 9 |
| `indent`, `transform`, `Files.mismatch` | 12 |
| `formatted`, `stripIndent`, `translateEscapes`, text blocks | 15 |
| `splitWithDelimiters`, `StringBuilder.repeat`, `Math.clamp` | 21 |
| `Files.lines`, `Files.walk`, `BufferedReader.lines` (Java 8) | **Unchanged** |

### 3.7 Date and Time API (Lesson 15)

The core (`LocalDate`, `Duration`, `ZonedDateTime`, `DateTimeFormatter`) is **unchanged**. Additions:

| Feature | Since |
|---|---|
| `LocalDate.datesUntil`, `LocalDate.ofInstant`, `Duration.toSeconds`, `toDaysPart`, `dividedBy(Duration)`, `Clock.tickMillis` | 9 |
| `InstantSource` (an injectable "clock" for tests) | 17 |
| `DateTimeFormatter.ofLocalizedPattern("yMMMd")` | 19 |
| **CLDR locale data is the default** (JEP 252): text produced for some locales can look different from Java 8 | 9 |

### 3.8 CompletableFuture and concurrency (Lesson 16)

| Feature | Since |
|---|---|
| `orTimeout`, `completeOnTimeout`, `delayedExecutor`, `failedFuture`, `completedStage`, `copy` | 9 |
| `exceptionallyAsync`, `exceptionallyCompose` | 12 |
| `Future.resultNow()`, `Future.state()` | 19 |
| `ExecutorService` is `AutoCloseable` (use `try (var pool = ...)`) | 19 |
| `Thread.sleep(Duration)` | 19 |
| **Virtual threads**: `Thread.ofVirtual()`, `Executors.newVirtualThreadPerTaskExecutor()` (JEP 444) | 21 |
| Flow API (reactive streams interfaces), `ProcessHandle`, `StackWalker`, `Thread.onSpinWait` | 9 |
| `java.net.http.HttpClient` (works with `CompletableFuture`) (JEP 321) | 11 |
| Structured Concurrency (JEP 453) | **preview** in 21 |

**Official guidance (JEP 444):** virtual threads help when you have *many (thousands) of tasks that mostly wait*. They give *throughput, not speed*. They should *never be pooled*. The Stream API "remains the preferred way to process large data sets in parallel". So Lesson 13 (parallel streams for CPU work) and virtual threads (for waiting work) do **not** replace each other.

### 3.9 Arrays, Objects, Math, Random (Lesson 17)

| Feature | Since |
|---|---|
| `Objects.requireNonNullElse`, `Objects.checkIndex`, `Arrays.mismatch`, range `Arrays.equals` | 9 |
| `Math.absExact` | 15 |
| `HexFormat`, `RandomGenerator` (JEP 356) | 17 |
| `Math.ceilDiv` | 18 |

## 4. Java 8 way -> modern way (cheat table)

| You write in Java 8 | In Java 21 you can write |
|---|---|
| `Collections.unmodifiableList(Arrays.asList(1, 2))` | `List.of(1, 2)` |
| `list.get(list.size() - 1)` | `list.getLast()` |
| `stream.collect(Collectors.toList())` (when read-only is fine) | `stream.toList()` |
| `.filter(Optional::isPresent).map(Optional::get)` | `.flatMap(Optional::stream)` |
| `!str.trim().isEmpty()` | `!str.isBlank()` |
| `filter(s -> !s.isEmpty())` | `filter(Predicate.not(String::isEmpty))` |
| `new String(Files.readAllBytes(p), UTF_8)` | `Files.readString(p)` |
| `Map<String, List<Integer>> m = new HashMap<>();` | `var m = new HashMap<String, List<Integer>>();` |
| class with fields, getters, `equals`, `hashCode`, `toString` | `record Emp(String name, double salary) {}` |
| `if (o instanceof A) { A a = (A) o; ... }` | `if (o instanceof A a) { ... }` |
| `"line1\n" + "line2\n"` | text block `"""` |
| `String.format("%s", x)` | `"%s".formatted(x)` |
| `switch` with `break` and a temp variable | `var r = switch (x) { case 1 -> "a"; default -> "b"; };` |
| two passes to get count and sum | `Collectors.teeing(counting(), summingInt(f), merger)` |
| `pool.shutdown(); pool.awaitTermination(...)` | `try (var pool = Executors.newFixedThreadPool(4)) { ... }` |
| thread pool for 10,000 waiting tasks | `Executors.newVirtualThreadPerTaskExecutor()` |

## 5. Behaviour changes that can surprise you

| Topic | Java 8 | Newer | Proof |
|---|---|---|---|
| `list.stream().peek(f).count()` | `peek` runs for every item | From 9, `count()` **may skip the pipeline** if it knows the size, so `peek` may never run | Javadoc note on `Stream.count()` and `peek()`; run on JDK 21: 0 calls (3 when a `filter` is present) |
| `flatMap` + `findFirst`/`limit` on an infinite inner stream | did not stop early (could hang) | stops correctly | OpenJDK bug JDK-8075939, fixed in **10**, back-ported to 11 and to OpenJDK 8u222 per the bug record |
| `Stream.toList()` vs `Collectors.toList()` | only `Collectors.toList()` | `toList()` returns an **unmodifiable** list; `Collectors.toList()` returned a normal `ArrayList` | run on JDK 21 |
| `List.of`, `Set.of`, `Map.of`, `toUnmodifiable*` | not available | **no null**, immutable, `Set.of`/`Map.of` order is not defined, duplicate keys in `Map.of` throw | run on JDK 21 |
| Default charset | depends on the OS (often not UTF-8 on Windows) | **UTF-8** on all systems from 18 (JEP 400) | JEP 400; run on JDK 21: `UTF-8` |
| Locale data | JRE locale data | **CLDR** default from 9 (JEP 252) | JEP 252 title and JDK 9 page |
| NullPointerException text | no detail | tells you which expression was null | run on JDK 21 (JEP 358 added in 14) |
| Reflection into JDK internals | worked | blocked by default from 16/17 (JEP 396, JEP 403) | JDK 16 and 17 pages |

## 6. Removed or going away

| Item | Status | Source | Proof |
|---|---|---|---|
| **Nashorn** JavaScript engine (and `jjs`) | deprecated in 11 (JEP 335); **removed in 15** (JEP 372). The `javax.script` API itself stays | JEP 372 | On JDK 21, `new ScriptEngineManager().getEngineByName("nashorn")` returns `null` |
| **Java EE and CORBA modules** (`javax.xml.bind` etc.) | not resolved by default in 9, **removed in 11** (JEP 320) | JEP 320 | Compile test: compiles on 8; fails on 9, 10, 11 by default; works on 9 and 10 only with `--add-modules java.xml.bind`; fails on 11 even with it |
| Applet API | deprecated for removal in 17 (JEP 398) | JDK 17 page | |
| Security Manager | deprecated for removal in 17 (JEP 411) | JDK 17 page | |
| Finalization (`finalize()`) | deprecated for removal in 18 (JEP 421) | JDK 18 page | |
| PermGen | already replaced by Metaspace in Java 8; Java 16 has "Elastic Metaspace" (JEP 387) | JDK 16 page | |

## 7. New language features that change how you write Java 8 style code

| Feature | Version | JEP | Why it matters for lambdas and streams |
|---|---|---|---|
| `var` | 10 | 286 | Shorter declarations for long generic types. |
| `var` in lambda parameters | 11 | 323 | Annotations on lambda parameters. |
| Switch expressions | 14 | 361 | `switch` returns a value; works well in a `map` lambda. |
| Text blocks | 15 | 378 | Clear multi-line text (JSON, SQL) for tests and examples. |
| Records | 16 | 395 | The perfect small **data carrier** for stream pipelines (`Emp::dept`). |
| Pattern matching for `instanceof` | 16 | 394 | No cast after `instanceof`. |
| Sealed classes | 17 | 409 | A closed set of types; the compiler can check all cases. |
| Record patterns | 21 | 440 | Take a record apart in a `switch`. |
| Pattern matching for `switch` | 21 | 441 | `switch` on types, with guards (`when`) and `case null`. |

## 8. Upgrade checklist (Java 8 -> 17 or 21)

1. Compile and run tests on the new JDK first. Fix warnings (`-Xlint:all`).
2. Search for `javax.xml.bind`, `javax.activation`, `javax.annotation`, CORBA: add the separate libraries (JAXB, etc.) to your build. (JEP 320)
3. Search for `sun.misc`, `setAccessible(true)` on JDK classes, `jdk.internal`: fix or add `--add-opens`. (JEP 396/403)
4. Search for `nashorn` / `ScriptEngine`: move to another engine. (JEP 372)
5. Check every `FileReader`, `FileWriter`, `new String(bytes)`, `getBytes()`, `Scanner` **without a charset**. Add `StandardCharsets.UTF_8` explicitly. (JEP 400)
6. Check date/number formatting output in tests: CLDR may change text. (JEP 252)
7. Check logic inside `peek`: it may not run. (Javadoc `Stream.count`)
8. Update build tools, frameworks and agents to versions that support the new JDK.
9. Only then adopt the new features (`var`, records, `toList()`...) gradually.

## 9. What is NOT covered (be honest)

- **Java 22 and newer** (for example Java 25, an LTS released in 2025). This page stops at Java 21 as you asked. New features after 21 (such as Stream Gatherers) were not verified here.
- **Preview features in 21** (String Templates, Scoped Values, Structured Concurrency, Unnamed Patterns) are not final and are only mentioned.
- Garbage collectors, modules (JPMS), JFR, `jlink`, and security APIs are outside "Java 8 features" and are only mentioned in the JEP tables.

## 10. Official sources

| What | Link |
|---|---|
| JDK 9 JEPs | <https://openjdk.org/projects/jdk9/> |
| JDK 10 JEPs | <https://openjdk.org/projects/jdk/10/> |
| JDK 11 JEPs | <https://openjdk.org/projects/jdk/11/> |
| JDK 14 / 15 / 16 / 17 JEPs | <https://openjdk.org/projects/jdk/14/> / <https://openjdk.org/projects/jdk/15/> / <https://openjdk.org/projects/jdk/16/> / <https://openjdk.org/projects/jdk/17/> |
| JDK 18 JEPs | <https://openjdk.org/projects/jdk/18/> |
| JDK 21 JEPs | <https://openjdk.org/projects/jdk/21/> |
| JEP 320 Remove Java EE and CORBA | <https://openjdk.org/jeps/320> |
| JEP 372 Remove Nashorn | <https://openjdk.org/jeps/372> |
| JEP 395 Records | <https://openjdk.org/jeps/395> |
| JEP 400 UTF-8 by Default | <https://openjdk.org/jeps/400> |
| JEP 431 Sequenced Collections | <https://openjdk.org/jeps/431> |
| JEP 444 Virtual Threads | <https://openjdk.org/jeps/444> |
| JEP 252 CLDR by default | <https://openjdk.org/jeps/252> |
| Java SE 21 API (`@since` notes) | <https://docs.oracle.com/en/java/javase/21/docs/api/> |
| `Stream` (Java 21) | <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html> |
| `Optional` (Java 21) | <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html> |
| Bug JDK-8075939 (`flatMap` short-circuit) | <https://bugs.openjdk.org/browse/JDK-8075939> |
| Oracle Java SE Support Roadmap | <https://www.oracle.com/java/technologies/java-se-support-roadmap.html> |

## 11. Run the proof yourself

```bash
./tools/check-api-since.sh     # 104 checks on a real JDK 21 (about 2 minutes)
./run-modern.sh all            # runs the three "modern" lessons (JDK 21)
./run-java8.sh all             # the 20 ORIGINAL Java 8 lessons (compiled with --release 8)
./run.sh all                   # the 20 Java 21 lessons (compiled with --release 21)
```

Quick memory aid: [Version Cheat Sheet](VERSION_CHEATSHEET.md).

Hands-on lessons: [Modern 01 (Java 9-11)](../modern/01-java9-to-11/README.md), [Modern 02 (Java 12-17)](../modern/02-java12-to-17/README.md), [Modern 03 (Java 18-21)](../modern/03-java18-to-21/README.md).
