# Lesson 09 - Primitive Streams

> **Level:** Intermediate  |  **Time:** 25 minutes  |  **Run it:** `.\run.ps1 9` / `./run.sh 9`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/09-primitive-streams](../../lessons-java8/09-primitive-streams/README.md).

## Why do they exist?

Generics cannot hold `int`. A `Stream<Integer>` holds **Integer objects** ("boxing"). Boxing costs memory and time.

Java 8 has three special streams that hold raw numbers:

| Stream | Holds |
|---|---|
| `IntStream` | `int` |
| `LongStream` | `long` |
| `DoubleStream` | `double` |

They are **faster** and give you extra methods: `sum()`, `average()`, `min()`, `max()`, `range()`, `summaryStatistics()`.

## Moving between stream types

```
 Stream<T> --mapToInt/mapToLong/mapToDouble--> IntStream / LongStream / DoubleStream
 IntStream --boxed() or mapToObj()-----------> Stream<Integer> / Stream<R>
 IntStream --asLongStream() / asDoubleStream()
```

```java
double total = employees.stream().mapToDouble(Employee::salary).sum();   // no boxing
```

## Creating them

| Code | Result |
|---|---|
| `IntStream.of(1, 2, 3)` | 1, 2, 3 |
| `IntStream.range(0, 5)` | 0, 1, 2, 3, 4 (end **excluded**) |
| `IntStream.rangeClosed(1, 5)` | 1, 2, 3, 4, 5 (end **included**) |
| `Arrays.stream(intArray)` | the array items |
| `"abc".chars()` | character codes 97, 98, 99 |
| `new Random().ints(5, 1, 7)` | 5 random ints from 1 to 6 |

## `summaryStatistics()` - five answers, one pass

```java
IntSummaryStatistics s = staff.stream().mapToInt(Employee::age).summaryStatistics();
s.getMin();  s.getMax();  s.getAverage();  s.getSum();  s.getCount();
```

Much better than looping five times.

## Optional primitive results

`average()`, `min()` and `max()` return `OptionalDouble` / `OptionalInt`, because an empty stream has no answer.

```java
double avg = IntStream.empty().average().orElse(0);   // 0.0
```

`sum()` of an empty stream is simply `0`.

## Overflow warning

`int` overflows silently at about 2.1 billion. For big sums or factorials use `LongStream` (or `mapToLong`).

## Try it yourself

1. Print the sum of all numbers from 1 to 1000 that are divisible by 3 or 5.
2. Count the capital letters in `"Hello World From Java"`.
3. Find the average salary of the Engineering department. (Hint: `filter`, `mapToDouble`, `average`)

## Official sources

- IntStream API: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/IntStream.html>
- IntSummaryStatistics: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/IntSummaryStatistics.html>

**Previous:** [Lesson 08](../08-terminal-operations/README.md) | **Next:** [Lesson 10 - Collectors](../10-collectors/README.md)
