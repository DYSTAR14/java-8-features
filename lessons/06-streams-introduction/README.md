# Lesson 06 - Streams: The Big Picture

> **Level:** Beginner  |  **Time:** 30 minutes  |  **Run it:** `.\run.ps1 6` / `./run.sh 6`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/06-streams-introduction](../../lessons-java8/06-streams-introduction/README.md).

## What is a Stream?

A **Stream** is a *sequence of elements* that supports *operations* (filter, map, sort, count...).

Important: a stream is **not a collection**. It does **not store** data. It is a **pipeline** that moves data from a source through some steps.

```
 SOURCE            INTERMEDIATE OPERATIONS               TERMINAL OPERATION
 (list, array...)  (filter, map, sorted, distinct ...)   (collect, forEach, count, reduce ...)

 [1,2,3,4]  --->   filter(even)  --->  map(x*x)   --->   collect(toList)   --->  [4, 16]
```

## The 3 parts of every stream

| Part | How many | Examples | Returns |
|---|---|---|---|
| **Source** | exactly 1 | `list.stream()`, `Stream.of()`, `IntStream.range()` | a Stream |
| **Intermediate** operations | 0 or more | `filter`, `map`, `sorted`, `distinct`, `limit` | a **new Stream** (so you can chain) |
| **Terminal** operation | exactly 1 | `collect`, `forEach`, `count`, `reduce`, `anyMatch` | a **result** (or nothing) |

## 4 rules that explain almost everything

1. **Lazy:** intermediate operations do **nothing** until a terminal operation is called.
2. **One use only:** after the terminal operation, the stream is closed. Make a new one if needed.
3. **Source is safe:** a stream does not change the list/array it came from.
4. **Item by item:** each element travels through the *whole pipeline* before the next one starts (see Lesson 07).

## Why use streams instead of loops?

| Loop | Stream |
|---|---|
| Says **HOW** (step by step) | Says **WHAT** you want |
| Needs temporary variables | No temporary variables |
| Hard to run in parallel | Change `.stream()` to `.parallelStream()` (Lesson 13) |

```java
// WHAT I want: names that start with "a", in capital letters, joined by a space
String s = names.stream()
        .filter(n -> n.startsWith("a"))
        .map(String::toUpperCase)
        .collect(Collectors.joining(" "));
```

Streams are not always better. For a very simple loop, a `for` loop is fine.

## Types of streams

| Type | For | Why |
|---|---|---|
| `Stream<T>` | objects | general |
| `IntStream`, `LongStream`, `DoubleStream` | numbers | faster (no boxing), extra methods like `sum()` and `average()` |

## Ways to make a stream (all shown in the example)

`collection.stream()`, `Arrays.stream(array)`, `Stream.of(...)`, `IntStream.range(a,b)`, `IntStream.rangeClosed(a,b)`,
`Stream.iterate(seed, f)`, `Stream.generate(supplier)`, `"text".chars()`, `Files.lines(path)`, `Stream.empty()`.

## Try it yourself

1. From `[1..20]`, get the cubes of numbers divisible by 3.
2. Change the example in step 3: remove the `collect(...)`. Does `processing` print? Why not?
3. Make an infinite stream of `"ha"` and join the first 4 into `"ha-ha-ha-ha"`. (Hint: `Collectors.joining("-")`)

## What changed after Java 8 (the code in this lesson already uses it)

Java 9 added `takeWhile`, `dropWhile`, `Stream.ofNullable` and `Stream.iterate(seed, hasNext, next)`. Java 16 added `Stream.toList()`. Laziness got one more rule: from Java 9, `count()` may skip the pipeline when it knows the size, so `peek` may not run (see Lesson 14). See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Package overview (the best explanation, read it slowly): <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html>
- Oracle Tutorial - Aggregate Operations: <https://docs.oracle.com/javase/tutorial/collections/streams/index.html>

**Previous:** [Lesson 05](../05-optional/README.md) | **Next:** [Lesson 07 - Intermediate Operations](../07-intermediate-operations/README.md)
