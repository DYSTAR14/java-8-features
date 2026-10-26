# Java 8 Interview Questions and Short Answers

Short, clear answers. For each one, the lesson that explains it deeper is in brackets.
Tip: say the answer in your own words, then give a tiny code example.

## Lambdas, functional interfaces, method references

**1. What is a lambda expression?** A short, nameless function: `(a, b) -> a + b`. It implements a functional interface. [Lesson 01]

**2. What is a functional interface?** An interface with exactly **one abstract method**. It may also have default and static methods. `@FunctionalInterface` is optional but makes the compiler check it. [01, 02]

**3. Name the four main functional interfaces.** `Predicate<T>` (T -> boolean), `Function<T,R>` (T -> R), `Consumer<T>` (T -> void), `Supplier<T>` (() -> T). [02]

**4. `Function` vs `UnaryOperator`? `BiFunction` vs `BinaryOperator`?** `UnaryOperator<T>` is a `Function<T,T>`. `BinaryOperator<T>` is a `BiFunction<T,T,T>`. Same types in and out. [02]

**5. Lambda vs anonymous class?** Lambda: shorter, `this` means the outer class, no new `.class` file at compile time (uses `invokedynamic`), only for functional interfaces. Anonymous class: can implement any interface or class, has its own `this` and state. [01]

**6. Why must a variable used in a lambda be effectively final?** The lambda may run later or on another thread, so Java copies the value. Allowing changes would cause confusing bugs. [01]

**7. What are the four kinds of method references?** Static (`Integer::parseInt`), bound instance (`System.out::println`), unbound instance (`String::length`), constructor (`ArrayList::new`). [03]

**8. Why does `Comparator.comparing(e -> e.getX()).reversed()` not compile?** Java infers the lambda type from left to right and assumes `Object`. Fix: use a method reference or write the type `(Employee e) -> ...`. [20]

## Interfaces

**9. What are default methods and why were they added?** Methods with a body in an interface. They let Java add `stream()`, `forEach()`, `removeIf()` to old interfaces without breaking existing classes. [04]

**10. A class implements two interfaces that have the same default method. What happens?** Compile error until you override it. Inside you can choose with `InterfaceName.super.method()`. A method in a class always wins over a default. [04]

**11. Can an interface have static methods?** Yes (Java 8). Call them with the interface name. They are not inherited. [04]

## Optional

**12. What is `Optional` and why use it?** A container that holds one value or nothing. It makes "might be missing" visible in the return type and reduces `NullPointerException`s. [05]

**13. `orElse` vs `orElseGet`?** `orElse(x)` always creates `x`. `orElseGet(supplier)` runs the supplier only if empty. [05]

**14. `map` vs `flatMap` on `Optional`?** Use `map` when the function returns a plain value. Use `flatMap` when it returns an `Optional` (avoids `Optional<Optional<T>>`). [05]

**15. Should `Optional` be used as a field or a parameter?** No. Use it mainly as a method **return type**. [05]

## Streams: concepts

**16. What is a Stream? Is it a data structure?** No. It is a pipeline of operations over data from a source. It does not store data. [06]

**17. Stream vs Collection?** A collection stores data and you can read it many times. A stream computes data on demand (lazy), can be used once, and never changes its source. [06]

**18. Intermediate vs terminal operations?** Intermediate (`filter`, `map`, `sorted`) return a stream and are lazy. Terminal (`collect`, `forEach`, `count`) start the work and end the stream. [06, 07, 08]

**19. What does "lazy" mean for streams?** Intermediate operations do nothing until a terminal operation runs. Items also flow one by one through the whole pipeline, which allows short-circuiting. [06, 07]

**20. Can you reuse a stream?** No. A second terminal operation throws `IllegalStateException`. Create a new stream. [06, 14]

**21. What is a short-circuit operation?** One that can stop early: `limit`, `findFirst`, `findAny`, `anyMatch`, `allMatch`, `noneMatch`. [07, 08]

**22. Which stream operations are stateful?** `sorted`, `distinct`, `limit`, `skip` need to remember other items (sorted must see all of them). `filter` and `map` are stateless. [07]

## Streams: operations

**23. `map` vs `flatMap`?** `map`: one in, one out. `flatMap`: one in, many out, then flattened into one stream. Example: `Stream<List<X>>` -> `flatMap(List::stream)` -> `Stream<X>`. [07]

**24. `filter` vs `map`?** `filter` keeps or drops items (same type). `map` changes each item (possibly a new type). [07]

**25. `findFirst` vs `findAny`?** `findFirst` returns the first item (respects order). `findAny` returns any item, which can be faster in parallel. In sequential streams they usually give the same item. [08]

**26. `peek` vs `map` vs `forEach`?** `peek` looks at items and passes them on (intermediate, for debugging). `map` transforms. `forEach` is terminal and ends the stream. [07, 08]

**27. `reduce` - how does it work?** It combines all items into one value with an associative rule, starting from an identity value. `reduce(0, Integer::sum)`. [08]

**28. `sorted()` with and without arguments?** No argument: natural order (items must be `Comparable`). With a `Comparator`: your own order. The sort is stable for ordered streams. [07]

