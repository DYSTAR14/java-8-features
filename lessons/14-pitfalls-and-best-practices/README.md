# Lesson 14 - Pitfalls and Best Practices

> **Level:** Advanced  |  **Time:** 35 minutes  |  **Run it:** `.\run.ps1 14` / `./run.sh 14`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/14-pitfalls-and-best-practices](../../lessons-java8/14-pitfalls-and-best-practices/README.md).

Experts are not people who never make mistakes. They know the **common** mistakes and avoid them. Here are the 12 most common stream mistakes.

| # | Mistake | What happens | Fix |
|---|---|---|---|
| 1 | No terminal operation | Nothing runs (lazy) | End with `collect`, `forEach`, `count`... |
| 2 | Using a stream twice | `IllegalStateException` | Make a new stream, or use a `Supplier<Stream<T>>` |
| 3 | Changing the source while streaming | `ConcurrentModificationException` | Collect into a **new** list |
| 4 | Side effects (`forEach(list::add)`) | Wrong results in parallel | Use `collect()` |
| 5 | Infinite stream without `limit` | Program never ends | Add `limit(n)` |
| 6 | `map` before `filter` | Extra useless work | `filter` first |
| 7 | Checked exceptions in a lambda | Compile error | try/catch inside, or a wrapper method |
| 8 | `null` items, `Optional.get()` | NPE / `NoSuchElementException` | `filter(Objects::nonNull)`, `orElse...` |
| 9 | `toMap` with duplicate keys | `IllegalStateException` | Add a merge function |
| 10 | `Stream<Integer>` for math | Slower (boxing) | `IntStream`, `mapToInt` |
| 11 | Logic inside `peek` | May not run | `peek` is for debugging only |
| 12 | Relying on the type of `Collectors.toList()` | Surprise later | `toCollection(ArrayList::new)` |

## Pure functions - the heart of good stream code

A lambda in `map` / `filter` should be **pure**:

- Same input -> same output.
- It does **not change** anything outside itself.

Pure lambdas are easy to read, easy to test, and **safe in parallel**.

## Readability rules

1. **One idea per line.** Put each `.filter` / `.map` on its own line.
2. **Short lambdas (1-2 lines).** Longer? Move it into a method and use a method reference.
3. **Give names.** Extract a `Predicate<Employee> isSenior = e -> e.age() > 40;` and reuse it.
4. **Not everything is a stream.** A simple `for` loop is fine (and sometimes better, e.g. when you need `break`, an index, or to change a local variable).

```java
// Hard to read
list.stream().filter(e -> e.age() > 40 && e.salary() > 70000 && !e.city().equals("Rome")).map(e -> e.name().toUpperCase() + "-" + e.department()).collect(Collectors.toList());

// Better
Predicate<Employee> isSenior = e -> e.age() > 40;
Predicate<Employee> isWellPaid = e -> e.salary() > 70000;
list.stream()
    .filter(isSenior.and(isWellPaid))
    .map(this::label)
    .collect(Collectors.toList());
```

## Debugging a pipeline

- Use `peek(x -> System.out.println("after filter: " + x))` between steps.
- In an IDE, use "Trace Current Stream Chain" (IntelliJ IDEA).
- Break the pipeline into variables: `Stream<X> step1 = ...; step1.filter(...)`.

## Try it yourself

1. Find the bug: `names.stream().map(String::trim);` then printing `names` expecting trimmed text. (Two bugs!)
2. Rewrite `forEach(result::add)` using `collect`.
3. Add your own mistake #13 to the table after you meet one in real life.

## What changed after Java 8 (the code in this lesson already uses it)

Pitfall 11 (`peek`) is real: on Java 9+ `list.stream().peek(f).count()` runs `peek` **zero** times (verified on JDK 21). `Stream.toList()` (16) is unmodifiable, unlike `Collectors.toList()`. `Optional.orElseThrow()` (10) is the preferred form of `get()`. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Stream package doc - Side-effects, Non-interference, Statelessness: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html#NonInterference>
- Tutorial - Reduction (mutable reduction and side effects): <https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html>

**Previous:** [Lesson 13](../13-parallel-streams/README.md) | **Next:** [Lesson 15 - Date and Time API](../15-date-time-api/README.md)
