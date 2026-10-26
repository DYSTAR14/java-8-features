# Lesson 12 - Advanced Stream Techniques

> **Level:** Advanced  |  **Time:** 50 minutes  |  **Run it:** `.\run.ps1 12` / `./run.sh 12`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/12-advanced-stream-techniques](../../lessons-java8/12-advanced-stream-techniques/README.md).

A collection of patterns that you meet again and again in real projects and interviews.

## 1. `flatMap` replaces nested loops

```java
// all pairs (i, j) with i < j
IntStream.rangeClosed(1, 3).boxed()
    .flatMap(i -> IntStream.rangeClosed(1, 3)
                    .filter(j -> i < j)
                    .mapToObj(j -> "(" + i + "," + j + ")"))
    .collect(toList());
```

Rule: **outer loop = outer stream, inner loop = the stream inside `flatMap`**.

## 2. Sequences with `iterate` and `generate`

| Method | Meaning |
|---|---|
| `Stream.iterate(seed, f)` | seed, f(seed), f(f(seed)), ... (infinite) |
| `Stream.generate(supplier)` | values from a supplier (infinite) |
| `.limit(n)` | **must** be used to stop an infinite stream |

Fibonacci trick: keep **two numbers** in an array `{a, b}` and step to `{b, a+b}`.

> `takeWhile`, `dropWhile` and the 3-argument `iterate(seed, hasNext, next)` came in **Java 9** and are used in this lesson's code. (In the Java 8 edition you had to use `limit` or `filter` + `findFirst`.)

## 3. Three-argument `reduce`

```java
<U> U reduce(U identity, BiFunction<U,? super T,U> accumulator, BinaryOperator<U> combiner)
```

Use when the **result type differs** from the item type. The `combiner` joins partial results in parallel runs.
Most of the time `mapToInt(...).sum()` is simpler.

## 4. `Stream.concat(a, b)`

Joins two streams. For more than two, use `flatMap`.

## 5. Writing your own `Collector`

```java
Collector.of(supplier, accumulator, combiner, finisher)
```

| Part | Question it answers |
|---|---|
| supplier | "What does an empty bucket look like?" |
| accumulator | "How do I add one item to the bucket?" |
| combiner | "How do I merge two buckets?" (parallel) |
| finisher | "How do I turn the bucket into the final answer?" |

You rarely need this, but it shows what `Collectors.toList()` really is.

## 6. Word frequency (classic!)

```java
Arrays.stream(text.split(" "))
      .collect(groupingBy(Function.identity(), counting()))   // Map<String, Long>
      .entrySet().stream()
      .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
      .limit(3)
      .forEach(...);
```

## 7. Comparators

| Need | Code |
|---|---|
| By a field | `Comparator.comparing(Person::getName)` |
| By a number field (no boxing) | `Comparator.comparingInt(...)` |
| Reverse | `.reversed()` |
| Tie-break | `.thenComparing(...)` |
| Nulls last/first | `Comparator.nullsLast(...)` / `nullsFirst(...)` |

> Gotcha: `Comparator.comparing(Person::getAge).reversed()` works with method references, but with a **lambda** the type may not be inferred; write `Comparator.comparing((Person p) -> p.getAge()).reversed()`.

## 8. Other sources

Map: `map.entrySet().stream()` | Iterator: `StreamSupport.stream(Spliterators...)` | Regex: `Pattern.splitAsStream` | File: `Files.lines(path)` (close it!).

## Try it yourself

1. Print the first 10 **prime** numbers using `Stream.iterate` / `IntStream` and `filter`.
2. Given `Map<String, List<String>>` (student -> subjects), list all distinct subjects.
3. Is `"listen"` an anagram of `"silent"`? (sort the characters of both and compare)

## What changed after Java 8 (the code in this lesson already uses it)

The Java 9 methods `takeWhile`, `dropWhile` and the 3-argument `Stream.iterate` now exist (this lesson used `limit` because it targets Java 8). Java 12 `teeing` replaces many custom collectors. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Stream API: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html>
- Collector interface: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collector.html>
- Comparator: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html>

**Previous:** [Lesson 11](../11-grouping-and-partitioning/README.md) | **Next:** [Lesson 13 - Parallel Streams](../13-parallel-streams/README.md)
