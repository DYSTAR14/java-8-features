# Lesson 07 - Intermediate Operations

> **Level:** Beginner to Intermediate  |  **Time:** 40 minutes  |  **Run it:** `.\run.ps1 7` / `./run.sh 7`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/07-intermediate-operations](../../lessons-java8/07-intermediate-operations/README.md).

Intermediate operations take a stream and give back a **new stream**. They are **lazy** (they only run when a terminal operation is called).

## The toolbox

| Operation | What it does | Takes | Example |
|---|---|---|---|
| `filter` | Keep items that pass a test | `Predicate` | `filter(n -> n > 5)` |
| `map` | Change each item into something else | `Function` | `map(String::length)` |
| `mapToInt` / `mapToLong` / `mapToDouble` | Same as map, but gives a primitive stream | `ToIntFunction`... | `mapToInt(Employee::age)` |
| `flatMap` | Turn each item into a stream, then **merge** all streams | `Function` returning a Stream | `flatMap(List::stream)` |
| `distinct` | Remove duplicates (uses `equals`) | - | `distinct()` |
| `sorted` | Sort | optional `Comparator` | `sorted(Comparator.reverseOrder())` |
| `limit(n)` | Keep only the first n | - | `limit(5)` |
| `skip(n)` | Throw away the first n | - | `skip(5)` |
| `peek` | Look at items passing by (**debugging**) | `Consumer` | `peek(System.out::println)` |
| `boxed` | `IntStream` -> `Stream<Integer>` | - | `boxed()` |

## `map` vs `flatMap` - the most confusing pair

- `map`: **one in, one out**. `"a b"` -> `["a","b"]` (still ONE item: an array).
- `flatMap`: **one in, many out, then flattened**. `"a b"` -> `"a"`, `"b"` (TWO items).

```
 [ [1,2], [3,4], [5] ]  --flatMap(List::stream)-->  1, 2, 3, 4, 5
```

Use `flatMap` when you see `Stream<List<X>>`, `Stream<X[]>`, or `Stream<Stream<X>>` and want a plain `Stream<X>`.

## Sorting with Comparator (very useful)

```java
Comparator.comparing(Employee::name)                       // by name
Comparator.comparingDouble(Employee::salary).reversed()    // by salary, highest first
Comparator.comparing(Employee::department)
          .thenComparing(Employee::name)                   // then by name inside same department
```

## Items flow one by one (vertical, not horizontal)

Many beginners think: "first filter ALL items, then map ALL items". That is wrong.

```
 "a"   -> filter (fails, stops)
 "bb"  -> filter -> map -> forEach
 "ccc" -> filter -> map -> forEach
```

This is why `limit(2)` can stop early, and why streams can even handle infinite sources.

## Performance tip: filter early

Put `filter` **before** `map` and `sorted`. Fewer items reach the costly steps.

```java
.filter(...).map(...).sorted()    // good
.sorted().filter(...)             // sorts everything first - wasteful
```

## Try it yourself

1. Get the 3 highest-paid employee names. (`sorted`, `limit`, `map`)
2. List the unique first letters of all employee names. (`map`, `distinct`)
3. From `["a-b", "c-d-e"]` produce `[a, b, c, d, e]`. (`flatMap` and `split`)

## What changed after Java 8 (the code in this lesson already uses it)

Java 9: `takeWhile` and `dropWhile` (stop or skip while a test is true). Java 16: `mapMulti`. Fix in Java 10 (bug JDK-8075939): `flatMap` now works correctly with short-circuit operations such as `findFirst` and `limit` on an infinite inner stream. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Stream API doc: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html>
- Tutorial - Reduction / pipelines: <https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html>

**Previous:** [Lesson 06](../06-streams-introduction/README.md) | **Next:** [Lesson 08 - Terminal Operations](../08-terminal-operations/README.md)
