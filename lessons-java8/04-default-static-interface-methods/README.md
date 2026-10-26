# Lesson 04 - Default and Static Methods in Interfaces

> **Level:** Beginner  |  **Time:** 15 minutes  |  **Run it:** `.\run-java8.ps1 4` / `./run-java8.sh 4`

## Why did Java add this?

Imagine you own a popular interface `List`, used by millions of classes. You want to add a new method `stream()`.
Before Java 8, adding a method to an interface **broke every class** that implemented it.

**Solution:** a **default method** - a method *with a body* inside the interface.
Old classes keep working and automatically receive the new method.

This is exactly how Java added `stream()`, `forEach()`, `removeIf()` and `sort()` to old collections.

## Default method

```java
interface Vehicle {
    String name();                       // abstract

    default String describe() {          // has a body!
        return "This is a " + name();
    }
}
```

A class that implements `Vehicle` only has to write `name()`. It may also **override** `describe()`.

## Static method in an interface

```java
interface MathUtil {
    static int square(int x) { return x * x; }
}
int r = MathUtil.square(7);   // call with the interface name
```

Static interface methods are **not inherited**. Only `MathUtil.square(...)` works.

## The "same default in two interfaces" problem

If a class implements two interfaces that both have `default move()`, Java cannot choose. You **must** override and decide:

```java
public String move() {
    return Walker.super.move() + " and " + Swimmer.super.move();
}
```

### Conflict rules (simple version)

1. A **class method always wins** over an interface default.
2. A **more specific interface** wins over a less specific one.
3. Otherwise -> you must override yourself (and may call `X.super.method()`).

## Default vs abstract class?

- Interfaces still have **no instance fields/state**. Defaults are for *behaviour built from the other methods*.
- A class can implement many interfaces, but extend only one class.

## Try it yourself

1. Add a `default String honk()` to `Vehicle` that returns `"beep"`. Does `Car` need to change?
2. Make `Bike` call the default `describe()` of `Vehicle` from inside its override. (Hint: `Vehicle.super.describe()`)
3. Why can you write `list.forEach(...)` on a Java 7-era custom list class when you compile with Java 8?

## In newer Java (9 to 21)

Java 9 added **private methods in interfaces**, so several default methods can share one helper (Java 8 interfaces could not have them). See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Oracle Tutorial - Default Methods: <https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html>
- Language spec, interfaces (9.4 Method Declarations): <https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.4>

**Previous:** [Lesson 03](../03-method-references/README.md) | **Next:** [Lesson 05 - Optional](../05-optional/README.md)
