# Java 8 Features: From Beginner to Stream Pro

Learn the most important features of Java 8 step by step, with **simple English**, **small examples**, and **code you can run**.

The main goal: by the end you can read and write **Stream API** code with confidence.

```java
// What you will be able to write after this course
Map<String, Double> averageSalaryByDepartment = employees.stream()
        .filter(e -> e.age() > 25)
        .collect(Collectors.groupingBy(Employee::department,
                 Collectors.averagingDouble(Employee::salary)));
```

## Who is this for?

- You know basic Java (variables, classes, `if`, `for`, `List`, `Map`).
- You want to understand lambdas and streams, not just copy them.
- English is not your first language? Good. Every lesson uses short sentences and simple words.

## What you need

| Need | Details |
|---|---|
| Java JDK | **JDK 21** for the main course (`lessons/`) and the `modern/` lessons. **JDK 8 or newer** is enough for the original Java 8 edition (`lessons-java8/`). |
| Editor | Any (IntelliJ IDEA, VS Code, Eclipse, even Notepad) |
| Time | About 12 to 15 hours in total. Go slowly. |

Check Java: `java -version` and `javac -version`.

## How to run a lesson

**Windows (PowerShell):**
```powershell
.\run.ps1 1             # lesson 01, Java 21 edition (needs JDK 21)
.\run.ps1 11            # lesson 11
.\run.ps1 all           # every lesson
.\run-java8.ps1 11      # the ORIGINAL Java 8 version of lesson 11 (compiled with --release 8)
```

**Mac / Linux / Git Bash:**
```bash
./run.sh 1
./run.sh all
./run-java8.sh 11
```

## Two editions of the same 20 lessons

| Folder | Style | Compiled with |
|---|---|---|
| [`lessons/`](lessons/README.md) | **Java 21 edition**: records, `var`, `List.of`, `stream.toList()`, `Optional.orElseThrow()` ... | `--release 21` |
| [`lessons-java8/`](lessons-java8/README.md) | **Original Java 8 edition** | `--release 8` (the compiler rejects anything newer than Java 8, so it is real Java 8) |

Every file in `lessons/` starts with a **"JAVA 21 EDITION"** comment that lists what changed compared with the Java 8 file. Learn the Java 8 feature first (what it is), then see how it looks on Java 21.

## The learning path

```
 LEVEL 1: Beginner            LEVEL 2: Streams             LEVEL 3: Advanced           LEVEL 4: Extras
 -------------------          ------------------           ------------------          ------------------
 01 Lambdas                   06 Streams: big picture      11 groupingBy/partition     15 Date & Time API
 02 Functional interfaces     07 Intermediate operations   12 Advanced techniques      16 CompletableFuture
 03 Method references         08 Terminal operations       13 Parallel streams         17 More Java 8 features
 04 Default/static methods    09 Primitive streams         14 Pitfalls & best practice 18 48 Practice problems
 05 Optional                  10 Collectors (basics)

 LEVEL 5: Real world and interviews
 ----------------------------------
 19 Streams with files and text     20 Lambda recipes and gotchas     + docs/INTERVIEW_QUESTIONS.md

 LEVEL 6: Java 8 features after Java 8 (needs JDK 21)
 ----------------------------------------------------
 Modern 01: Java 9-11    Modern 02: Java 12-17    Modern 03: Java 18-21    + docs/AFTER_JAVA8.md
```

