import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LESSON 19 - Streams with Files and Text (real-world work)
 *
 * In real projects data usually comes from FILES, not from a hand-made list.
 * Java 8 added:
 *    Files.lines(path)        -> Stream of lines
 *    Files.walk(dir)          -> Stream of all files in a folder tree
 *    BufferedReader.lines()   -> Stream of lines from any reader
 *    Pattern.splitAsStream()  -> Stream of pieces of a text
 *    Pattern.asPredicate()    -> a regex as a Predicate
 *
 * IMPORTANT: streams that read files hold an open file. Use try-with-resources
 * so the file is closed, even when something goes wrong.
 *
 * JAVA 21 EDITION. The stream tools did not change after Java 8. What changed here:
 *   - Row is a record (Java 16)                      - Predicate.not + String.isBlank (Java 11)
 *   - Files.readString / writeString, String.lines(), Path.of (Java 11)
 *   - UTF-8 became the default charset in Java 18 (JEP 400). We still name the charset: it is a good habit.
 * The original Java 8 file is in lessons-java8/19-streams-with-files-and-text/.
 */
public class Lesson19_StreamsWithFilesAndText {

    /**
     * One row of our CSV file.
     * Java 21 edition: a record (Java 16). Java 8 way: a class with final fields and a constructor.
     * A record may still write its own methods: here a static parser and a short toString.
     */
    record Row(String name, String dept, double salary) {
        static Row parse(String line) {                 // "Alice,Engineering,120000"
            String[] p = line.split(",");
            return new Row(p[0].trim(), p[1].trim(), Double.parseDouble(p[2].trim()));
        }
        @Override public String toString() { return name + "/" + dept + "/" + (long) salary; }
    }

