# Lesson 05 - Optional

> **Level:** Beginner  |  **Time:** 25 minutes  |  **Run it:** `.\run.ps1 5` / `./run.sh 5`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/05-optional](../../lessons-java8/05-optional/README.md).

## The problem

`null` is a famous source of bugs - `NullPointerException` (NPE). A method that returns `null` does not *tell* you it might return nothing.

## The idea

`Optional<T>` is a **box that holds one value or is empty**. The method signature now tells the truth:

```java
Optional<User> findUser(String name);   // "maybe a user, maybe nothing"
```

The caller is *forced to think* about the empty case.

## Cheat sheet

| Goal | Code |
|---|---|
| Make a box with a value (not null) | `Optional.of(x)` |
| Make a box, x may be null | `Optional.ofNullable(x)` |
| Make an empty box | `Optional.empty()` |
| Is there a value? | `opt.isPresent()` |
| Do something if present | `opt.ifPresent(v -> ...)` |
| Value or a fixed fallback | `opt.orElse(fallback)` |
| Value or a fallback computed **only when needed** | `opt.orElseGet(() -> compute())` |
| Value or throw my exception | `opt.orElseThrow(() -> new MyException())` |
| Transform the value | `opt.map(v -> ...)` |
| Transform with a function that returns Optional | `opt.flatMap(v -> ...)` |
| Keep only if condition true | `opt.filter(v -> ...)` |

## `map` vs `flatMap`

```java
Optional<String> a = user.map(User::getName);            // getName returns String       -> use map
Optional<Address> b = user.flatMap(User::getAddress);    // getAddress returns Optional  -> use flatMap
```

Chain them to avoid null checks:

```java
String city = findUser("asha")
        .flatMap(User::getAddress)
        .flatMap(Address::getCity)
        .orElse("City unknown");
```

## `orElse` vs `orElseGet` (important!)

```java
opt.orElse(createDefault());          // createDefault() ALWAYS runs
opt.orElseGet(() -> createDefault()); // runs only if opt is empty
```

If the default is cheap (a constant) use `orElse`. If it costs time or has side effects, use `orElseGet`.

## Good habits

- DO return `Optional` from methods that might find nothing.
- DON'T use `Optional` for fields, method parameters, or inside collections.
- DON'T call `get()` blindly. If empty it throws `NoSuchElementException`.
- DON'T wrap a collection: return an **empty list**, not `Optional<List<T>>`.

## Try it yourself

1. Write `Optional<Integer> firstEven(List<Integer> list)` using `stream().filter(...).findFirst()`.
2. Print `"No even number"` when it is empty, using `orElse`.
3. Why is `Optional.of(null)` a bug? (It throws NPE immediately - use `ofNullable`.)

## What changed after Java 8 (the code in this lesson already uses it)

Java 9: `ifPresentOrElse`, `or`, `stream`. Java 10: `orElseThrow()` with no argument (the Java 21 Javadoc calls it the preferred alternative to `get()`). Java 11: `isEmpty()`. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- API docs: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html>
- Oracle article "Tired of Null Pointer Exceptions? Consider Using Java SE 8's Optional!": <https://www.oracle.com/technical-resources/articles/java/java8-optional.html>

**Previous:** [Lesson 04](../04-default-static-interface-methods/README.md) | **Next:** [Lesson 06 - Streams Introduction](../06-streams-introduction/README.md)
