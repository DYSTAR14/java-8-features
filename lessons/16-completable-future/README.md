# Lesson 16 - CompletableFuture

> **Level:** Advanced  |  **Time:** 40 minutes  |  **Run it:** `.\run.ps1 16` / `./run.sh 16`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/16-completable-future](../../lessons-java8/16-completable-future/README.md).

## The idea

Imagine ordering food. You do **not** stand at the counter until it is ready (blocking). You get a **buzzer** (a promise), do other things, and when it buzzes you pick up the food.

`CompletableFuture<T>` is that buzzer: a **promise of a value that will arrive later**, and you can say what to do *when* it arrives.

> The old `Future` had only `get()` which **blocks**. `CompletableFuture` lets you **chain**, **combine** and **handle errors**, just like streams do for data.

## Start a task

| Method | Meaning |
|---|---|
| `supplyAsync(supplier)` | run code on another thread, **returns a value** |
| `runAsync(runnable)` | run code on another thread, no value |
| `completedFuture(value)` | already-finished future (great for tests) |

Always pass **your own executor** as a second argument for slow/blocking tasks:
`supplyAsync(() -> ..., myPool)`. Otherwise they run on the shared common pool.

## Chain steps (looks like stream methods!)

| Method | Like | Meaning |
|---|---|---|
| `thenApply(fn)` | `map` | transform the result |
| `thenAccept(consumer)` | `forEach` | use the result, return nothing |
| `thenRun(runnable)` | | run something after, ignore result |
| `thenCompose(fn)` | `flatMap` | `fn` returns another `CompletableFuture` |
| `thenCombine(other, fn)` | | wait for **two** futures and merge |

```java
supplyAsync(() -> fetchUser(id))
    .thenApply(User::getEmail)
    .thenCompose(email -> supplyAsync(() -> sendMail(email)))
    .thenAccept(r -> log(r));
```

## Many at once

| Method | Meaning |
|---|---|
| `allOf(f1, f2, ...)` | done when **all** are done (returns `Void`; read each one yourself) |
| `anyOf(f1, f2, ...)` | done when the **first** is done |

> **Classic mistake:** `ids.stream().map(id -> supplyAsync(...)).map(CompletableFuture::join)` in ONE stream runs the tasks **one by one**, because streams are lazy.
> Fix: first `collect` the futures into a list (all started), **then** join them.

## Errors

| Method | Meaning |
|---|---|
| `exceptionally(ex -> fallback)` | recover with a fallback value |
| `handle((value, ex) -> ...)` | get both outcome and error, return a new value |
| `whenComplete((value, ex) -> ...)` | look at the outcome (logging), does not change it |

`join()` throws an unchecked `CompletionException` (the real error is `getCause()`). `get()` throws checked exceptions.

## Getting the result

| Method | Behaviour |
|---|---|
| `join()` | waits, unchecked exception |
| `get()` | waits, checked exceptions |
| `getNow(default)` | does not wait; returns default if not ready |
| `complete(value)` | you set the value by hand |

## Best practices

- Avoid calling `join()` / `get()` in the middle of your code; chain with `thenApply`/`thenCompose` and block only at the very end.
- Use a **dedicated executor** for slow I/O and **shut it down**.
- Always think about the failure path (`exceptionally` / `handle`).
- Timeouts (`orTimeout`, `completeOnTimeout`) came in **Java 9** (section 6 of the example). The Java 8 edition had no built-in timeout.
- `ExecutorService` is `AutoCloseable` from **Java 19**: `try (var pool = Executors.newFixedThreadPool(4)) { ... }` closes it for you and waits for the tasks.

## Try it yourself

1. Fetch three prices in parallel and print the **cheapest**. (`allOf` + stream `min`)
2. Make one task throw an exception and recover with `exceptionally`.
3. Run the `combining` example sequentially and compare the time (should be about 600 ms).

## What changed after Java 8 (the code in this lesson already uses it)

Java 9: `orTimeout`, `completeOnTimeout`, `delayedExecutor`, `failedFuture`. Java 12: `exceptionallyAsync`, `exceptionallyCompose`. Java 19: `Future.resultNow()`, `Future.state()`, and `ExecutorService` can be closed with try-with-resources. Java 21: virtual threads. See [Modern 01](../../modern/01-java9-to-11/README.md) and [Modern 03](../../modern/03-java18-to-21/README.md).

## Official sources

- API docs: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html>
- `java.util.concurrent` package overview: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html>

**Previous:** [Lesson 15](../15-date-time-api/README.md) | **Next:** [Lesson 17 - More Java 8 Features](../17-more-java8-features/README.md)
