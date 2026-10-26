# Lesson 01 - Lambda Expressions

> **Level:** Beginner  |  **Time:** 20 minutes  |  **Run it:** `.\run-java8.ps1 1` (Windows) or `./run-java8.sh 1` (Mac/Linux)

## The big idea

A **lambda expression** is a short way to write a small piece of code (a function) **without giving it a name**.

You can then *pass this code around* like a value: put it in a variable, give it to a method, return it from a method.

```java
// Before Java 8
Runnable a = new Runnable() {
    public void run() { System.out.println("Hi"); }
};

// Java 8
Runnable b = () -> System.out.println("Hi");
```

Same result, much less noise.

## Syntax in one picture

```
 (parameters)   ->   body
  ^                   ^
  what goes in        what to do / what to return
```

| You write | Meaning |
|---|---|
| `() -> 5` | no input, returns 5 |
| `x -> x * 2` | one input (brackets optional), returns double |
| `(a, b) -> a + b` | two inputs |
| `(int a, int b) -> a + b` | types written (optional) |
| `x -> { int y = x * 2; return y; }` | many lines: use `{ }` and `return` |
| `() -> { }` | no input, does nothing (empty block) |
| `() -> 42` or `() -> null` | no input, returns a constant |
| `() -> { return 42; }` | same as `() -> 42`, written as a block |
| `(String s) -> s.length()` | parameter type written |
| `(Thread t) -> { t.start(); }` | block with a statement, no result |

## Where can I use a lambda?

Only where Java expects a **functional interface** - an interface with **exactly one abstract method**.
`Runnable`, `Comparator`, `Callable` are all examples. (Lesson 02 shows all the important ones.)

You can mark your own with `@FunctionalInterface`. It is optional, but then the compiler will warn you if you add a second abstract method by mistake.

## Rules to remember

1. A lambda can **read** outside local variables, but they must be **effectively final** (never re-assigned).
2. Inside a lambda, `this` means the **surrounding class** (not the lambda). In an anonymous class, `this` is the anonymous object.
3. A lambda is **not** a new kind of object type - it is just a neat way to create an object of a functional interface.

## What the example file shows

`Lesson01_Lambda.java` has 6 small steps:
1. Old way (anonymous class) vs new way (lambda)
2. All syntax forms
3. Sorting lists with a lambda
4. Using outside variables (and the effectively-final rule)
5. Making your own functional interface
6. How `this` behaves

## Try it yourself

1. Write a lambda that takes a `String` and returns its length.
2. Sort a list of numbers from biggest to smallest using `list.sort(...)`.
3. Un-comment the line `prefix = "Dr. ";` in step 4 and read the compiler error. Errors are teachers!

## Official sources

- Oracle Tutorial - Lambda Expressions: <https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html>
- Language spec (15.27 Lambda Expressions): <https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.27>

**Next:** [Lesson 02 - Functional Interfaces](../02-functional-interfaces/README.md)