| # | Lesson | You will learn | Level |
|---|---|---|---|
| 01 | [Lambda Expressions](lessons/01-lambda-expressions/README.md) | Short code for small functions | Beginner |
| 02 | [Functional Interfaces](lessons/02-functional-interfaces/README.md) | `Predicate`, `Function`, `Consumer`, `Supplier` | Beginner |
| 03 | [Method References](lessons/03-method-references/README.md) | The `::` shortcut | Beginner |
| 04 | [Default & Static Methods](lessons/04-default-static-interface-methods/README.md) | Methods with a body inside interfaces | Beginner |
| 05 | [Optional](lessons/05-optional/README.md) | Say goodbye to many `NullPointerException`s | Beginner |
| 06 | [Streams: The Big Picture](lessons/06-streams-introduction/README.md) | Source, pipeline, lazy, one-use | Beginner |
| 07 | [Intermediate Operations](lessons/07-intermediate-operations/README.md) | `filter` `map` `flatMap` `sorted` `distinct` `limit` | Intermediate |
| 08 | [Terminal Operations](lessons/08-terminal-operations/README.md) | `collect` `reduce` `count` `anyMatch` `findFirst` | Intermediate |
| 09 | [Primitive Streams](lessons/09-primitive-streams/README.md) | `IntStream`, statistics, no boxing | Intermediate |
| 10 | [Collectors (basics)](lessons/10-collectors/README.md) | `toList` `toMap` `joining` `counting` | Intermediate |
| 11 | [Grouping & Partitioning](lessons/11-grouping-and-partitioning/README.md) | `groupingBy` + downstream collectors | Advanced |
| 12 | [Advanced Stream Techniques](lessons/12-advanced-stream-techniques/README.md) | `flatMap` tricks, custom Collector, word count | Advanced |
| 13 | [Parallel Streams](lessons/13-parallel-streams/README.md) | Use many CPU cores, and the dangers | Advanced |
| 14 | [Pitfalls & Best Practices](lessons/14-pitfalls-and-best-practices/README.md) | 12 common mistakes and the fixes | Advanced |
| 15 | [Date & Time API](lessons/15-date-time-api/README.md) | `LocalDate`, `Duration`, time zones | Intermediate |
| 16 | [CompletableFuture](lessons/16-completable-future/README.md) | Async code, chaining, combining | Advanced |
| 17 | [More Java 8 Features](lessons/17-more-java8-features/README.md) | New `Map` methods, `Base64`, `StringJoiner`... | Intermediate |
| 18 | [48 Practice Problems](lessons/18-stream-practice/README.md) | Real interview-style exercises | All |
| 19 | [Streams with Files and Text](lessons/19-streams-with-files-and-text/README.md) | `Files.lines`, CSV to report, word count, closing streams | Intermediate |
| 20 | [Lambda Recipes and Gotchas](lessons/20-lambda-recipes-and-gotchas/README.md) | Type-inference trap, currying, strategy pattern, exception wrapper, `unordered()` | Advanced |

### Level 6: what changed after Java 8 (Java 9 to 21)

These lessons need **JDK 21** and run with `.\run-modern.ps1 1` (or `./run-modern.sh 1`). Read the overview first: [After Java 8](docs/AFTER_JAVA8.md). It was checked against the real JDK and the official OpenJDK pages.

| # | Lesson | You will learn |
|---|---|---|
| M1 | [Java 9 to 11](modern/01-java9-to-11/README.md) | `Optional` extras, `List.of`, `takeWhile`, `Collectors.filtering`, `Predicate.not`, `var`, `Files.readString`, `CompletableFuture` timeouts |
| M2 | [Java 12 to 17](modern/02-java12-to-17/README.md) | `Collectors.teeing`, `Stream.toList()`, `mapMulti`, records, switch expressions, text blocks, sealed types |
| M3 | [Java 18 to 21](modern/03-java18-to-21/README.md) | sequenced collections, pattern matching for `switch`, virtual threads, UTF-8 default |

**Short on time?** Do lessons 01, 02, 03, 06, 07, 08, 10, 11, then 18. That is the "stream core".

## Extra help

| File | What it is |
|---|---|
| [**Master Cheat Sheet**](docs/CHEATSHEET.md) | Everything in one place: lambdas, streams, collectors, Optional, Comparator, Date/Time, CompletableFuture, 24 recipes, SQL map, traps, speed tips. 179 verified examples |
| [Interview Questions](docs/INTERVIEW_QUESTIONS.md) | 53 short Q&A: lambdas, streams, collectors, parallel, CompletableFuture |
| [Version Cheat Sheet](docs/VERSION_CHEATSHEET.md) | **Start here for memory:** one keyword per Java version, and the same task written for each version |
| [After Java 8](docs/AFTER_JAVA8.md) | What changed for each Java 8 feature in Java 9 to 21, with proof and an upgrade checklist |
| [Glossary](docs/GLOSSARY.md) | Simple meaning of every new word |
| [Official Resources](docs/RESOURCES.md) | Links to Oracle's official docs, tutorials, specs |

