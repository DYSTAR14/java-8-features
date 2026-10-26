# Modern 02 - Java 12 to 17: records, switch expressions, text blocks

> **Needs:** JDK 17 or newer (JDK 21 recommended)  |  **Run it:** `.\run-modern.ps1 2` / `./run-modern.sh 2`
> **Level:** Intermediate  |  **Time:** 35 minutes  |  Java 17 is a **long-term-support (LTS)** release.

This range changes **how you write the data and the branching around your streams**. The stream methods themselves got only a few additions.

## Quick table

| Topic | Java 8 way | Newer way | Since |
|---|---|---|---|
| Two results from one pass | two streams, or a custom `Collector` | `Collectors.teeing(c1, c2, merge)` | 12 |
| Stream to list | `collect(Collectors.toList())` | `.toList()` (**unmodifiable**) | 16 |
| One item -> 0, 1 or many items | `flatMap` (creates a stream per item) | `mapMulti((x, sink) -> ...)` | 16 |
| Multi-line text | `"line\n" + "line\n"` | text block `"""` ... `"""` | 15 |
| Format a string | `String.format("%s", x)` | `"%s".formatted(x)` | 15 |
| `switch` that returns a value | `switch` + `break` + temp variable | `switch` expression with `->` and `yield` | 14 |
| Data class | class + constructor + getters + `equals` + `hashCode` + `toString` | `record Emp(String name, double salary) {}` | 16 |
| `instanceof` then cast | `if (o instanceof String) { String s = (String) o; }` | `if (o instanceof String s)` | 16 |
| Limit who may implement a type | not possible | `sealed interface X permits A, B {}` | 17 |
| Hex text | custom code | `HexFormat.of().formatHex(bytes)` | 17 |
| Random numbers | `Random`, `ThreadLocalRandom`, `SplittableRandom` (no common type) | `RandomGenerator` interface | 17 |
| Know **what** was null | `NullPointerException` with no detail | message like `Cannot invoke "String.length()" because "NAME" is null` | 14 |

## Records are perfect stream data

```java
record Emp(String name, String dept, double salary) {}

Map<String, Double> payroll = emps.stream()
        .collect(groupingBy(Emp::dept, summingDouble(Emp::salary)));
```

A record is **immutable** and gets `equals`, `hashCode` and `toString` for free. Compare it with the Java 8 `Employee` class in `lessons-java8/common/Employee.java` (about 30 lines). The main lessons already use a record for `Employee`.

## `Stream.toList()` is NOT the same as `Collectors.toList()`

| | `collect(Collectors.toList())` | `stream.toList()` (16) |
|---|---|---|
| Can you add items? | yes (an `ArrayList` in practice, but not promised) | **no**, `UnsupportedOperationException` |
| Allows `null` items? | yes | yes |

If other code later modifies the list, keep `Collectors.toList()` (or `toCollection(ArrayList::new)`).

## Things from these releases that are not code demos

| Item | Version | Source |
|---|---|---|
| Switch expressions (standard) | 14 | JEP 361 |
| Helpful NullPointerExceptions | 14 | JEP 358 |
| Text blocks (standard) | 15 | JEP 378 |
| **Nashorn removed** (the `jjs` tool too) | 15 | JEP 372 |
| Records, pattern matching for `instanceof` (standard) | 16 | JEP 395, JEP 394 |
| Strongly encapsulate JDK internals by default | 16 (and 17) | JEP 396, JEP 403 |
| Sealed classes | 17 | JEP 409 |
| Enhanced pseudo-random number generators | 17 | JEP 356 |
| Applet API deprecated for removal | 17 | JEP 398 |
| Security Manager deprecated for removal | 17 | JEP 411 |

> **Upgrade warning:** if your old Java 8 code uses reflection on JDK internals (`sun.misc.*`, `setAccessible` on JDK classes), JEP 396/403 may break it on Java 16/17+. Fix it, or use `--add-opens` as a short-term workaround.

## Try it yourself

1. Open `lessons-java8/common/Employee.java` (the Java 8 class) and rewrite it as a record yourself. Then compare with `lessons/common/Employee.java`.
2. Write a switch expression that maps a month number to the number of days (use a `case 4, 6, 9, 11 ->` group).
3. Use `Collectors.teeing` to compute the **youngest and oldest** employee in one pass.

## Official sources

- JDK 14 JEP list: <https://openjdk.org/projects/jdk/14/>
- JDK 15 JEP list: <https://openjdk.org/projects/jdk/15/>
- JDK 16 JEP list: <https://openjdk.org/projects/jdk/16/>
- JDK 17 JEP list: <https://openjdk.org/projects/jdk/17/>
- JEP 395 (Records): <https://openjdk.org/jeps/395>
- `Stream` API for Java 21 (`toList`, `mapMulti`, the `count()` note): <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html>

**Previous:** [Modern 01](../01-java9-to-11/README.md) | **Next:** [Modern 03 - Java 18 to 21](../03-java18-to-21/README.md)
