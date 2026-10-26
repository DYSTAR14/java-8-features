# Which Java Version? One Task, Different Code (Lambdas and Streams)

Goal of this page: help you **remember** which lambda or stream code works in which Java version.
Every code line here was compiled: the Java 8 lines with `--release 8`, the newer lines with `--release 21`.

---

## 1. One keyword per version (learn only this first)

| Version | Keyword | Remember it as |
|---|---|---|
| **8** | **lambda** | `->`, `stream()`, `Optional`, `::`, `Collectors` |
| **9** | **takeWhile** | "small helpers": `List.of`, `Optional.or`, `orTimeout` |
| **10** | **var** | `var` and `toUnmodifiable...` / `copyOf` |
| **11** | **not** | `Predicate.not`, `isBlank`, `Files.readString` |
| **12** | **teeing** | two results in one pass |
| **14** | **switch =>** | `switch` that returns a value |
| **15** | **""" """** | text block |
| **16** | **toList** | `stream.toList()`, `record`, `mapMulti` |
| **17** | **sealed** | closed set of types |
| **19** | **close** | `try (var pool = Executors...)` |
| **21** | **virtual** | virtual threads, `getLast()`, `switch` on types |

Sentence to say out loud: **"Eight lambda, nine takeWhile, ten var, eleven not, twelve teeing, sixteen toList, twenty-one virtual."**

LTS versions (the ones companies use): **8, 11, 17, 21**.

---

## 2. The same task in each version

Read left to right: older code on the left, newer on the right. "-" means: no direct way, use a loop or the older code.

### Streams

| Task | Java 8 | Newer | Since |
|---|---|---|---|
| Stream to list | `s.collect(Collectors.toList())` | `s.toList()` (**unmodifiable**) | 16 |
| Stream to unmodifiable list | `collect(collectingAndThen(toList(), Collections::unmodifiableList))` | `collect(Collectors.toUnmodifiableList())` | 10 |
| Stop at the first failing item | - | `s.takeWhile(n -> n < 5)` | 9 |
| Skip items while a test is true | - | `s.dropWhile(n -> n < 5)` | 9 |
| Loop as a stream | `Stream.iterate(1, n -> n * 3).limit(5)` | `Stream.iterate(1, n -> n < 100, n -> n * 3)` | 9 |
| Stream of 0 or 1 item | `x == null ? Stream.empty() : Stream.of(x)` | `Stream.ofNullable(x)` | 9 |
| Keep non-blank text | `filter(x -> !x.trim().isEmpty())` | `filter(Predicate.not(String::isBlank))` | 11 |
| Count and sum in one pass | two streams or a custom collector | `collect(Collectors.teeing(counting(), summingInt(f), merger))` | 12 |
| One item becomes 0, 1 or many | `flatMap(x -> Stream.of(...))` | `mapMulti((x, sink) -> sink.accept(x))` | 16 |

### Collectors

| Task | Java 8 | Newer | Since |
|---|---|---|---|
| Group, and filter **inside** each group (keep empty groups) | filter first (empty groups disappear) | `groupingBy(f, filtering(p, toList()))` | 9 |
| Group, and flat-map inside each group | - | `groupingBy(f, flatMapping(g, toList()))` | 9 |
| `groupingBy`, `partitioningBy`, `toMap`, `joining`, `counting` | same | **unchanged** | |

### Optional

| Task | Java 8 | Newer | Since |
|---|---|---|---|
| Do A if present, else B | `if (o.isPresent()) {..} else {..}` | `o.ifPresentOrElse(a, b)` | 9 |
| Fall back to another Optional | `o.isPresent() ? o : other` | `o.or(() -> other)` | 9 |
| Optionals inside a stream | `.filter(Optional::isPresent).map(Optional::get)` | `.flatMap(Optional::stream)` | 9 |
| Value or exception | `o.get()` | `o.orElseThrow()` | 10 |
| Is it empty? | `!o.isPresent()` | `o.isEmpty()` | 11 |

### Collections

| Task | Java 8 | Newer | Since |
|---|---|---|---|
| Small immutable list | `Collections.unmodifiableList(Arrays.asList(1, 2, 3))` | `List.of(1, 2, 3)` | 9 |
| Immutable copy | `Collections.unmodifiableList(new ArrayList<>(x))` | `List.copyOf(x)` | 10 |
| Last element | `list.get(list.size() - 1)` | `list.getLast()` | 21 |
| Reverse view | copy + `Collections.reverse` | `list.reversed()` | 21 |

### Lambdas and language