    public static void main(String[] args) throws IOException {
        // Build a small temporary folder with files, so this lesson works on any computer.
        Path dir = Files.createTempDirectory("java8-lesson19");
        Path csv = dir.resolve("staff.csv");
        Path text = dir.resolve("story.txt");

        Files.write(csv, List.of(
                "name,department,salary",          // header line
                "Alice,Engineering,120000",
                "Bob,Engineering,95000",
                "Carol,Sales,70000",
                "",                                // empty line (real files have these!)
                "Dave,Sales,65000",
                "Eve,HR,60000"), StandardCharsets.UTF_8);

        Files.write(text, List.of(
                "The cat sat on the mat.",
                "The dog sat on the log!",
                "A cat and a dog, friends."), StandardCharsets.UTF_8);

        try {
            readLines(csv);
            csvToObjects(csv);
            wordFrequency(text);
            walkFolder(dir);
            otherTextSources();
            writeResult(dir);
        } finally {
            // clean up (delete files first, then the folder)
            try (Stream<Path> all = Files.walk(dir)) {
                all.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
    }

    // ------------------------------------------------------------------
    static void readLines(Path csv) throws IOException {
        System.out.println("--- 1. Files.lines + try-with-resources ---");
        // The stream is closed automatically at the end of the try block.
        try (Stream<String> lines = Files.lines(csv, StandardCharsets.UTF_8)) {
            long dataRows = lines.skip(1)                       // skip header
                    .filter(Predicate.not(String::isBlank))     // ignore empty lines (Java 11: Predicate.not, isBlank)
                    .count();                                   // Java 8 way: .filter(l -> !l.trim().isEmpty())
            System.out.println("data rows = " + dataRows);      // 5
        }
        // Files.lines is LAZY: it reads the file line by line, so even a 10 GB file
        // does not need 10 GB of memory (unlike Files.readAllLines).
        // A read error inside the stream is thrown as UncheckedIOException.

        try (Stream<String> lines = Files.lines(csv)) {
            System.out.println("first line: " + lines.findFirst().orElse("(empty file)"));
        }
    }

    // ------------------------------------------------------------------
    static void csvToObjects(Path csv) throws IOException {
        System.out.println("\n--- 2. CSV -> objects -> report ---");
        try (Stream<String> lines = Files.lines(csv)) {
            List<Row> rows = lines.skip(1)
                    .map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .map(Row::parse)
                    .toList();
            System.out.println(rows);

            Map<String, Double> payroll = rows.stream().collect(
                    Collectors.groupingBy(r -> r.dept, TreeMap::new, Collectors.summingDouble(r -> r.salary)));
            System.out.println("payroll per department: " + payroll);
        }

        // Reading from a String (or any Reader) works the same way
        BufferedReader reader = new BufferedReader(new StringReader("one\ntwo\nthree"));
        System.out.println("BufferedReader.lines(): " + reader.lines().collect(Collectors.joining("|")));
    }

    // ------------------------------------------------------------------
    static void wordFrequency(Path text) throws IOException {
        System.out.println("\n--- 3. Word frequency of a file (classic task) ---");
        try (Stream<String> lines = Files.lines(text)) {
            Map<String, Long> freq = lines
                    .flatMap(line -> Arrays.stream(line.split("\\W+")))   // split on anything that is not a letter
                    .filter(w -> !w.isEmpty())
                    .map(String::toLowerCase)
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

            freq.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                            .thenComparing(Map.Entry.comparingByKey()))
                    .limit(4)
                    .forEach(e -> System.out.println("  " + e.getKey() + " = " + e.getValue()));
        }
    }

    // ------------------------------------------------------------------
    static void walkFolder(Path dir) throws IOException {
        System.out.println("\n--- 4. Files.walk: all files in a folder ---");
        try (Stream<Path> paths = Files.walk(dir)) {
            List<String> names = paths.filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
            System.out.println(names);   // [staff.csv, story.txt]
        }
        // Files.list(dir) = only the direct children (no sub-folders).
        // Always close these streams: they keep the folder open.
    }

    // ------------------------------------------------------------------
    static void otherTextSources() {
        System.out.println("\n--- 5. Other text helpers ---");

        // Pattern.splitAsStream: split with a regex, get a Stream
        System.out.println(Pattern.compile("\\s*,\\s*").splitAsStream("a , b,c ,d").toList());

        // Pattern.asPredicate: use a regex as a filter
        Pattern emailLike = Pattern.compile("^[\\w.]+@[\\w.]+\\.[a-z]{2,}$");
        List<String> valid = Stream.of("asha@mail.com", "not-an-email", "raj@site.org")
                .filter(emailLike.asPredicate())
                .toList();
        System.out.println("valid emails: " + valid);

        // Count the lines of a multi-line String
        String multi = "line 1\nline 2\nline 3";
        System.out.println("lines: " + multi.split("\n").length);

        // Characters -> Stream
        String sentence = "Hello World";
        long upper = sentence.chars().filter(Character::isUpperCase).count();
        System.out.println("uppercase letters: " + upper);

        // Example of the exception you get from a stream that fails while reading
        try {
            try (Stream<String> s = Files.lines(Path.of("this-file-does-not-exist.txt"))) {
                s.count();
            }
        } catch (IOException e) {
            System.out.println("missing file -> " + e.getClass().getSimpleName());
        } catch (UncheckedIOException e) {
            System.out.println("read failure -> UncheckedIOException");
        }
    }

    // ------------------------------------------------------------------
    static void writeResult(Path dir) throws IOException {
        System.out.println("\n--- 6. Writing a stream result to a file ---");
        Path out = dir.resolve("result.txt");
        List<String> upper = Stream.of("a", "b", "c").map(String::toUpperCase).toList();
        Files.write(out, upper, StandardCharsets.UTF_8);          // one line per item

        try (Stream<String> lines = Files.lines(out)) {
            System.out.println("file now contains: " + lines.collect(Collectors.joining(",")));
        }

        // Java 11: a whole file as ONE String. Java 8 way: new String(Files.readAllBytes(path), UTF_8)
        Path note = dir.resolve("note.txt");
        Files.writeString(note, "hello\nworld");                  // writes UTF-8 by default
        String content = Files.readString(note);
        System.out.println("readString:     " + content.replace('\n', '|'));
        // Java 11: String.lines() = a Stream of the lines of a String
        System.out.println("String.lines(): " + content.lines().toList());
    }
}
