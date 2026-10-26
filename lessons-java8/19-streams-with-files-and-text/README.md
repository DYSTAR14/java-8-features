# Lesson 19 - Streams with Files and Text

> **Level:** Intermediate  |  **Time:** 35 minutes  |  **Run it:** `.\run-java8.ps1 19` / `./run-java8.sh 19`

So far our data came from small lists. In real projects data usually lives in **files**, **logs**, **CSV**, **text**. Streams are great for this.

## The tools

| Code | Gives you |
|---|---|
| `Files.lines(path)` | `Stream<String>` of lines (lazy, reads one line at a time) |
| `Files.walk(dir)` | `Stream<Path>` of every file and folder below `dir` |
| `Files.list(dir)` | `Stream<Path>` of direct children only |
| `bufferedReader.lines()` | `Stream<String>` from any reader |
| `Pattern.compile(regex).splitAsStream(text)` | pieces of a text |
| `pattern.asPredicate()` | a regex used as a `Predicate<String>` |
| `Files.write(path, iterable)` | write a list of lines to a file |

## The most important rule: close the stream

A stream from `Files.lines` or `Files.walk` holds an **open file**. Always use **try-with-resources**:

```java
try (Stream<String> lines = Files.lines(path)) {
    long n = lines.filter(l -> l.contains("ERROR")).count();
}   // file is closed here, even if an exception happens
```

If you forget, the file stays open (a "resource leak").

## Why `Files.lines` and not `Files.readAllLines`?

| | `readAllLines` | `Files.lines` |
|---|---|---|
| Reads | the **whole file** into a `List` | **one line at a time** |
| Memory for a 10 GB file | 10 GB+ | tiny |
| Can stop early (`limit`, `findFirst`) | no (already read everything) | yes |

## Typical recipe: CSV to report

```java
try (Stream<String> lines = Files.lines(csv)) {
    List<Row> rows = lines.skip(1)               // header
            .map(String::trim)
            .filter(l -> !l.isEmpty())           // blank lines
            .map(Row::parse)                     // "a,b,c" -> object
            .collect(Collectors.toList());
}
```

Then use everything from lessons 10 and 11 (`groupingBy`, `summingDouble`...).

> For serious CSV (quotes, commas inside fields) use a CSV library. `split(",")` is only for simple files.

## Word frequency of a file

```java
lines.flatMap(l -> Arrays.stream(l.split("\\W+")))   // words
     .filter(w -> !w.isEmpty())
     .map(String::toLowerCase)
     .collect(groupingBy(w -> w, counting()));
```

## Errors

- Opening a missing file throws `NoSuchFileException` (an `IOException`) when you call `Files.lines`.
- A read error **while the stream runs** is thrown as `UncheckedIOException` (because lambdas cannot throw checked exceptions).
- Specify the character set: `Files.lines(path, StandardCharsets.UTF_8)`.

## Try it yourself

1. Write a log file with lines like `INFO ...`, `ERROR ...`. Count the `ERROR` lines.
2. Find the 3 **longest lines** of a file.
3. List all `.java` files in your project folder with `Files.walk` and `filter(p -> p.toString().endsWith(".java"))`.

## In newer Java (9 to 21)

Java 11: `Files.readString`, `Files.writeString`, `Path.of`, `String.lines()`. **Java 18 made UTF-8 the default charset** (JEP 400): classes such as `FileReader`, `FileWriter`, `InputStreamReader`, `PrintStream` and `Scanner` used the operating system's charset in Java 8, and use UTF-8 from Java 18. (`Files.lines(path)` already read UTF-8 in Java 8.) On a system that did not use UTF-8, old code can read or write different bytes after the upgrade. Always name the charset. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- `Files` API (lines, walk, list): <https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html>
- `BufferedReader.lines()`: <https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html#lines-->
- `Pattern` API: <https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html>
- try-with-resources (tutorial): <https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html>

**Previous:** [Lesson 18](../18-stream-practice/README.md) | **Next:** [Lesson 20 - Lambda Recipes and Gotchas](../20-lambda-recipes-and-gotchas/README.md)
