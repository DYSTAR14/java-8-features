# Java 8 Master Cheat Sheet (with Java 9 to 21 updates)

**Lambdas, Streams, Collectors, Optional, Comparator, Date/Time, CompletableFuture** in one place.
Simple words. Every example was compiled and run (179 checks, see the end).

> **How to read this sheet**
> - Code lines end with `// result`. That result is real output.
> - A tag like **[9]** means "needs Java 9 or newer". No tag = works in **Java 8**.
> - `Employee` data used in the examples is at the end of the sheet (section 20).

**Jump to:**
[1 Big picture](#1-the-big-picture) |
[2 Lambdas](#2-lambdas-and-method-references) |
[3 Interfaces](#3-functional-interfaces) |
[4 Create](#4-create-a-stream) |
[5 Intermediate](#5-intermediate-operations) |
[6 Terminal](#6-terminal-operations) |
[7 Primitive](#7-primitive-streams) |
[8 Collectors](#8-collectors) |
[9 Optional](#9-optional) |
[10 Comparator](#10-comparator-and-sorting) |
[11 Helpers](#11-collection-map-and-string-helpers) |
[12 Date/Time](#12-date-and-time) |
[13 Async](#13-completablefuture) |
[14 Parallel](#14-parallel-streams) |
[15 Recipes](#15-recipes-copy-and-use) |
[16 SQL](#16-sql-to-stream) |
[17 Decide](#17-which-one-do-i-use) |
[18 Traps](#18-traps-and-fixes) |
[19 Speed](#19-speed-tips) |
[20 Data](#20-the-sample-data) |
[21 Java 9-21](#21-whats-new-after-java-8) |
[22 Rules](#22-golden-rules)

---

## 0. The 12 things to remember

| # | Remember |
|---|---|
| 1 | A stream is a **pipeline**: `source -> intermediate steps -> ONE terminal step`. |
| 2 | **No terminal step = nothing runs** (streams are lazy). |
| 3 | A stream is used **once**. Make a new one. |
| 4 | A stream **never changes** its source list. |
| 5 | Four interfaces run everything: `Predicate` (yes/no), `Function` (change), `Consumer` (use), `Supplier` (make). |
| 6 | `filter` keeps, `map` changes, `flatMap` flattens, `sorted` orders, `distinct` removes duplicates. |
| 7 | `collect(groupingBy(key, downstream))` answers most "report" questions. |
| 8 | `min`, `max`, `findFirst`, `reduce(op)` return an **Optional**. Never call `get()` blindly. |
| 9 | Numbers: use `IntStream` / `mapToInt` (no boxing). |
| 10 | Parallel streams are **not** always faster. Measure. No shared variables. |
| 11 | `toMap` with a duplicate key **throws**. Add a merge function. |
| 12 | `comparing(e -> ...).reversed()` does **not compile**. Use a method reference. |

---

## 1. The big picture

```
 SOURCE            INTERMEDIATE (lazy, 0 or more)             TERMINAL (starts the work, exactly 1)
 list.stream()  -> filter -> map -> sorted -> limit ... -> collect / forEach / count / reduce ...
```

| Part | Returns | Example |
|---|---|---|
| Source | a stream | `list.stream()`, `Stream.of(..)`, `IntStream.range(..)` |
| Intermediate | a **new stream** (chain it) | `filter`, `map`, `sorted` |
| Terminal | a **result** (or nothing) | `collect`, `count`, `forEach` |

Items flow **one by one** through the whole pipeline, not step by step. That is why `limit(2)` can stop early.

---

## 2. Lambdas and method references

**Lambda** = a small function with no name: `(parameters) -> body`

| You write | Meaning |
|---|---|
| `() -> 42` | no input, returns 42 |
| `x -> x * 2` | one input (brackets optional) |
| `(a, b) -> a + b` | two inputs |
| `(int a, int b) -> a + b` | types written (optional) |
| `x -> { int y = x * 2; return y; }` | many lines: `{ }` and `return` |
| `(var a, var b) -> a + b` **[11]** | `var` as the type (lets you add annotations) |

**Rules:** a lambda can read outside variables only if they are **effectively final** (never changed). Inside a lambda, `this` is the outer class.

**Method reference** `::` = a lambda that only calls one method.

| Kind | Write | Same as | Result |
|---|---|---|---|
| Static method | `Integer::parseInt` | `s -> Integer.parseInt(s)` | `"42"` -> `42` |
| Method of an object | `prefix::concat` | `s -> prefix.concat(s)` | `"pen"` -> `"Item: pen"` |
| Method of any object of a class | `String::length` | `s -> s.length()` | `"abc"` -> `3` |
| Constructor | `ArrayList::new` | `() -> new ArrayList<>()` | `[]` |
| Array constructor | `String[]::new` | `n -> new String[n]` | used in `toArray(String[]::new)` |

> Left side is an **object** -> bound. Left side is a **class** and the method is not static -> the first parameter becomes the object.

---

## 3. Functional interfaces

A functional interface has **one abstract method**. A lambda fits into it. Package: `java.util.function`.

| Interface | Shape | Method | Used by |
|---|---|---|---|
| `Predicate<T>` | `T -> boolean` | `test` | `filter`, `anyMatch`, `removeIf`, `partitioningBy` |
| `Function<T,R>` | `T -> R` | `apply` | `map`, `flatMap`, `groupingBy` |
| `Consumer<T>` | `T -> void` | `accept` | `forEach`, `peek`, `ifPresent` |
| `Supplier<T>` | `() -> T` | `get` | `generate`, `orElseGet`, `toCollection` |
| `UnaryOperator<T>` | `T -> T` | `apply` | `iterate`, `replaceAll` |
| `BinaryOperator<T>` | `(T,T) -> T` | `apply` | `reduce`, `toMap` merge |
| `BiFunction<T,U,R>` | `(T,U) -> R` | `apply` | `Map.replaceAll`, `Map.compute` |
| `BiPredicate`, `BiConsumer` | two inputs | `test`, `accept` | `Map.forEach` |
| `Comparator<T>` | `(T,T) -> int` | `compare` | `sorted`, `min`, `max` |
| `Runnable` / `Callable<V>` | `() -> void` / `() -> V` | `run` / `call` | threads, executors |
| `IntPredicate`, `ToIntFunction<T>`, `IntBinaryOperator`... | primitive versions | | no boxing: faster |

**Combine them:**

```java
Predicate<Integer> even = n -> n % 2 == 0, big = n -> n > 100;
even.and(big).test(200);    // true
even.or(big).test(7);       // false
even.negate().test(7);      // true
Predicate.not(String::isBlank)   // [11] negate a method reference

Function<Integer,Integer> dbl = n -> n * 2, plus1 = n -> n + 1;
dbl.andThen(plus1).apply(5);   // 11  (dbl first, then plus1)
dbl.compose(plus1).apply(5);   // 12  (plus1 first, then dbl)

BinaryOperator.minBy(Comparator.<Integer>naturalOrder()).apply(8, 3);   // 3
Function<Integer, Function<Integer, Integer>> add = a -> b -> a + b;    // currying
add.apply(10).apply(5);        // 15
```

---

## 4. Create a stream

| I have... | Code | Result |
|---|---|---|
| a List / Set | `list.stream()` | |
| an array | `Arrays.stream(arr)` | `Arrays.stream(new int[]{1,2,3}).sum()` -> `6` |
| a few values | `Stream.of(a, b, c)` | |
| number range, end **excluded** | `IntStream.range(0, 5)` | `[0, 1, 2, 3, 4]` |
| number range, end **included** | `IntStream.rangeClosed(1, 5)` | `[1, 2, 3, 4, 5]` |
| infinite, from a start value | `Stream.iterate(1, n -> n * 2).limit(5)` | `[1, 2, 4, 8, 16]` |
| infinite, from a supplier | `Stream.generate(() -> "x").limit(3)` | `[x, x, x]` |
| loop with a stop condition **[9]** | `Stream.iterate(1, n -> n < 100, n -> n * 3)` | `[1, 3, 9, 27, 81]` |
| a String's characters | `"abc".chars()` | `[97, 98, 99]` (int codes) |
| a Map | `map.entrySet().stream()`, `map.keySet().stream()`, `map.values().stream()` | |
| a file's lines | `Files.lines(path)` (**close it**: try-with-resources) | |
| 0 or 1 item **[9]** | `Stream.ofNullable(x)` | `null` gives an empty stream |
| join two streams | `Stream.concat(a, b)` | `[1, 2, 3, 4]` |
| nothing | `Stream.empty()` | |

> **Trap:** `Stream.of(intArray)` is a stream of **one** array (count = `1`). Use `Arrays.stream(intArray)`.

---

## 5. Intermediate operations

They return a **new stream** and are **lazy**.

| Operation | Does | Takes |
|---|---|---|
| `filter(p)` | keep matching items | `Predicate` |
| `map(f)` | change each item | `Function` |
| `mapToInt/Long/Double(f)` | change to a primitive stream | `ToIntFunction`... |
| `mapToObj(f)`, `boxed()` | primitive stream -> object stream | |
| `flatMap(f)` | each item -> a stream, then merge all | `Function` returning a Stream |
| `distinct()` | remove duplicates (`equals`) | |
| `sorted()` / `sorted(cmp)` | sort | `Comparator` |
| `limit(n)` / `skip(n)` | first n / drop first n | |
| `peek(c)` | look at items (**debug only**) | `Consumer` |
| `takeWhile(p)` / `dropWhile(p)` **[9]** | stop / skip while a test is true | `Predicate` |
| `mapMulti(..)` **[16]** | each item -> 0, 1 or many items (alternative to `flatMap`) | `BiConsumer` |
| `parallel()` / `sequential()` | switch mode | |
| `unordered()` | "order does not matter" (faster parallel `distinct`, `limit`) | |

**Pictures** (`input --operation--> output`):

| Operation | Input | Output |
|---|---|---|
| `filter(x -> x % 2 == 0)` | `1 2 3 4 5` | `2 4` |
| `map(x -> x * 10)` | `1 2 3` | `10 20 30` |
| `flatMap(x -> Stream.of(x, -x))` | `1 2 3` | `1 -1 2 -2 3 -3` |
| `flatMap(List::stream)` | `[1,2] [3] [4,5]` | `1 2 3 4 5` |
| `distinct()` | `1 1 2 2 3` | `1 2 3` |
| `sorted()` | `4 2 1 5 3` | `1 2 3 4 5` |
| `sorted(Comparator.reverseOrder())` | `1 3 2` | `3 2 1` |
| `limit(3)` | `1 2 3 4 5` | `1 2 3` |
| `skip(2)` | `1 2 3 4 5` | `3 4 5` |
| `skip(5).limit(5)` on `1..20` | | `6 7 8 9 10` (a page) |
| `takeWhile(n -> n < 5)` **[9]** | `1 2 3 10 11 4` | `1 2 3` (stops at 10) |
| `dropWhile(n -> n < 5)` **[9]** | `1 2 3 10 11 4` | `10 11 4` |
| `filter(n -> n < 5)` (compare!) | `1 2 3 10 11 4` | `1 2 3 4` (never stops) |
| `mapMulti` odd -> n and n*10 **[16]** | `1 2 3 4` | `1 10 3 30` |

**`map` or `flatMap`?** `map` = one in, one out. `flatMap` = one in, **many** out, flattened. Use it for `Stream<List<X>>` or `Stream<X[]>`.

---

## 6. Terminal operations

They **start** the pipeline. After one, the stream is closed.

| Operation | Returns | Stops early? |
|---|---|---|
| `forEach(c)` | nothing | no |
| `collect(collector)` | list / map / String / number ... | no |
| `toArray(String[]::new)` | array | no |
| `toList()` **[16]** | **unmodifiable** `List` | no |
| `count()` | `long` (not `int`) | no |
| `min(cmp)` / `max(cmp)` | `Optional<T>` | no |
| `reduce(identity, op)` | a value | no |
| `reduce(op)` | `Optional<T>` | no |
| `findFirst()` / `findAny()` | `Optional<T>` | **yes** |
| `anyMatch` / `allMatch` / `noneMatch` | `boolean` | **yes** |

**Pictures** (input `1 2 3 4 5`):

| Operation | Result |
|---|---|
| `filter(even).findFirst()` | `Optional[2]` |
| `allMatch(x -> x < 6)` | `true` |
| `anyMatch(x -> x > 4)` | `true` |
| `noneMatch(x -> x > 5)` | `true` |
| `count()` | `5` |
| `min(naturalOrder())` / `max(naturalOrder())` | `Optional[1]` / `Optional[5]` |
| `reduce(0, Integer::sum)` | `15` |
| `reduce(Integer::max)` | `Optional[5]` |
| `Stream.of("ab","cde").reduce(0, (acc, s) -> acc + s.length(), Integer::sum)` | `5` (3-argument form: result type differs) |
| `forEach(print)` | nothing (`void`) |

**How `reduce(0, (a, b) -> a + b)` works on `[1, 2, 3]`:** `0+1=1`, `1+2=3`, `3+3=6`. The rule must be **associative** (`+`, `*`, `max` yes; `-` no).

`findAny` is fastest in **parallel** streams. `findFirst` keeps the order.

---

## 7. Primitive streams

`IntStream`, `LongStream`, `DoubleStream`: **no boxing** (faster) and extra methods.

| Need | Code | Result |
|---|---|---|
| sum | `IntStream.of(3, 1, 2).sum()` | `6` |
| average | `IntStream.of(3, 1, 2).average()` | `OptionalDouble[2.0]` |
| max / min | `IntStream.of(3, 1, 2).max()` | `OptionalInt[3]` |
| average of empty | `IntStream.empty().average().orElse(0)` | `0.0` |
| everything in one pass | `IntStream.of(3, 1, 2).summaryStatistics()` | `count=3, sum=6, min=1, average=2.000000, max=3` |
| to objects | `IntStream.rangeClosed(1, 3).boxed()` | `[1, 2, 3]` |
| to anything | `IntStream.rangeClosed(1, 3).mapToObj(i -> "Item-" + i)` | `[Item-1, Item-2, Item-3]` |
| sum of a field | `staff.stream().mapToDouble(Employee::salary).sum()` | `793000.0` |
| big numbers | `LongStream.rangeClosed(1, 20).reduce(1L, (a, b) -> a * b)` | `2432902008176640000` |

```
 Stream<T> --mapToInt/mapToLong/mapToDouble--> IntStream / LongStream / DoubleStream
 IntStream --boxed() / mapToObj()-----------> Stream<Integer> / Stream<R>
 IntStream --asLongStream() / asDoubleStream()
```

`int` overflows silently near 2.1 billion. Use `LongStream` or `Math.addExact`.

---

## 8. Collectors

`stream.collect(collector)` = gather the items into a result.

| I want | Collector | Example result |
|---|---|---|
| a List / Set | `toList()` / `toSet()` | |
| a specific collection | `toCollection(TreeSet::new)` | `[a, b, c]` |
| a read-only List **[10]** | `toUnmodifiableList()` (also `Set`, `Map`) | rejects `null` |
| a Map | `toMap(keyFn, valueFn)` | **throws** on duplicate key |
| a Map, safe | `toMap(keyFn, valueFn, (a, b) -> a)` | keeps the first |
| a Map of a chosen type | `toMap(k, v, merge, LinkedHashMap::new)` | `{Alice=30, Bob=45, Carol=28}` |
| one text | `joining(", ", "[", "]")` | `[a, b, c]` |
| count | `counting()` | `10` |
| sum | `summingInt/Long/Double(f)` | `summingInt(Employee::age)` -> `346` |
| average | `averagingInt/Long/Double(f)` | `79300.0` |
| smallest / biggest | `minBy(cmp)` / `maxBy(cmp)` | `Optional` |
| all statistics | `summarizingInt/Long/Double(f)` | `count=10, sum=346, min=26, average=34.600000, max=45` |
| your own reduce | `reducing(0.0, Employee::salary, Double::sum)` | `793000.0` |
| groups | `groupingBy(f)` / `groupingBy(f, downstream)` / `groupingBy(f, TreeMap::new, downstream)` | |
| 2 groups | `partitioningBy(p)` / `partitioningBy(p, downstream)` | `{false=.., true=..}` |
| change items inside | `mapping(f, downstream)` | `ALICE\|BOB\|CAROL` with `joining("\|")` |
| filter inside a group **[9]** | `filtering(p, downstream)` | keeps empty groups |
| flatMap inside a group **[9]** | `flatMapping(f, downstream)` | |
| two results, one pass **[12]** | `teeing(c1, c2, merger)` | `10 people, average 79300` |
| change the final result | `collectingAndThen(downstream, f)` | e.g. make it read-only |

### groupingBy: the downstream cheat sheet

`groupingBy(classifier, downstream)`. The **downstream** decides what happens inside each group.

| Per group I want... | Downstream | Example (by department) |
|---|---|---|
| how many | `counting()` | `{Engineering=3, HR=2, Marketing=2, Sales=3}` |
| sum / average | `summingDouble(f)` / `averagingDouble(f)` | HR average = `59000.0` |
| a list of one field | `mapping(Employee::name, toList())` | Sales = `[Carol, Dave, Judy]` |
| the best item | `collectingAndThen(maxBy(cmp), o -> o.get().name())` | `{Engineering=Alice, HR=Eve, Marketing=Heidi, Sales=Judy}` |
| a group inside a group | `groupingBy(Employee::city, TreeMap::new, counting())` | Sales = `{Berlin=1, London=2}` |
| filter, keep empty groups **[9]** | `filtering(e -> e.salary() > 100000, mapping(Employee::name, toList()))` | `{Engineering=[Alice, Frank], HR=[], Marketing=[], Sales=[]}` |

```java
// split in two: age 40 and older, or younger
partitioningBy(e -> e.age() >= 40, mapping(Employee::name, toList()))
// {false=[Alice, Carol, Dave, Frank, Grace, Heidi, Ivan], true=[Bob, Eve, Judy]}
```

> `groupingBy` returns a `HashMap` (order not guaranteed). Pass `TreeMap::new` (sorted) or `LinkedHashMap::new` (insertion order) when order matters.
> `partitioningBy` **always** has both keys `true` and `false`.

### The two `toList`s

| | `collect(Collectors.toList())` | `stream.toList()` **[16]** |
|---|---|---|
| Can you add items? | not promised (Javadoc: "no guarantees on type, mutability...") | **no**: `UnsupportedOperationException` |
| `null` items | allowed | allowed |
| Need a list you can change? | `collect(Collectors.toCollection(ArrayList::new))` | same |

---

## 9. Optional

A box with **one value or nothing**. Use it as a **return type**, not as a field or parameter.

| Goal | Code |
|---|---|
| make | `Optional.of(x)` (x not null), `Optional.ofNullable(x)`, `Optional.empty()` |
| is there a value? | `opt.isPresent()` / `opt.isEmpty()` **[11]** |
| do something if present | `opt.ifPresent(v -> ...)` |
| do A if present, else B **[9]** | `opt.ifPresentOrElse(v -> ..., () -> ...)` |
| value or fixed fallback | `opt.orElse("x")` |
| value or fallback computed **only when needed** | `opt.orElseGet(() -> compute())` |
| value or exception | `opt.orElseThrow(() -> new MyException())` |
| value or `NoSuchElementException` **[10]** | `opt.orElseThrow()` (preferred over `get()`) |
| change the value | `opt.map(f)` |
| function that returns an Optional | `opt.flatMap(f)` |
| keep only if a test passes | `opt.filter(p)` |
| fall back to another Optional **[9]** | `opt.or(() -> otherOptional)` |
| 0 or 1 item stream **[9]** | `opt.stream()` (drop empties: `.flatMap(Optional::stream)`) |

```java
Optional.of(" java ").map(String::trim).filter(s -> !s.isEmpty()).map(String::toUpperCase).orElse("?");   // JAVA
Stream.of(Optional.of("a"), Optional.<String>empty(), Optional.of("c")).flatMap(Optional::stream).toList();   // [a, c]  [9] [16]
```

> `orElse(x)` **always** builds `x`. `orElseGet(supplier)` builds it only if empty. Use `orElseGet` for anything slow.
> `Optional.of(null)` throws. Do not return `Optional<List<..>>`: return an empty list.

---

## 10. Comparator and sorting

| Need | Code |
|---|---|
| by a field | `Comparator.comparing(Employee::name)` |
| by a number field (no boxing) | `Comparator.comparingInt(..)`, `comparingDouble(..)`, `comparingLong(..)` |
| biggest first | `Comparator.comparing(Employee::salary).reversed()` |
| tie-break | `.thenComparing(Employee::name)` |
| tie-break, reversed | `.thenComparing(Employee::salary, Comparator.reverseOrder())` |
| natural / reverse order | `Comparator.naturalOrder()` / `Comparator.reverseOrder()` |
| nulls | `Comparator.nullsFirst(..)` / `Comparator.nullsLast(..)` |
| ignore case | `String.CASE_INSENSITIVE_ORDER` |
| sort a list in place | `list.sort(cmp)` |
| sort map entries by value | `Map.Entry.<String, Long>comparingByValue().reversed()` |

```java
// department A-Z, then salary highest first
Comparator.comparing(Employee::department).thenComparing(Employee::salary, Comparator.reverseOrder())   // [Alice, Frank, Bob] first 3
// by length, then A-Z
Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())   // [fig, kiwi, pear, apple]
Stream.of("b", null, "a").sorted(Comparator.nullsLast(Comparator.<String>naturalOrder()))   // [a, b, null]
```

> **Trap:** `Comparator.comparing(e -> e.salary()).reversed()` does **not compile** (Java thinks `e` is `Object`).
> Fix: `comparing(Employee::salary).reversed()` or `comparing((Employee e) -> e.salary()).reversed()`.

---

## 11. Collection, Map and String helpers

**Map**

| Method | Does | Example |
|---|---|---|
| `getOrDefault(k, d)` | value or default | `new HashMap<String,Integer>().getOrDefault("x", 0)` -> `0` |
| `putIfAbsent(k, v)` | put only if missing | |
| `computeIfAbsent(k, f)` | create the value if missing | **group into lists**: `map.computeIfAbsent(w.length(), k -> new ArrayList<>()).add(w)` |
| `computeIfPresent(k, f)` / `compute(k, f)` | change a value | |
| `merge(k, v, f)` | put, or combine with the old value | **count**: `map.merge(word, 1, Integer::sum)` -> `{be=2, not=1, or=1, to=2}` |
| `replaceAll(f)` / `forEach((k, v) -> ..)` | change all / loop | |

**List / Collection:** `removeIf(p)`, `replaceAll(op)`, `sort(cmp)`, `forEach(c)`, `stream()`.

**String:** `String.join("-", "2024", "03", "15")` -> `2024-03-15` | `new StringJoiner(", ", "{", "}")` -> `{a, b}` | `Base64.getEncoder().encodeToString(bytes)` | `Arrays.setAll(arr, i -> i * i)` | `Arrays.parallelSort(arr)`.

**Newer helpers:**

| Need | Code | Since |
|---|---|---|
| small immutable list / set / map | `List.of(1, 2)`, `Set.of(..)`, `Map.of("a", 1)` (no `null`, `Set.of`/`Map.of` order undefined) | 9 |
| immutable copy | `List.copyOf(list)` (a snapshot) | 10 |
| blank / strip / repeat / lines | `s.isBlank()`, `s.strip()`, `"ab".repeat(3)`, `s.lines()` | 11 |
| whole file as text | `Files.readString(path)`, `Files.writeString(path, text)` | 11 |
| first / last / reverse view | `list.getFirst()`, `list.getLast()`, `list.reversed()` | 21 |
| clamp a number | `Math.clamp(15, 0, 10)` -> `10` | 21 |

---

## 12. Date and Time

`java.time`: **immutable** (`date.plusDays(1)` returns a **new** object: use it!), thread-safe.

| Class | Holds | Example |
|---|---|---|
| `LocalDate` | date | `LocalDate.of(2024, 3, 15)` |
| `LocalTime` | time | `LocalTime.of(14, 30)` |
| `LocalDateTime` | date + time, no zone | |
| `ZonedDateTime` | date + time + zone | meetings across the world |
| `Instant` | one moment (UTC) | timestamps |
| `Period` | years / months / days | `P9M10D` |
| `Duration` | hours / minutes / seconds | `PT8H45M` |

```java
LocalDate d = LocalDate.of(2024, 3, 15);
d.plusDays(10);                                         // 2024-03-25
d.getDayOfWeek();                                       // FRIDAY
ChronoUnit.DAYS.between(d, LocalDate.of(2024, 12, 25)); // 285
Period.between(d, LocalDate.of(2024, 12, 25));          // P9M10D
Period.between(LocalDate.of(1990, 6, 20), d).getYears();// 33  (age)
d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));    // 15/03/2024
LocalDate.parse("2024-03-15");                          // text -> date (ISO format)
Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 45));   // PT8H45M
Duration.ofMinutes(125).toHours();                      // 2
d.datesUntil(d.plusDays(3));                            // [9] stream of dates
```

| Pattern | Meaning | | Pattern | Meaning |
|---|---|---|---|---|
| `yyyy` | year | | `HH` | hour 24h |
| `MM` | **month** | | `mm` | **minute** |
| `dd` | day | | `ss` | second |
| `EEEE` | weekday name | | `MMMM` | month name |

> `MM` = month, `mm` = minute. Do not mix them. Use `DateTimeFormatter` (thread-safe), never `SimpleDateFormat`.

---

## 13. CompletableFuture

A **promise** of a value that arrives later. Chain steps without blocking.

| Method | Like | Meaning |
|---|---|---|
| `supplyAsync(() -> v, pool)` | | run on another thread, returns a value |
| `runAsync(() -> ..)` | | run on another thread, no value |
| `thenApply(f)` | `map` | change the result |
| `thenAccept(c)` | `forEach` | use the result |
| `thenCompose(f)` | `flatMap` | `f` returns another future |
| `thenCombine(other, f)` | | wait for **two** futures, merge |
| `allOf(f1, f2..)` / `anyOf(..)` | | wait for all / the first |
| `exceptionally(ex -> fallback)` | | recover from an error |
| `handle((v, ex) -> ..)` | | get result **and** error |
| `whenComplete((v, ex) -> ..)` | | look at the outcome (logging) |
| `join()` / `get()` | | wait (`join`: no checked exceptions) |
| `orTimeout(t, unit)` **[9]** | | fail with `TimeoutException` |
| `completeOnTimeout(v, t, unit)` **[9]** | | default value after the time |
| `failedFuture(ex)` **[9]** | | an already failed future (tests) |
| `exceptionallyAsync`, `exceptionallyCompose` **[12]** | | async recovery |
| `state()`, `resultNow()` **[19]** | | read a finished future, no waiting |

```java
CompletableFuture.supplyAsync(() -> 2).thenApply(x -> x * 10)
        .thenCombine(CompletableFuture.supplyAsync(() -> 5), Integer::sum).join();   // 25
CompletableFuture.supplyAsync(() -> 3).thenCompose(x -> CompletableFuture.supplyAsync(() -> x + 1)).join();   // 4
CompletableFuture.<Integer>supplyAsync(() -> { throw new IllegalStateException("x"); }).exceptionally(ex -> -1).join();   // -1
```

> **Start all futures first, then join.** `ids.stream().map(id -> supplyAsync(..)).map(CompletableFuture::join)` in **one** stream runs them one by one (the stream is lazy).
> Close your own pool: `try (var pool = Executors.newFixedThreadPool(4)) { .. }` **[19]**.
> Thousands of **waiting** tasks: `Executors.newVirtualThreadPerTaskExecutor()` **[21]**.

---

## 14. Parallel streams

```java
list.parallelStream()     // or stream.parallel()
IntStream.rangeClosed(1, 1000).parallel().sum();   // 500500, same answer as sequential
```

| Use when | Avoid when |
|---|---|
| many items (thousands+) and CPU-heavy work | few items, quick work |
| `ArrayList`, arrays, `IntStream.range` (split well) | `LinkedList`, `Stream.iterate` (split badly) |
| items are independent | shared variables, blocking calls (I/O) |
| `sum`, `count`, `max`, `collect` | `limit`, `findFirst`, `forEachOrdered` (need order) |

| Order in parallel | |
|---|---|
| `forEach` | any order |
| `forEachOrdered` | original order (slower) |
| `collect(toList())` | **keeps** the order |
| `findAny` / `findFirst` | any / first |

**Three dangers:** (1) shared variable: `counter[0]++` loses updates (use `count()`, `sum()`, `collect()`, `AtomicInteger`). (2) `reduce` with `-` (not associative). (3) blocking calls block the **shared** common pool.

Rule: write it sequential, **measure**, then maybe go parallel. Waiting work: virtual threads **[21]**.

---

## 15. Recipes (copy and use)

Data: the 10 employees in section 20. Results are real.

```java
// 1  names in a department
staff.stream().filter(e -> e.department().equals("Engineering")).map(Employee::name).collect(Collectors.toList());   // [Alice, Bob, Frank]

// 2  total and average
staff.stream().mapToDouble(Employee::salary).sum();             // 793000.0
staff.stream().mapToInt(Employee::age).average().orElse(0);     // 34.6

// 3  highest paid
staff.stream().collect(Collectors.maxBy(Comparator.comparingDouble(Employee::salary))).map(Employee::name).get();   // Alice

// 4  top 3 earners
staff.stream().sorted(Comparator.comparingDouble(Employee::salary).reversed()).limit(3).map(Employee::name).collect(Collectors.toList());   // [Alice, Frank, Bob]

// 5  second highest salary (distinct first!)
staff.stream().map(Employee::salary).distinct().sorted(Comparator.reverseOrder()).skip(1).findFirst().get();   // 105000.0

// 6  count per department (sorted keys)
staff.stream().collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.counting()));   // {Engineering=3, HR=2, Marketing=2, Sales=3}

// 7  best person per department
staff.stream().collect(Collectors.groupingBy(Employee::department, TreeMap::new,
        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingDouble(Employee::salary)), o -> o.get().name())));
        // {Engineering=Alice, HR=Eve, Marketing=Heidi, Sales=Judy}

// 8  sort a Map by value (biggest first), ties by key. perDept = groupingBy(Employee::department, counting())
perDept.entrySet().stream()
       .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
       .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());   // [Engineering=3, Sales=3, HR=2, Marketing=2]

// 9  first employee of each city, keep the order
staff.stream().collect(Collectors.toMap(Employee::city, Employee::name, (a, b) -> a, LinkedHashMap::new));   // {London=Alice, Paris=Bob, Berlin=Dave}

// 10 word frequency
Arrays.stream("the cat the dog the end".split(" "))
      .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));   // {cat=1, dog=1, end=1, the=3}

// 11 character frequency
"banana".chars().mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));   // {a=3, b=1, n=2}

// 12 find duplicates
Stream.of(1, 2, 3, 2, 4, 3).collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()))
      .entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList());   // [2, 3]

// 13 flatten lists
Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3), Arrays.asList(4, 5)).stream().flatMap(List::stream).collect(Collectors.toList());   // [1, 2, 3, 4, 5]

// 14 page 2 (size 5)
IntStream.rangeClosed(1, 20).boxed().skip(5).limit(5).collect(Collectors.toList());   // [6, 7, 8, 9, 10]

// 15 palindrome
IntStream.range(0, s.length() / 2).allMatch(i -> s.charAt(i) == s.charAt(s.length() - 1 - i));   // true for "racecar"

// 16 sum of digits
String.valueOf(12345).chars().map(c -> c - '0').sum();   // 15

// 17 reverse each word
Arrays.stream("java is fun".split(" ")).map(w -> new StringBuilder(w).reverse().toString()).collect(Collectors.joining(" "));   // avaj si nuf

// 18 sum of odd squares 1..10
IntStream.rangeClosed(1, 10).filter(n -> n % 2 == 1).map(n -> n * n).sum();   // 165

// 19 Fibonacci (carry two numbers)
Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]}).limit(10).map(f -> f[0]).collect(Collectors.toList());   // [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]

// 20 group anagrams (key = sorted letters)
Stream.of("listen", "silent", "enlist", "cat").collect(Collectors.groupingBy(
        w -> { char[] c = w.toCharArray(); Arrays.sort(c); return new String(c); }, TreeMap::new, Collectors.toList()))
      .values().stream().filter(g -> g.size() > 1).collect(Collectors.toList());   // [[listen, silent, enlist]]

// 21 count words with Map.merge (no stream)
Map<String, Integer> counts = new TreeMap<>();
for (String w : "to be or not to be".split(" ")) counts.merge(w, 1, Integer::sum);   // {be=2, not=1, or=1, to=2}

// 22 group into lists with computeIfAbsent (no stream)
byLen.computeIfAbsent(w.length(), k -> new ArrayList<>()).add(w);   // {2=[hi, yo], 5=[hello]}

// 23 nested grouping: department, then city
groupingBy(Employee::department, TreeMap::new, groupingBy(Employee::city, TreeMap::new, counting()))   // Sales -> {Berlin=1, London=2}

// 24 two results in one pass [12]
staff.stream().collect(Collectors.teeing(Collectors.counting(), Collectors.averagingDouble(Employee::salary),
        (n, avg) -> n + " people, average " + Math.round(avg)));   // 10 people, average 79300
```

---

## 16. SQL to Stream

| SQL | Stream |
|---|---|
| `WHERE age > 30` | `.filter(e -> e.age() > 30)` |
| `SELECT name` | `.map(Employee::name)` |
| `SELECT DISTINCT city` | `.map(Employee::city).distinct()` |
| `ORDER BY salary DESC` | `.sorted(Comparator.comparingDouble(Employee::salary).reversed())` |
| `ORDER BY dept, salary DESC` | `.sorted(comparing(Employee::department).thenComparing(Employee::salary, reverseOrder()))` |
| `LIMIT 5 OFFSET 10` | `.skip(10).limit(5)` |
| `COUNT(*)` | `.count()` |
| `SUM(salary)` / `AVG(salary)` | `.mapToDouble(Employee::salary).sum()` / `.average()` |
| `MAX(salary)` | `.max(Comparator.comparingDouble(Employee::salary))` |
| `GROUP BY dept` | `.collect(groupingBy(Employee::department))` |
| `GROUP BY dept` + `COUNT(*)` | `.collect(groupingBy(Employee::department, counting()))` |
| `GROUP BY dept` + `AVG(salary)` | `.collect(groupingBy(Employee::department, averagingDouble(Employee::salary)))` |
| `HAVING COUNT(*) > 2` | group first, then `.entrySet().stream().filter(e -> e.getValue() > 2)` |
| `EXISTS (...)` | `.anyMatch(..)` |
| `NOT EXISTS (...)` | `.noneMatch(..)` |
| `IN ('a','b')` | `.filter(e -> Set.of("a","b").contains(e.city()))` **[9]** |
| `UNION ALL` | `Stream.concat(a, b)` |
| `UNION` | `Stream.concat(a, b).distinct()` |
| `STRING_AGG(name, ',')` | `.map(Employee::name).collect(joining(","))` |
| `COALESCE(x, 'n/a')` | `Optional.ofNullable(x).orElse("n/a")` |

---

## 17. Which one do I use?

**Which operation?**

```
 Fewer items?                     -> filter, distinct, limit, skip, takeWhile
 Change each item?                -> map  (mapToInt for numbers)
 Each item is a list or array?    -> flatMap
 Different order?                 -> sorted
 Yes / no answer?                 -> anyMatch / allMatch / noneMatch
 One item?                        -> findFirst / min / max   (they return Optional)
 One number?                      -> count / sum / average / reduce
 Keep the items?                  -> collect(toList / toSet / toMap)
 Groups?                          -> collect(groupingBy / partitioningBy)
 Only do something?               -> forEach
```

**Which Optional method?**

```
 Need a value, have a fixed fallback        -> orElse
 Need a value, fallback is costly           -> orElseGet
 Need a value, missing is a bug             -> orElseThrow()
 Only act if present                        -> ifPresent / ifPresentOrElse
 Change the value                           -> map   (function returns an Optional -> flatMap)
```

**Which stream type?**

```
 Objects                      -> Stream<T>
 int / long / double numbers  -> IntStream / LongStream / DoubleStream   (sum, average, range, summaryStatistics)
```

**Which functional interface?**

```
 returns boolean  -> Predicate        returns a value  -> Function (or UnaryOperator, BinaryOperator)
 returns nothing  -> Consumer         takes nothing    -> Supplier
 two inputs       -> Bi...            primitives       -> Int... / ToInt... (no boxing)
```

---

## 18. Traps and fixes

| Trap | What happens | Fix |
|---|---|---|
| No terminal operation | nothing runs | end with `collect` / `forEach` / `count` |
| Using a stream twice | `IllegalStateException` | make a new stream, or a `Supplier<Stream<T>>` |
| Changing the source while streaming | `ConcurrentModificationException` | collect into a **new** list |
| `forEach(list::add)` / outside variables in lambdas | wrong results in parallel | `collect`, keep lambdas pure |
| `toMap` with a duplicate key | `IllegalStateException` | add a merge function `(a, b) -> a` |
| `toMap` with a `null` value | `NullPointerException` | filter or replace the null |
| `groupingBy` classifier returns `null` | `NullPointerException` | give `null` a real key, or filter it |
| `Stream.of(intArray)` | stream of ONE array | `Arrays.stream(intArray)` |
| `comparing(e -> ..).reversed()` | does not compile | `comparing(Employee::x).reversed()` or `(Employee e) -> ..` |
| `int n = stream.count()` | does not compile (`long`) | `long n = ..` |
| `Optional.get()` on empty | `NoSuchElementException` | `orElse` / `orElseGet` / `orElseThrow()` |
| `Optional.of(null)` | `NullPointerException` | `Optional.ofNullable` |
| `null` item in `map(String::toUpperCase)` | `NullPointerException` | `filter(Objects::nonNull)` first |
| Infinite stream with no `limit` | never ends | add `limit` (or `takeWhile` **[9]**) |
| `Files.lines` / `Files.walk` not closed | open file leak | `try (Stream<String> s = Files.lines(p)) { .. }` |
| Parallel + shared counter | lost updates | `count()`, `sum()`, `AtomicInteger` |
| Parallel `reduce` with `-` | different answers | use an associative operation |
| Checked exception in a lambda | does not compile | catch inside, or a small wrapper method |
| Find duplicates with `filter(n -> !seen.add(n))` | works, but unsafe in parallel | `groupingBy(identity(), counting())`, keep counts > 1 |
| `peek` + `count()` on a List | on Java 9+ `count()` skips the pipeline when the size is known, so `peek` may **not run** | never put logic in `peek` |
| `Stream.toList()` then `.add(..)` **[16]** | `UnsupportedOperationException` | `toCollection(ArrayList::new)` |
| `List.of(.., null)` **[9]** | `NullPointerException` | use `Arrays.asList` or remove the `null` |
| `MM` vs `mm` in a date pattern | month vs minute | `MM` = month, `mm` = minute |
| `date.plusDays(1);` result ignored | nothing changed | `date = date.plusDays(1);` |

---

## 19. Speed tips

1. **`filter` early**, `map` later, `sorted` last: fewer items reach the costly steps. (Example: `map` then `filter` over 1000 items did 1000 mappings; `filter` then `map` did 9.)
2. Numbers: `mapToInt(..).sum()` beats `Stream<Integer>` + `reduce` (no boxing).
3. `sorted`, `distinct`, `limit` and `skip` are **stateful** (they remember items): keep them after `filter`.
4. `anyMatch`, `allMatch`, `noneMatch`, `findFirst`, `limit` **stop early**. Use them instead of `filter(..).count() > 0`.
5. `Files.lines` reads **lazily** (line by line). `Files.readAllLines` loads the whole file.
6. One pass is better than two: `summaryStatistics()`, `summarizingInt`, `teeing` **[12]**.
7. Parallel only for big, CPU-heavy, independent work. **Measure** first.
8. For `Map` counting: `merge(k, 1, Integer::sum)` is simple; `groupingBy(.., counting())` fits inside a stream pipeline.
9. A simple `for` loop is fine (and sometimes clearer). Streams are a tool, not a rule.

---

## 20. The sample data

The `Employee` used in all examples (`lessons/common/Sample.java`):

| name | department | salary | age | city |
|---|---|---|---|---|
| Alice | Engineering | 120000 | 30 | London |
| Bob | Engineering | 95000 | 45 | Paris |
| Carol | Sales | 70000 | 28 | London |
| Dave | Sales | 65000 | 35 | Berlin |
| Eve | HR | 60000 | 40 | Paris |
| Frank | Engineering | 105000 | 38 | Berlin |
| Grace | HR | 58000 | 26 | London |
| Heidi | Marketing | 72000 | 33 | Paris |
| Ivan | Marketing | 68000 | 29 | Berlin |
| Judy | Sales | 80000 | 42 | London |

Totals: salary `793000.0`, average salary `79300.0`, average age `34.6`.

---

## 21. What's new after Java 8

Every row was checked on a real JDK. Details: [After Java 8](AFTER_JAVA8.md) | Easy to memorise: [Version Cheat Sheet](VERSION_CHEATSHEET.md).

| Task | Java 8 way | Newer way | Since |
|---|---|---|---|
| stream to list | `collect(Collectors.toList())` | `.toList()` (unmodifiable) | 16 |
| stop when a test fails | no direct way | `takeWhile(p)` | 9 |
| loop as a stream | `iterate(..).limit(n)` | `iterate(seed, hasNext, next)` | 9 |
| filter inside a group | filter first (empty groups vanish) | `Collectors.filtering(..)` | 9 |
| two results in one pass | two streams | `Collectors.teeing(..)` | 12 |
| read-only collector result | `collectingAndThen(toList(), unmodifiableList)` | `toUnmodifiableList()` | 10 |
| Optional if/else | `if (o.isPresent()) .. else ..` | `ifPresentOrElse` | 9 |
| Optionals in a stream | `.filter(Optional::isPresent).map(Optional::get)` | `.flatMap(Optional::stream)` | 9 |
| `get()` | `opt.get()` | `opt.orElseThrow()` | 10 |
| negate a method reference | `s -> !s.isBlank()` | `Predicate.not(String::isBlank)` | 11 |
| small immutable list | `Collections.unmodifiableList(Arrays.asList(..))` | `List.of(..)` | 9 |
| last element | `list.get(list.size() - 1)` | `list.getLast()` | 21 |
| long type names | `Map<String, List<Integer>> m = new HashMap<>();` | `var m = new HashMap<String, List<Integer>>();` | 10 |
| data class | class + getters + `equals` + `toString` | `record Point(int x, int y) {}` | 16 |
| type test + cast | `if (o instanceof String) { String s = (String) o; }` | `if (o instanceof String s)` | 16 |
| `switch` for types | `if` / `else if` chain | `switch (o) { case Integer i -> ..; case String s -> ..; }` | 21 |
| multi-line text | `"a\n" + "b\n"` | text block `"""` | 15 |
| file as text | `new String(Files.readAllBytes(p), UTF_8)` | `Files.readString(p)` | 11 |
| future with a timeout | not available | `orTimeout(..)` / `completeOnTimeout(..)` | 9 |
| close a pool | `shutdown()` + `awaitTermination(..)` | `try (var pool = ..) { }` | 19 |
| thousands of waiting tasks | thread pool | `Executors.newVirtualThreadPerTaskExecutor()` | 21 |

**Behaviour changes to know:** `peek` may not run with `count()` (9) | `flatMap` now short-circuits correctly (10) | UTF-8 is the default charset (18) | **Nashorn** removed (15) | Java EE modules such as `javax.xml.bind` removed (11).

---

## 22. Golden rules

1. No terminal operation -> nothing happens.
2. A stream is used **once**.
3. `filter` early.
4. Keep lambdas **pure**: no changes to outside variables.
5. Numbers? `IntStream` / `mapToInt`.
6. `Optional`: never `get()` blindly. Return it; do not store it.
7. `toMap`: always think about duplicate keys.
8. Need a certain Map or List type? Ask for it (`TreeMap::new`, `toCollection(ArrayList::new)`).
9. Parallel only after measuring, and never with shared variables.
10. Clear beats clever. A plain `for` loop is sometimes the best answer.

---

### How this sheet was verified

Every example, every input/output picture and every recipe above was compiled and run:

- **128 Java 8 examples**, compiled with `--release 8` (so no newer API can hide in them) and again on Java 21.
- **51 Java 9 to 21 examples**, compiled with `--release 21`.
- Run the proof yourself: `./tools/check-cheatsheet.sh` (needs JDK 21). The "since Java N" tags are proven by `./tools/check-api-since.sh`.

Official sources: [Stream package](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html) |
[Collectors](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html) |
[Optional](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html) |
[java.util.function](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/package-summary.html) |
[Comparator](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html) |
[java.time](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html) |
[CompletableFuture](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html) |
[Oracle Tutorial: Lambda Expressions](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html) |
[Oracle Tutorial: Streams](https://docs.oracle.com/javase/tutorial/collections/streams/index.html)
