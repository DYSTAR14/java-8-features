# Lesson 08 - Terminal Operations

> **Level:** Intermediate  |  **Time:** 35 minutes  |  **Run it:** `.\run-java8.ps1 8` / `./run-java8.sh 8`

A **terminal operation** is the "go" button of a stream. It runs the pipeline and returns a result (or nothing). After it, the stream is closed.

## The toolbox

| Operation | Returns | Short-circuit? | Meaning |
|---|---|---|---|
| `forEach(Consumer)` | `void` | no | Do something for each item |
| `collect(Collector)` | a collection / value | no | Gather results (Lesson 10) |
| `toArray()` | array | no | Make an array |
| `count()` | `long` | no | How many items |
| `min(Comparator)` / `max(Comparator)` | `Optional<T>` | no | Smallest / biggest |
| `reduce(...)` | value or `Optional<T>` | no | Combine all items into one |
| `findFirst()` | `Optional<T>` | **yes** | First item |
| `findAny()` | `Optional<T>` | **yes** | Any item (fast in parallel) |
| `anyMatch(Predicate)` | `boolean` | **yes** | At least one matches? |
| `allMatch(Predicate)` | `boolean` | **yes** | Everyone matches? |
| `noneMatch(Predicate)` | `boolean` | **yes** | Nobody matches? |

**Short-circuit** = it can stop early, as soon as the answer is known. `anyMatch` stops at the first `true`.

## `reduce` explained slowly

`reduce` squeezes many items into one by repeating a rule.

```
 reduce(0, (a, b) -> a + b)   over  [1, 2, 3]

   start: 0
   0 + 1 = 1
   1 + 2 = 3
   3 + 3 = 6     <- result
```

Three forms:

| Form | Use |
|---|---|
| `reduce(identity, accumulator)` | You have a safe starting value (0 for sum, 1 for product, "" for text). Returns the value. |
| `reduce(accumulator)` | No starting value. Returns `Optional` (empty if no items). |
| `reduce(identity, accumulator, combiner)` | Result type is different from item type, or for parallel streams. |

**Rule:** the rule must be *associative* - `(a op b) op c` equals `a op (b op c)`. `+`, `*`, `max` are. `-` is not (this matters in parallel streams, Lesson 13).

## Why do `min`, `max`, `findFirst` return `Optional`?

Because the stream may be empty - then there is no answer. Use `orElse`, `ifPresent`, etc. (Lesson 05).

## `count()` trap

`count()` returns `long`, not `int`. Writing `int n = stream.count();` does not compile.

## Try it yourself

1. Find the employee with the lowest salary. (`min` + `Comparator.comparingDouble`)
2. Check if **all** employees are adults (age >= 18).
3. Use `reduce` to join `["a","b","c"]` into `"abc"`.

## In newer Java (9 to 21)

Java 16: `Stream.toList()` (returns an **unmodifiable** list). Java 12: `Collectors.teeing` gets two results in one pass. The `count()` Javadoc says the pipeline may not run if the size is known from the source. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Stream API doc: <https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html>
- Tutorial - Reduction: <https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html>

**Previous:** [Lesson 07](../07-intermediate-operations/README.md) | **Next:** [Lesson 09 - Primitive Streams](../09-primitive-streams/README.md)
