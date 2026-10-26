# lessons: the Java 21 edition

These are the 20 main lessons, written in **Java 21 style**. Java 8 features are still the topic; the code around them uses what Java 9 to 21 added (`var`, `List.of`, `Stream.toList()`, records, `Optional.orElseThrow()`, and so on).

- **Needs:** JDK 21 or newer. Run a lesson with `.\run.ps1 11` (Windows) or `./run.sh 11` (Mac/Linux).
- **The original Java 8 version** of every lesson is in [`../lessons-java8/`](../lessons-java8/README.md) (run with `run-java8`).
- **How to spot the differences:** every `Lesson*.java` file starts with a **"JAVA 21 EDITION"** comment that lists exactly what changed, and the changed lines carry a `Java 8 way:` comment where it helps.

## What changed in every lesson, in short

| Change | Java version | Where you see it |
|---|---|---|
| `Employee` is a `record`: `Employee::salary` instead of `Employee::getSalary` | 16 | `common/`, lessons 07-11, 18, 20 |
| `stream.toList()` instead of `collect(Collectors.toList())` (the result is **unmodifiable**) | 16 | almost every stream lesson. Lessons 06, 08 and 10 keep `collect(...)` once on purpose, because you must know it |
| `List.of(...)` instead of `Arrays.asList(...)` (immutable, **no nulls**) | 9 | all lessons. Lines that need `null` still use `Arrays.asList` |
| `var` for long declarations | 10 | lessons 01, 09, 16, 17, 20 |
| `Optional.orElseThrow()` instead of `get()`; `ifPresentOrElse`, `or`, `stream`, `isEmpty` | 9-11 | lessons 05, 08, 10, 11, 18 |
| New methods used in examples: `takeWhile`, `dropWhile`, `Collectors.filtering`, `teeing`, `mapMulti`, `LocalDate.datesUntil`, `Files.readString` | 9-16 | lessons 07, 10, 11, 12, 15, 19 |
| `try (var pool = Executors...)`, `orTimeout`, virtual threads | 9-21 | lessons 16, 20 |

Full explanation and proof: [After Java 8](../docs/AFTER_JAVA8.md) and the [Version Cheat Sheet](../docs/VERSION_CHEATSHEET.md).

## Lessons

| # | Lesson |
|---|---|
| 01 | [Lambda Expressions](01-lambda-expressions/README.md) |
| 02 | [Functional Interfaces](02-functional-interfaces/README.md) |
| 03 | [Method References](03-method-references/README.md) |
| 04 | [Default and Static Methods in Interfaces](04-default-static-interface-methods/README.md) |
| 05 | [Optional](05-optional/README.md) |
| 06 | [Streams: The Big Picture](06-streams-introduction/README.md) |
| 07 | [Intermediate Operations](07-intermediate-operations/README.md) |
| 08 | [Terminal Operations](08-terminal-operations/README.md) |
| 09 | [Primitive Streams](09-primitive-streams/README.md) |
| 10 | [Collectors (basics)](10-collectors/README.md) |
| 11 | [Grouping and Partitioning](11-grouping-and-partitioning/README.md) |
| 12 | [Advanced Stream Techniques](12-advanced-stream-techniques/README.md) |
| 13 | [Parallel Streams](13-parallel-streams/README.md) |
| 14 | [Pitfalls and Best Practices](14-pitfalls-and-best-practices/README.md) |
| 15 | [Date and Time API](15-date-time-api/README.md) |
| 16 | [CompletableFuture](16-completable-future/README.md) |
| 17 | [More Java 8 Features](17-more-java8-features/README.md) |
| 18 | [48 Practice Problems](18-stream-practice/README.md) |
| 19 | [Streams with Files and Text](19-streams-with-files-and-text/README.md) |
| 20 | [Lambda Recipes and Gotchas](20-lambda-recipes-and-gotchas/README.md) |
