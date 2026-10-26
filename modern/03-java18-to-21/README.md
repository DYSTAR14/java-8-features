# Modern 03 - Java 18 to 21: sequenced collections, virtual threads, pattern switch

> **Needs:** JDK 21  |  **Run it:** `.\run-modern.ps1 3` / `./run-modern.sh 3`
> **Level:** Advanced  |  **Time:** 40 minutes  |  Java 21 is a **long-term-support (LTS)** release.

## Quick table

| Topic | Java 8 way | Newer way | Since |
|---|---|---|---|
| Last item of a list | `list.get(list.size() - 1)` | `list.getLast()` | 21 |
| Reverse view | copy + `Collections.reverse` | `list.reversed()` (a **view**) | 21 |
| Add / remove at the ends | `list.add(0, x)` | `addFirst`, `addLast`, `removeFirst`, `removeLast` | 21 |
| First entry of a `LinkedHashMap` | `map.entrySet().iterator().next()` | `map.firstEntry()`, `putFirst`, `reversed()` | 21 |
| Branch on the type of an object | `if (o instanceof A) ... else if (o instanceof B) ...` | `switch (o) { case A a -> ...; case B b -> ...; }` | 21 |
| Take a record apart | `r.x()`, `r.y()` | `case Point(int x, int y) ->` (record pattern) | 21 |
| Cover all cases safely | `default` that hides forgotten cases | `sealed` + `switch` with no `default` (compiler checks) | 21 |
| Thousands of waiting tasks | thread pool + `CompletableFuture` | `Executors.newVirtualThreadPerTaskExecutor()` | 21 |
| Close an executor | `shutdown()` + `awaitTermination()` | `try (var pool = ...) { }` | 19 |
| Result of a `Future` | `f.get()` (checked exceptions) | `f.resultNow()`, `f.state()` | 19 |
| Clamp a number | `Math.min(max, Math.max(min, v))` | `Math.clamp(v, min, max)` | 21 |
| Default text encoding | depends on the OS (often not UTF-8 on Windows) | **UTF-8 everywhere** | 18 |

## Sequenced collections (JEP 431, Java 21)

Java 8 had no common way to say "the first element" or "the last element" for lists, sets and maps. Java 21 adds three interfaces:

| Interface | Implemented by |
|---|---|
| `SequencedCollection` | `List`, `Deque` |
| `SequencedSet` | `LinkedHashSet`, `SortedSet` (`TreeSet`) |
| `SequencedMap` | `LinkedHashMap`, `SortedMap` (`TreeMap`) |

They add `getFirst()`, `getLast()`, `addFirst()`, `addLast()`, `removeFirst()`, `removeLast()` and `reversed()` (and for maps: `firstEntry`, `lastEntry`, `putFirst`, `putLast`, `sequencedKeySet`, ...).

> **Warning:** `getFirst()` on an **empty** list throws `NoSuchElementException`.

## Virtual threads (JEP 444, Java 21) vs parallel streams vs CompletableFuture

| Tool | Best for | Notes (from the official JEP) |
|---|---|---|
| **Parallel stream** (Lesson 13) | CPU work on a large data set | "The Stream API remains the preferred way to process large data sets in parallel." |
| **Virtual threads** | Many tasks that **wait** (database, web calls): more than a few thousand at once | Give **throughput**, not speed. **Never pool them**: create one per task. |
| **CompletableFuture** (Lesson 16) | Chaining and combining async steps | Still useful; can run on a virtual-thread executor |

Rule of thumb: *computing* -> parallel stream. *Waiting* -> virtual threads.

## Pattern matching for `switch` (JEP 441) and record patterns (JEP 440)

```java
sealed interface Shape permits Circle, Rect {}
record Circle(double radius) implements Shape {}
record Rect(double w, double h) implements Shape {}

double area(Shape s) {
    return switch (s) {
        case Circle c        -> Math.PI * c.radius() * c.radius();
        case Rect(double w, double h) -> w * h;      // no default: the compiler checks all cases
    };
}
```

Add a new `permits Triangle` and every such `switch` becomes a **compile error** until you handle it. That is the point.

## Platform changes worth knowing

| Item | Version | Source | Why it matters for Java 8 code |
|---|---|---|---|
| UTF-8 by default | 18 | JEP 400 | `FileReader`, `FileWriter`, `InputStreamReader`, `PrintStream`, `Scanner`, `Formatter` without a charset now use UTF-8, not the OS charset. Old files in another encoding may now read wrong. Opt out: `-Dfile.encoding=COMPAT`. |
| Simple Web Server (`jwebserver`) | 18 | JEP 408 | A one-line static file server for testing. |
| Finalization deprecated for removal | 18 | JEP 421 | Do not rely on `finalize()`. Use try-with-resources or `Cleaner`. |
| Generational ZGC | 21 | JEP 439 | A low-pause garbage collector option. |
| Dynamic agent loading will be disallowed in future | 21 | JEP 451 | A warning appears; may affect profilers and mocking tools. |

## Preview in Java 21 (do not use in production)

String Templates (JEP 430), Scoped Values (446), Structured Concurrency (453), Unnamed Patterns and Variables (443), Unnamed Classes and Instance Main Methods (445), and the Foreign Function & Memory API (442, third preview). They need `--enable-preview`, and they can still change.

## Try it yourself

1. Change `Square` to have a `side` of `-1`. Add a `when side < 0 -> 0` guard.
2. Add `record Triangle(...)` to `permits` and watch the compiler tell you which `switch` is incomplete.
3. Run 10,000 tasks that sleep for 100 ms with `newFixedThreadPool(50)` and with virtual threads. Compare the time.

## Official sources

- JDK 18 JEP list: <https://openjdk.org/projects/jdk/18/>
- JDK 21 JEP list: <https://openjdk.org/projects/jdk/21/>
- JEP 431 Sequenced Collections: <https://openjdk.org/jeps/431>
- JEP 444 Virtual Threads: <https://openjdk.org/jeps/444>
- JEP 400 UTF-8 by Default: <https://openjdk.org/jeps/400>

**Previous:** [Modern 02](../02-java12-to-17/README.md) | **Back to the start:** [Main README](../../README.md)
