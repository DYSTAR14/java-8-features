# Modern 01 - Java 9, 10 and 11: new tools for your Java 8 code

> **Needs:** JDK 11 or newer (JDK 21 recommended)  |  **Run it:** `.\run-modern.ps1 1` / `./run-modern.sh 1`
> **Level:** Intermediate  |  **Time:** 30 minutes

Java 8 gave us lambdas and streams. Java 9, 10 and 11 added many **small, useful methods** around them. Everything here was **checked on a real JDK** (see `tools/check-api-since.sh`).

## Quick table

| Topic | Java 8 way | Newer way | Since |
|---|---|---|---|
| Optional: run one of two actions | `if (o.isPresent()) ... else ...` | `o.ifPresentOrElse(a, b)` | 9 |
| Optional: fall back to another Optional | `o.isPresent() ? o : other` | `o.or(() -> other)` | 9 |
| Optional: use in a stream | `.filter(Optional::isPresent).map(Optional::get)` | `.flatMap(Optional::stream)` | 9 |
| Optional: is it empty? | `!o.isPresent()` | `o.isEmpty()` | 11 |
| Optional: value or exception | `o.get()` | `o.orElseThrow()` | 10 |
| Small immutable list/set/map | `Collections.unmodifiableList(Arrays.asList(..))` | `List.of(..)`, `Set.of(..)`, `Map.of(..)` | 9 |
| Immutable copy | `Collections.unmodifiableList(new ArrayList<>(x))` | `List.copyOf(x)` | 10 |
| Stop a stream when a test fails | no direct way | `takeWhile(..)` / `dropWhile(..)` | 9 |
| Stream with a stop condition | `iterate(..).limit(..)` | `Stream.iterate(seed, hasNext, next)` | 9 |
| Stream of 0 or 1 item | `x == null ? Stream.empty() : Stream.of(x)` | `Stream.ofNullable(x)` | 9 |
| Filter inside a group | `filter` before `groupingBy` (loses groups) | `Collectors.filtering(..)` | 9 |
| Flat map inside a group | not possible | `Collectors.flatMapping(..)` | 9 |
| Read-only collector results | `collectingAndThen(toList(), Collections::unmodifiableList)` | `Collectors.toUnmodifiableList()` | 10 |
| Negate a method reference | `s -> !s.isBlank()` | `Predicate.not(String::isBlank)` | 11 |
| Local type inference | `Map<String, List<Integer>> m = ...` | `var m = ...` | 10 |
| Blank / repeat / strip text | `trim().isEmpty()` | `isBlank()`, `repeat(n)`, `strip()`, `lines()` | 11 |
| Read / write a whole file as text | `new String(Files.readAllBytes(p))` | `Files.readString(p)` / `Files.writeString(p, s)` | 11 |
| Future with a timeout | not available | `orTimeout`, `completeOnTimeout` | 9 |
| Private helper inside an interface | not allowed | `private` methods in interfaces | 9 |

## Four things to remember

1. **`List.of`, `Set.of`, `Map.of` are immutable and reject `null`.** `Arrays.asList` and `ArrayList` are different: they allow `null`, and `Arrays.asList` allows `set` but not `add`.
2. **`takeWhile` is not `filter`.** `filter` checks every item; `takeWhile` stops at the first failure.
3. **`var` is not dynamic typing.** The compiler still knows the exact type.
4. **`peek` + `count()` changed.** From Java 9, `count()` may skip the whole pipeline if it can know the size from the source. Never put real logic inside `peek`.

## Other things from these releases (not shown in code)

| Item | Version | Official source |
|---|---|---|
| Module system (JPMS) | 9 | JEP 261 |
| JShell (a REPL to test Java quickly) | 9 | JEP 222 |
| G1 becomes the default garbage collector | 9 | JEP 248 |
| CLDR locale data by default (date/number text can look different) | 9 | JEP 252 |
| Local-variable type inference (`var`) | 10 | JEP 286 |
| HTTP Client (standard, works with `CompletableFuture`) | 11 | JEP 321 |
| `var` in lambda parameters | 11 | JEP 323 |
| Nashorn deprecated | 11 | JEP 335 |
| Java EE and CORBA modules removed (e.g. `javax.xml.bind`) | 11 | JEP 320 |
| Run a single file: `java Hello.java` | 11 | JEP 330 |

## Try it yourself

1. Rewrite your Lesson 05 `Optional` examples with `ifPresentOrElse` and `orElseThrow()`.
2. Use `takeWhile` to read numbers from a sorted list until you pass 100.
3. Group the employees by department and keep only the ones above 90000 **inside** each group with `Collectors.filtering`. Which departments show an empty list?

## Official sources

- Java SE 21 API: `Optional`, `Stream`, `Collectors`, `List` (look for the "Since" notes): <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html>
- JDK 9 JEP list: <https://openjdk.org/projects/jdk9/>
- JDK 10 JEP list: <https://openjdk.org/projects/jdk/10/>
- JDK 11 JEP list: <https://openjdk.org/projects/jdk/11/>
- JEP 320 (Java EE and CORBA removed): <https://openjdk.org/jeps/320>

**Next:** [Modern 02 - Java 12 to 17](../02-java12-to-17/README.md)
