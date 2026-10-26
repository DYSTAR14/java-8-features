# Lesson 03 - Method References

> **Level:** Beginner  |  **Time:** 15 minutes  |  **Run it:** `.\run-java8.ps1 3` / `./run-java8.sh 3`

## The big idea

If your lambda **only calls one existing method**, you can replace it with a **method reference** using `::`.
It is shorter and often easier to read.

```java
list.forEach(s -> System.out.println(s));   // lambda
list.forEach(System.out::println);          // method reference (same thing)
```

> Read `::` as **"the method called..."**. So `String::length` = "the method called `length` of `String`".
> Do **not** write `()` after the method name - you are *pointing* at the method, not calling it.

## The 4 kinds

| # | Kind | Pattern | Lambda it replaces |
|---|---|---|---|
| 1 | Static method | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| 2 | Method of a **specific** object | `System.out::println` | `s -> System.out.println(s)` |
| 3 | Method of **any** object of a class | `String::length` | `s -> s.length()` |
| 4 | Constructor | `Person::new` | `name -> new Person(name)` |

### How to tell kind 2 from kind 3?

- Left side is an **object** (like `System.out`, `myList`, `prefix`) -> kind 2.
- Left side is a **class name** and the method is not static -> kind 3. The **first lambda parameter** becomes the object.

```java
String::startsWith        // (a, b) -> a.startsWith(b)
```

### Constructors and arrays

```java
Supplier<List<String>> make = ArrayList::new;   // () -> new ArrayList<>()
IntFunction<int[]> arr = int[]::new;            // size -> new int[size]
String[] a = stream.toArray(String[]::new);     // you will use this a lot
```

## When NOT to use them

If the lambda does more than call one method (`s -> s.length() + 1`), keep the lambda.
Clarity beats cleverness.

## Try it yourself

1. Turn `names.stream().map(n -> n.toUpperCase())` into a method reference.
2. Turn `list.stream().map(x -> new StringBuilder(x))` into a method reference.
3. Which of the 4 kinds is `Math::abs`?  (Answer: static.)

## Official sources

- Oracle Tutorial - Method References: <https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html>

**Previous:** [Lesson 02](../02-functional-interfaces/README.md) | **Next:** [Lesson 04 - Default and Static Methods in Interfaces](../04-default-static-interface-methods/README.md)
