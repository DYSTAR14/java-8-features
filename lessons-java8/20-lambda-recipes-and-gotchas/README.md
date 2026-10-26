# Lesson 20 - Lambda Recipes and Gotchas

> **Level:** Advanced  |  **Time:** 45 minutes  |  **Run it:** `.\run-java8.ps1 20` / `./run-java8.sh 20`

Small patterns and traps that show up in real code and in interviews.

## 1. The type-inference trap (very common!)

```java
staff.sort(Comparator.comparing(e -> e.getSalary()).reversed());   // COMPILE ERROR
//                                 ^ Java thinks 'e' is Object
```

Java reads a chain from left to right. At `comparing(e -> ...)` it does not yet know `e` is an `Employee`, because `.reversed()` comes later and cannot help.

| Fix | Code |
|---|---|
| Method reference (best) | `comparing(Employee::getSalary).reversed()` |
| Write the lambda type | `comparing((Employee e) -> e.getSalary()).reversed()` |
| Type witness | `Comparator.<Employee, Double>comparing(e -> e.getSalary()).reversed()` |
| Avoid `reversed()` | `comparing(Employee::getSalary, Comparator.reverseOrder())` |

The same happens with `.thenComparing(...)` after a lambda-based `comparing(...)`.

## 2. Lambdas with threads

```java
new Thread(() -> doWork()).start();
Future<Integer> f = pool.submit(() -> 6 * 7);   // returns a value -> Callable
```

A lambda that **returns a value** is a `Callable`. One that returns **nothing** is a `Runnable`.

## 3. Lazy values: pass a `Supplier`

```java
log(debugOn, () -> "report: " + buildExpensiveReport());   // built ONLY if debugOn
```

Passing the *value* always does the work. Passing a *Supplier* does it only when needed.

## 4. Functions that return functions

```java
static Predicate<String> longerThan(int n) { return s -> s.length() > n; }
words.stream().filter(longerThan(4))
```

**Currying:** `Function<Integer, Function<Integer,Integer>> add = a -> b -> a + b;`
Chain many steps: `steps.stream().reduce(Function.identity(), Function::andThen)`.

## 5. Strategy pattern with a Map of lambdas

```java
Map<String, BinaryOperator<Integer>> ops = new HashMap<>();
ops.put("+", (a, b) -> a + b);
ops.get("+").apply(8, 3);
```

No long `if / else if / switch`. New behaviour = one new line.

## 6. Checked exceptions inside lambdas

`Function` cannot throw checked exceptions. Write a wrapper **once**:

```java
@FunctionalInterface interface ThrowingFunction<T,R> { R apply(T t) throws Exception; }

static <T,R> Function<T,R> unchecked(ThrowingFunction<T,R> f) {
    return t -> { try { return f.apply(t); } catch (Exception e) { throw new RuntimeException(e); } };
}

stream.map(unchecked(this::readFile))
```

## 7. More stream tools

| Tool | Use |
|---|---|
| `groupingBy(f, mapping(g, toSet()))` | groups with unique values inside |
| `Stream.builder()` | build a stream item by item |
| `flatMapToInt(Arrays::stream)` | flatten a 2D `int[][]` with no boxing |
| `groupingByConcurrent`, `toConcurrentMap` | build maps from parallel streams |
| `filter(...).forEach(...)` | better than `forEach` with an `if` inside |

## 8. `unordered()`

A parallel ordered stream must remember the order of items - this costs time for `distinct()` and `limit()`.
If order does not matter, call `.unordered()` to allow a faster algorithm.

## 9. `groupingBy` and `null`

A classifier that returns `null` throws `NullPointerException` ("element cannot be mapped to a null key"). Give `null` a real key, or filter nulls first.

## Try it yourself

1. Sort employees by department, then by salary (highest first) using **only lambdas** (not method references). Make it compile!
2. Write `Function<Integer, Function<Integer, Function<Integer, Integer>>>` that adds 3 numbers by currying.
3. Add a `"/"` operation to the calculator. How do you handle division by zero?

## Official sources

- Oracle Tutorial - Lambda Expressions: <https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html>
- `Comparator` API: <https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html>
- Stream package (Ordering, Parallelism): <https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Ordering>
- JLS 15.12.2.2 (how Java chooses a method with lambda arguments): <https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12.2.2>

**Previous:** [Lesson 19](../19-streams-with-files-and-text/README.md) | **Back to the start:** [Main README](../../README.md)
