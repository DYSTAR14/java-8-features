# Lesson 13 - Parallel Streams

> **Level:** Advanced  |  **Time:** 35 minutes  |  **Run it:** `.\run.ps1 13` / `./run.sh 13`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/13-parallel-streams](../../lessons-java8/13-parallel-streams/README.md).

## The idea

A parallel stream **splits** the data, processes the pieces on **several CPU cores** at once, then **merges** the results.

```
 [1 ........ 1000]
      |  split
 [1..250] [251..500] [501..750] [751..1000]   <- 4 threads work together
      |  merge
   final result
```

Switching is one word:

```java
list.stream()           // one thread
list.parallelStream()   // many threads
stream.parallel()       // turn any stream parallel
stream.sequential()     // turn back (the LAST call wins for the whole pipeline)
```

Parallel streams run on the shared **ForkJoin common pool** (about `cores - 1` threads).

## Parallel is NOT always faster

Splitting work, starting threads and merging cost time. It only pays off when:

| Good for parallel | Bad for parallel |
|---|---|
| Many items (thousands+) | A few items |
| Heavy CPU work per item | Very quick operations |
| `ArrayList`, arrays, `IntStream.range` (split easily) | `LinkedList`, `Stream.iterate` (hard to split) |
| Independent items | Items that depend on each other |
| `sum`, `count`, `max`, `collect` | `limit`, `findFirst`, `forEachOrdered` (need order) |

> **Golden rule:** write it sequential first. Make it parallel only if you **measured** a real problem **and** measured a real improvement.

## Three big dangers

### 1. Shared mutable state
```java
int[] counter = {0};
stream.parallel().forEach(x -> counter[0]++);   // WRONG: threads overwrite each other
```
Use `count()`, `sum()`, `collect()` or `AtomicInteger`. Best of all: no shared state.

### 2. Non-associative `reduce`
`(a - b) - c` is not the same as `a - (b - c)`. Parallel reduce must use an **associative** rule: `+`, `*`, `min`, `max`, string concat.

### 3. Blocking calls
Database or network calls inside a parallel stream block the common pool and slow down **every** parallel stream in your program. Use `CompletableFuture` with your own executor instead (Lesson 16).

## Order

| Operation | Order in parallel |
|---|---|
| `forEach` | not guaranteed |
| `forEachOrdered` | kept (slower) |
| `collect(toList())` | kept |
| `findAny` | any item (fast) |
| `findFirst` | first item (slower) |

## Try it yourself

1. Run the lesson 3 times. Does the "unsafe counter" give the same wrong answer each time?
2. Change `n` to `1_000` in `speedDemo`. Which version wins now? Why?
3. Print `Thread.currentThread().getName()` inside a parallel `forEach` to see the worker threads.

## What changed after Java 8 (the code in this lesson already uses it)

The parallel stream API is **unchanged** in Java 9-21. Java 21 added virtual threads (JEP 444). Official guidance: the Stream API remains the preferred way to process large data sets in parallel; virtual threads are for many tasks that wait (I/O), and should not be pooled. See [Modern 03](../../modern/03-java18-to-21/README.md).

## Official sources

- Tutorial - Parallelism: <https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html>
- Stream package doc (Parallelism, Non-interference, Side-effects): <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html#Parallelism>

**Previous:** [Lesson 12](../12-advanced-stream-techniques/README.md) | **Next:** [Lesson 14 - Pitfalls and Best Practices](../14-pitfalls-and-best-practices/README.md)
