# Lesson 17 - More Java 8 Features

> **Level:** Intermediate  |  **Time:** 45 minutes  |  **Run it:** `.\run.ps1 17` / `./run.sh 17`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/17-more-java8-features](../../lessons-java8/17-more-java8-features/README.md).

Small additions, big daily use. Many of them take lambdas, so they feel like the stream world.

## 1. New `Map` methods (you will use these every week)

| Method | What it does | Typical use |
|---|---|---|
| `getOrDefault(k, def)` | value, or `def` if missing | no more `if (map.containsKey...)` |
| `putIfAbsent(k, v)` | put only when key is missing | defaults |
| `computeIfAbsent(k, fn)` | create the value if missing, return it | **group into lists**: `map.computeIfAbsent(k, x -> new ArrayList<>()).add(item)` |
| `computeIfPresent(k, fn)` | change the value if it exists | update |
| `compute(k, fn)` | change the value, present or not | update |
| `merge(k, v, fn)` | put `v`, or combine old and new with `fn` | **count**: `map.merge(word, 1, Integer::sum)` |
| `replaceAll(fn)` | change every value | |
| `forEach((k, v) -> ...)` | loop | |
| `remove(k, v)` | remove only if value matches | |

## 2. New `List` / `Collection` methods

`removeIf(predicate)`, `replaceAll(unaryOperator)`, `sort(comparator)`, `forEach(consumer)`, `stream()`, `parallelStream()`.

## 3. Comparator

`Comparator.comparing(...)`, `comparingInt/Long/Double(...)`, `thenComparing(...)`, `reversed()`, `naturalOrder()`, `reverseOrder()`, `nullsFirst(...)`, `nullsLast(...)`.

## 4. Strings

```java
String.join("-", "2024", "03", "15");            // 2024-03-15
new StringJoiner(", ", "{", "}").add("a").add("b");   // {a, b}
```

## 5. `Base64`

```java
String enc = Base64.getEncoder().encodeToString(bytes);
byte[] dec = Base64.getDecoder().decode(enc);
```
Also `getUrlEncoder()` and `getMimeEncoder()`.

## 6. Helpers

- `Objects.requireNonNull(x, "message")`, `Objects.nonNull`, `Objects.isNull` (method-reference friendly!).
- `Integer.sum / max / min` -> `reduce(Integer::sum)`.
- `Math.addExact`, `multiplyExact`, `toIntExact` -> **throw** instead of silently overflowing.
- `Math.floorMod`, `Integer.toUnsignedString`, `Integer.divideUnsigned`.

## 7. Arrays

| Method | Use |
|---|---|
| `Arrays.parallelSort(a)` | sort big arrays using several cores |
| `Arrays.setAll(a, i -> ...)` | fill by index |
| `Arrays.parallelPrefix(a, op)` | running totals |
| `Arrays.stream(a)` | array to stream |

## 8. Annotations

- **Repeating annotations:** `@Role("admin") @Role("editor")` with `@Repeatable`.
- **Type annotations:** annotations can go on types, e.g. `List<@NonNull String>` (used by checker tools).

## 9. Concurrency extras

`LongAdder` / `DoubleAdder` (fast counters), `StampedLock`, `ThreadLocal.withInitial(...)`, `ConcurrentHashMap` bulk operations (`forEach`, `reduce`, `search` with a parallelism threshold).

## 10. Platform changes (good to know)

- **Nashorn** JavaScript engine (removed in Java 15).
- **PermGen removed**, replaced by **Metaspace** (class metadata uses native memory).
- Compile with `javac -parameters` to keep method parameter names for reflection.

## Try it yourself

1. Count the letters of `"mississippi"` using `Map.merge`.
2. Group the words `["apple","avocado","banana","blueberry","cherry"]` by first letter using `computeIfAbsent`.
3. Fill an array with the first 10 powers of 2 using `Arrays.setAll`.

## What changed after Java 8 (the code in this lesson already uses it)

Java 9: `List.of`, `Set.of`, `Map.of` (immutable, no `null`). Java 11: `String.isBlank/strip/repeat/lines`, `Files.readString`. Java 21: sequenced collections (`getFirst`, `getLast`, `reversed`). **Nashorn** was deprecated in 11 and removed in 15 (JEP 372). See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- **What's New in JDK 8 (official overview):** <https://www.oracle.com/java/technologies/javase/8-whats-new.html>
- Compatibility guide: <https://www.oracle.com/java/technologies/javase/8-compatibility-guide.html>
- Map API: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html>
- Base64 API: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Base64.html>
- Repeating annotations tutorial: <https://docs.oracle.com/javase/tutorial/java/annotations/repeating.html>

**Previous:** [Lesson 16](../16-completable-future/README.md) | **Next:** [Lesson 18 - Stream Practice (48 problems)](../18-stream-practice/README.md)