| Task | Java 8 | Newer | Since |
|---|---|---|---|
| Long generic type | `Map<String, List<Integer>> m = new HashMap<String, List<Integer>>();` | `var m = new HashMap<String, List<Integer>>();` | 10 |
| Lambda parameter with an annotation | types only | `(var a, var b) -> a + b` | 11 |
| `instanceof` then use | `if (o instanceof String) { String t = (String) o; }` | `if (o instanceof String t)` | 16 |
| Branch by type | `if` / `else if` chain | `switch (o) { case Integer i -> ..; case String s -> ..; }` | 21 |
| `switch` that returns a value | `switch` + `break` + variable | `var r = switch (x) { case 1 -> "a"; default -> "b"; };` | 14 |
| Data class | class + getters + `equals` + `hashCode` + `toString` | `record Point(int x, int y) {}` | 16 |
| Multi-line text | `"a\n" + "b\n"` | `"""` text block `"""` | 15 |

### Files and text

| Task | Java 8 | Newer | Since |
|---|---|---|---|
| Whole file as text | `new String(Files.readAllBytes(p), UTF_8)` | `Files.readString(p)` | 11 |
| Is text blank? | `s.trim().isEmpty()` | `s.isBlank()` | 11 |
| Repeat text | loop | `"ab".repeat(3)` | 11 |

### CompletableFuture and threads

| Task | Java 8 | Newer | Since |
|---|---|---|---|
| Timeout for a future | - | `cf.orTimeout(1, SECONDS)` / `completeOnTimeout(..)` | 9 |
| Close a pool | `pool.shutdown(); pool.awaitTermination(..)` | `try (var pool = Executors.newFixedThreadPool(4)) {..}` | 19 |
| Result of a finished future | `f.get()` (checked exceptions) | `f.resultNow()` | 19 |
| Thousands of **waiting** tasks | fixed pool + `CompletableFuture` | `Executors.newVirtualThreadPerTaskExecutor()` | 21 |
| Parallel CPU work on big data | `parallelStream()` | same (**unchanged**) | |

---

## 3. "My code does not compile" quick guide

| Compiler says | Probably | Fix |
|---|---|---|
| `cannot find symbol ... takeWhile` / `ofNullable` / `ifPresentOrElse` / `List.of` | you compile with **Java 8** | use the Java 8 column, or upgrade to 9+ |
| `cannot find symbol ... toList()` on a stream, or `isBlank`, `Predicate.not` | Java **below 16** (toList) or **below 11** | `collect(Collectors.toList())`, `!s.trim().isEmpty()` |
| `cannot find symbol class var` | Java **below 10** | write the type |
| `class, interface, enum, or record expected` for `record` | Java **below 16** | write a class |
| `illegal start of expression` for `switch` with `->` | Java **below 14** | use the old `switch` |
| `getFirst()` / `getLast()` / `reversed()` not found on a List | Java **below 21** | `list.get(0)`, `list.get(list.size() - 1)` |
| `... is a preview API and is disabled by default` | the feature is **preview** in your Java | do not use it, or add `--enable-preview` (not for production) |

Check your version: `java -version` and `javac -version`.
Force a version in Maven: `<maven.compiler.release>8</maven.compiler.release>`. Gradle: `options.release = 8`.
(The Java 8 edition in `lessons-java8/` uses `--release 8` for the same reason.)

---

## 4. Four traps that depend on the version

1. **`stream.toList()` (16) vs `Collectors.toList()`**: the first returns an **unmodifiable** list. Calling `add` throws `UnsupportedOperationException`.
2. **`List.of(...)`, `Set.of(...)`, `Map.of(...)` (9)** reject `null` and are immutable. `Arrays.asList` allows `null`.
3. **`peek` + `count()` (9+)**: `peek` may never run when the size is known from the source.
4. **`var` (10)** is not dynamic typing. It also cannot be used for fields or method parameters.

---

## 5. Which version should my project use?

| Situation | Choose |
|---|---|
| Old project, cannot change | Java 8 style (`lessons-java8/`) |
| New code on Java 21 | the style in `lessons/` (Java 21 edition) |
| New project, no limits | **Java 21** (LTS) |
| Company is on 11 or 17 | write for that version: use the table's "Since" column |
| Not sure | check the `release` setting in `pom.xml` / `build.gradle` |

All other Java 8 topics in one place: [Master Cheat Sheet](CHEATSHEET.md). More detail and proof for every line: [After Java 8](AFTER_JAVA8.md). Runnable examples: [Modern 01](../modern/01-java9-to-11/README.md), [Modern 02](../modern/02-java12-to-17/README.md), [Modern 03](../modern/03-java18-to-21/README.md).
