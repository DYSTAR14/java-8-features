# Lesson 02 - Functional Interfaces

> **Level:** Beginner  |  **Time:** 25 minutes  |  **Run it:** `.\run-java8.ps1 2` / `./run-java8.sh 2`

## The big idea

Lesson 01 said: *a lambda fits where a functional interface is expected.*

You do not need to invent these interfaces. Java 8 gives you a toolbox in the package **`java.util.function`**.
Learn the **four main families** and you understand 90% of the Stream API.

## The Big Four

| Interface | Shape | Method | Plain-English meaning | Example |
|---|---|---|---|---|
| `Predicate<T>` | `T -> boolean` | `test` | A **yes/no question** | `n -> n > 10` |
| `Function<T,R>` | `T -> R` | `apply` | **Transform** one thing into another | `s -> s.length()` |
| `Consumer<T>` | `T -> void` | `accept` | **Use** a value, return nothing | `s -> System.out.println(s)` |
| `Supplier<T>` | `() -> T` | `get` | **Produce** a value from nothing | `() -> "hi"` |

Where you will meet them in streams:

| Stream method | Takes a... |
|---|---|
| `filter(...)` | `Predicate` |
| `map(...)` | `Function` |
| `forEach(...)` | `Consumer` |
| `Stream.generate(...)`, `orElseGet(...)` | `Supplier` |
| `reduce(...)` | `BinaryOperator` |

## The family

| Interface | Meaning |
|---|---|
| `UnaryOperator<T>` | `Function<T,T>` - same type in and out |
| `BinaryOperator<T>` | `BiFunction<T,T,T>` - two same-type inputs, same-type output |
| `BiPredicate`, `BiFunction`, `BiConsumer` | Same as above but with **two** inputs |
| `IntPredicate`, `IntFunction`, `ToIntFunction`, `IntBinaryOperator`, ... | **Primitive** versions (no boxing, faster) |

## Handy combining methods

```java
Predicate<Integer> even = n -> n % 2 == 0;
Predicate<Integer> big  = n -> n > 100;

even.and(big)      // both true
even.or(big)       // one is true
even.negate()      // opposite

Function<Integer,Integer> times2 = n -> n * 2;
Function<Integer,Integer> plus1  = n -> n + 1;

times2.andThen(plus1).apply(5)   // 5*2 then +1  = 11
times2.compose(plus1).apply(5)   // 5+1 then *2  = 12
```

> **Memory trick:** `a.andThen(b)` = "a, **then** b". `a.compose(b)` = "a **after** b".

## Create your own (only when needed)

```java
@FunctionalInterface
interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
```

## Try it yourself

1. Write a `Predicate<String>` that is true when a string is empty. Then use `negate()` to get "not empty".
2. Write a `Function<String,String>` that trims, and another that upper-cases. Join them with `andThen`.
3. Which interface would you use to model "read a line from a file"? (Hint: no input, one output.)

## Official sources

- API docs for the package: <https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html>
- Tutorial - Lambda Expressions (Standard Functional Interfaces): <https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html#approach6>

**Previous:** [Lesson 01](../01-lambda-expressions/README.md) | **Next:** [Lesson 03 - Method References](../03-method-references/README.md)