**29. `limit` vs `skip`?** `limit(n)` keeps the first n. `skip(n)` drops the first n. Together they give paging. [07]

**30. `Stream.of(array)` vs `Arrays.asList(array).stream()` vs `Arrays.stream(array)`?** For an object array, all give a stream of the elements. For a **primitive** array, `Stream.of(intArray)` gives a stream with ONE item (the array). Use `Arrays.stream(intArray)` to get an `IntStream`. [06, 09]

**31. `Stream.iterate` vs `Stream.generate`?** `iterate(seed, f)` uses the previous value to make the next. `generate(supplier)` makes each value independently. Both are infinite: use `limit`. [06, 12]

**32. Why use `IntStream` instead of `Stream<Integer>`?** No boxing (faster, less memory) and extra methods: `sum`, `average`, `range`, `summaryStatistics`. [09]

## Collectors

**33. `Collectors.toMap` problem with duplicate keys?** It throws `IllegalStateException`. Provide a merge function: `toMap(k, v, (a, b) -> a)`. [10]

**34. `groupingBy` vs `partitioningBy`?** `groupingBy` makes any number of groups from a key function. `partitioningBy` makes exactly two (true/false) from a predicate, and both keys always exist. [11]

**35. What is a downstream collector?** The collector given as the second argument of `groupingBy`; it processes the items inside each group (`counting()`, `summingInt()`, `mapping()` ...). [11]

**36. How do you find the highest-paid employee in each department?** `groupingBy(dept, collectingAndThen(maxBy(comparing(salary)), Optional::get))`. [11]

**37. How do you count occurrences of each word?** `groupingBy(Function.identity(), counting())`. [12, 19]

**38. How do you sort a `Map` by value?** Stream the `entrySet()` and use `sorted(Map.Entry.comparingByValue())`. [11, 12]

**39. `collect(Collectors.toList())` vs `.toList()`?** `Stream.toList()` is Java 16+, not Java 8. In Java 8 use `collect(toList())`. [10]

## Parallel streams and pitfalls

**40. How do you make a stream parallel? Is it always faster?** `parallelStream()` or `.parallel()`. Not always: it helps for big data with CPU-heavy work and splittable sources; for small data it is slower. Measure. [13]

**41. What thread pool do parallel streams use?** The shared `ForkJoinPool.commonPool()`. Blocking calls inside can slow down the whole application. [13]

**42. What can go wrong with parallel streams?** Shared mutable state (race conditions), non-associative `reduce`, blocking operations, order-dependent logic. [13]

**43. `forEach` vs `forEachOrdered`?** In parallel streams `forEach` has no order guarantee. `forEachOrdered` keeps encounter order (slower). [13]

**44. What does `unordered()` do?** Tells the stream you do not need encounter order, so parallel `distinct` and `limit` can be faster. [20]

**45. How do you handle checked exceptions in a stream lambda?** Catch inside the lambda, or use a small wrapper that converts to a runtime exception. [14, 20]

## Date/Time, concurrency, other

**46. Why was the new Date/Time API added?** `Date`/`Calendar` were mutable, not thread-safe, and confusing. `java.time` is immutable, thread-safe, and clear. [15]

**47. `LocalDate` vs `LocalDateTime` vs `ZonedDateTime` vs `Instant`?** Date only; date and time without zone; date and time with zone; a point on the UTC timeline. [15]

**48. `Period` vs `Duration`?** `Period`: date-based (years, months, days). `Duration`: time-based (hours, minutes, seconds, nanos). [15]

**49. `thenApply` vs `thenCompose` vs `thenCombine` in `CompletableFuture`?** `thenApply` = map. `thenCompose` = flatMap (the function returns another future). `thenCombine` = wait for two futures and merge. [16]

**50. How do you run many `CompletableFuture`s together?** Start them all, then `CompletableFuture.allOf(...)` (wait for all) or `anyOf(...)` (first one). [16]

**51. Name some other Java 8 features.** `Map.merge/computeIfAbsent/getOrDefault`, `String.join`, `Base64`, repeating annotations, `Arrays.parallelSort`, Nashorn, Metaspace replaces PermGen. [17]

**52. A popular trick finds duplicates with `filter(n -> !seen.add(n))` where `seen` is a `HashSet`. What is wrong with it?** It changes a variable outside the stream (a side effect). It is fine for a simple sequential stream, but it breaks in parallel streams (`HashSet` is not thread-safe) and it is not "pure". Safer: `groupingBy(Function.identity(), counting())` and keep entries with count > 1. [14, 18]

**53. What does `list.stream().peek(System.out::println).count()` print?** On Java 8 it prints every item. On Java 9+ it prints **nothing**, because `count()` can compute the size directly from the list and skips the pipeline. Lesson: never put real logic inside `peek`. [14]

## Coding questions to practise

All of these are solved in [Lesson 18](../lessons/18-stream-practice/README.md) (48 problems): odd/even split, character and element frequency, sort by length, three max/min, merge two arrays, reverse an array, reverse each word, second highest salary, group by, word frequency, first non-repeated character, palindrome, anagrams, primes, Fibonacci, FizzBuzz, flatten lists, remove duplicates, common elements, sum of digits. Age in years is in [Lesson 15](../lessons/15-date-time-api/README.md).
