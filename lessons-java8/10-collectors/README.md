# Lesson 10 - Collectors (the basics)

> **Level:** Intermediate  |  **Time:** 40 minutes  |  **Run it:** `.\run-java8.ps1 10` / `./run-java8.sh 10`

## What is a Collector?

`collect(...)` is the terminal operation that turns a stream into a **final result**.
You tell it *how* by giving a **Collector**. The class `Collectors` has dozens ready to use.

```java
List<String> names = staff.stream().map(Employee::getName).collect(Collectors.toList());
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
| `filtering(...)`, `flatMapping(...)` | **Not in Java 8** (added in Java 9) |
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

## In newer Java (9 to 21)

Java 9: `Collectors.filtering` and `Collectors.flatMapping`. Java 10: `toUnmodifiableList/Set/Map` (they reject `null`). Java 12: `Collectors.teeing`. Java 16: `stream.toList()` (unmodifiable). `groupingBy`, `partitioningBy` and `toMap` are **unchanged**. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Collectors API: <https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html>
- Tutorial - Reduction (collect): <https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html#collect>

**Previous:** [Lesson 09](../09-primitive-streams/README.md) | **Next:** [Lesson 11 - Grouping and Partitioning](../11-grouping-and-partitioning/README.md)