## How every lesson is built

Each lesson folder has:

1. **README.md** - the explanation in simple words, tables, and a "Try it yourself" section.
2. **LessonNN_Name.java** - a runnable file with many small, commented examples. Run it, read the output, change the code, run again.

Every lesson ends with *Official sources*: links to Oracle's documentation, so you can go deeper.

## A good way to study

1. **Read** the lesson README (10-15 minutes).
2. **Run** the example. Read the output next to the code.
3. **Change** something. Break it on purpose. Read the error message.
4. **Do** the "Try it yourself" tasks **without** copying.
5. After lesson 11, do the practice problems in lesson 18. Before an interview, read `docs/INTERVIEW_QUESTIONS.md` out loud.
6. Come back after one week and redo the problems you could not solve.

> Tip: Do not try to memorise. Understand the **idea** (source -> pipeline -> result). The names come with practice.

## Folder map

```
java8_features/
|-- README.md               <- you are here
|-- run.ps1 / run.sh        <- run the Java 21 lessons (compiled with --release 21)
|-- run-java8.ps1 / .sh     <- run the original Java 8 lessons (compiled with --release 8)
|-- run-modern.ps1 / .sh    <- run the Java 9-21 lessons (needs JDK 21)
|-- tools/                  <- check-api-since.sh ("added in Java N" claims) and check-cheatsheet.sh (cheat sheet examples)
|-- docs/                   <- cheat sheet, interview Q&A, after-Java-8 analysis, glossary, links
|-- modern/                 <- Level 6: Java 9-11, 12-17, 18-21
|-- lessons-java8/          <- the ORIGINAL Java 8 edition of the same 20 lessons
`-- lessons/                <- Java 21 edition (main course)
    |-- common/             <- shared sample data (Employee record, Sample)
    |-- 01-lambda-expressions/
    |-- ...
    |-- 18-stream-practice/
    |-- 19-streams-with-files-and-text/
    `-- 20-lambda-recipes-and-gotchas/
```

## Health check: prove that everything works

Run these four commands from the repo root (needs JDK 21). Each one should end without errors.

```bash
./run.sh all               # 20 Java 21 lessons (compiled with --release 21)
./run-java8.sh all         # 20 original Java 8 lessons (compiled with --release 8)
./run-modern.sh all        # 3 lessons for Java 9-21 features
./tools/check-api-since.sh # 104 "added in Java N" claims, checked against the real JDK (about 2 minutes)
./tools/check-cheatsheet.sh # 179 examples of the Master Cheat Sheet, compiled as Java 8 and as Java 21 (about 5 seconds)
```

On Windows use `.\run.ps1 all`, `.\run-java8.ps1 all` and `.\run-modern.ps1 all` (the last command needs Git Bash).

Last full verification: JDK 21.0.12 (Temurin). All 43 programs ran, 104 of 104 version claims matched, and 179 of 179 cheat sheet examples gave the printed result.

## The 10 most important ideas (read again at the end)

1. A **lambda** is a small function without a name: `(a, b) -> a + b`.
2. A **functional interface** has one abstract method; lambdas fit into it.
3. Learn `Predicate` (yes/no), `Function` (change), `Consumer` (use), `Supplier` (make).
4. `::` points to an existing method instead of writing a lambda.
5. A **stream** is a pipeline: source -> intermediate operations -> **one** terminal operation.
6. Streams are **lazy** and **single-use**, and they **never change the source**.
7. `filter` first, `map` next. `flatMap` flattens "many lists" into one stream.
8. `collect` with `groupingBy` + a downstream collector answers most "report" questions.
9. Use `Optional` for "maybe no value". Never call `get()` blindly.
10. Parallel streams are **not** automatically faster. Measure first, and avoid shared state.

Happy learning!
