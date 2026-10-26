# Lesson 10 - Collectors (the basics)

> **Level:** Intermediate  |  **Time:** 40 minutes  |  **Run it:** `.\run.ps1 10` / `./run.sh 10`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/10-collectors](../../lessons-java8/10-collectors/README.md).

## What is a Collector?

`collect(...)` is the terminal operation that turns a stream into a **final result**.
You tell it *how* by giving a **Collector**. The class `Collectors` has dozens ready to use.

```java
List<String> names = staff.stream().map(Employee::name).collect(Collectors.toList());
```

## Collectors that build collections

| Collector | Result |
|---|---|
| `toList()` | `List` |
| `toSet()` | `Set` (no duplicates) |
| `toCollection(TreeSet::new)` | the exact collection you choose |
| `toMap(keyFn, valueFn)` | `Map` |
| `toMap(keyFn, valueFn, mergeFn)` | `Map`, with a rule for duplicate keys |
| `toMap(keyFn, valueFn, mergeFn, LinkedHashMap::new)` | `Map` of the type you choose |

> **Trap:** `toMap` with two items that have the same key throws `IllegalStateException: Duplicate key`.
> Fix: give a **merge function**, e.g. `(old, new) -> old`.

## Collectors that build a String

```java
Collectors.joining()                 // "AliceBobCarol"
Collectors.joining(", ")             // "Alice, Bob, Carol"
Collectors.joining(", ", "[", "]")   // "[Alice, Bob, Carol]"
```

(Items must already be `String` - use `map(...)` first.)

## Collectors that calculate a number

| Collector | Result |
|---|---|
| `counting()` | `Long` |
| `summingInt/Long/Double(fn)` | the sum |
| `averagingInt/Long/Double(fn)` | the average (a `Double`) |
| `minBy(comparator)` / `maxBy(comparator)` | `Optional<T>` |
| `summarizingInt/Long/Double(fn)` | an object with count, sum, min, average, max |
| `reducing(identity, mapper, op)` | your own rule |

## Collectors that wrap another collector

| Collector | Meaning |
|---|---|
| `mapping(fn, downstream)` | transform items, then collect them with `downstream` |
| `filtering(...)`, `flatMapping(...)` | Added in **Java 9**. Used inside `groupingBy` (Lesson 11) |
| `toUnmodifiableList()`, `toUnmodifiableSet()`, `toUnmodifiableMap(...)` | Added in **Java 10**. Read-only results; they reject `null` |
| `teeing(c1, c2, merger)` | Added in **Java 12**. Two collectors over the same stream, then merge their results |
| `collectingAndThen(downstream, fn)` | collect, then change the final result (e.g. make it read-only) |

These are most useful as the **second argument of `groupingBy`** - next lesson!

## Collectors vs. stream shortcuts

Many collectors have a direct stream version. Use the shortest:

| Collector | Shortcut |
|---|---|
| `collect(counting())` | `count()` |
| `collect(summingInt(f))` | `mapToInt(f).sum()` |
| `collect(maxBy(c))` | `max(c)` |

Collectors are mainly valuable inside `groupingBy` (Lesson 11).

## Try it yourself

1. Make a `Map<String, Integer>` of employee name -> age.
2. Produce one String: `"Alice-Bob-Carol"` (just the first three names).
3. Count how many employees earn more than 70000, using `Collectors.counting()` after a `filter`.
4. What happens if you use `toSet()` on a stream with duplicates? Try it.

## What changed after Java 8 (the code in this lesson already uses it)

Java 9: `Collectors.filtering` and `Collectors.flatMapping`. Java 10: `toUnmodifiableList/Set/Map` (they reject `null`). Java 12: `Collectors.teeing`. Java 16: `stream.toList()` (unmodifiable). `groupingBy`, `partitioningBy` and `toMap` are **unchanged**. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Collectors API: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html>
- Tutorial - Reduction (collect): <https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html#collect>

**Previous:** [Lesson 09](../09-primitive-streams/README.md) | **Next:** [Lesson 11 - Grouping and Partitioning](../11-grouping-and-partitioning/README.md)
