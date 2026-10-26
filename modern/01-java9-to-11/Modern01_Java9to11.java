import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * MODERN 01 - What Java 9, 10 and 11 added to your Java 8 toolbox
 *
 * This file needs JDK 21 (or any JDK 11+). It will NOT compile as Java 8.
 * Every item says in which Java version it appeared.
 * (Run with:  .\run-modern.ps1 1   or   ./run-modern.sh 1)
 */
public class Modern01_Java9to11 {

    // ---- Java 9: private methods in interfaces --------------------------------
    interface Shape {
        double area();

        default String describe() {
            return "area = " + format(area());      // default method shares the private helper
        }

        private String format(double d) {            // Java 9: private interface method
            return String.format("%.1f", d);
        }
    }

    public static void main(String[] args) throws Exception {
        optionalAdditions();
        collectionFactories();
        streamAdditions();
        collectorAdditions();
        predicateNotAndVar();
        stringAndFileHelpers();
        completableFutureTimeouts();
        smallLanguageChanges();
    }

    // ------------------------------------------------------------------
    static void optionalAdditions() {
        System.out.println("--- Optional (Java 9, 10, 11) ---");
        Optional<String> some = Optional.of("data");
        Optional<String> none = Optional.empty();

        // 9: ifPresentOrElse - Java 8 had only ifPresent
        some.ifPresentOrElse(v -> System.out.println("have " + v), () -> System.out.println("nothing"));
        none.ifPresentOrElse(v -> System.out.println("have " + v), () -> System.out.println("nothing"));

        // 9: or - fall back to ANOTHER Optional (orElse gives a plain value)
        System.out.println("or: " + none.or(() -> Optional.of("backup")).get());

        // 9: stream - 0 or 1 element. Great for dropping empty Optionals:
        List<String> present = Stream.of(some, none, Optional.of("more"))
                .flatMap(Optional::stream)            // Java 8 needed .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
        System.out.println("present values: " + present);

        // 11: isEmpty - the opposite of isPresent
        System.out.println("none.isEmpty() = " + none.isEmpty());

        // 10: orElseThrow() with no argument - the clear replacement for get()
        // (the Java 21 Javadoc says: "The preferred alternative to this method is orElseThrow()")
        System.out.println("orElseThrow(): " + some.orElseThrow());
    }

    // ------------------------------------------------------------------
    static void collectionFactories() {
        System.out.println("\n--- List.of / Set.of / Map.of (Java 9) and copyOf (Java 10) ---");
        // Java 8: Collections.unmodifiableList(Arrays.asList(1, 2, 3))
        List<Integer> list = List.of(1, 2, 3);
        Set<String> set = Set.of("a", "b");
        Map<String, Integer> map = Map.of("one", 1, "two", 2);
        Map<String, Integer> many = Map.ofEntries(Map.entry("x", 10), Map.entry("y", 20));

        System.out.println(list + " " + new TreeSet<>(set) + " " + new TreeMap<>(map) + " " + new TreeMap<>(many));

        // These collections are IMMUTABLE and do NOT allow null.
        try {
            list.add(4);
        } catch (UnsupportedOperationException e) {
            System.out.println("List.of(...).add -> UnsupportedOperationException (immutable)");
        }
        try {
            List.of(1, null);
        } catch (NullPointerException e) {
            System.out.println("List.of(1, null) -> NullPointerException (no nulls allowed)");
        }
        // Set.of / Map.of iteration order is NOT defined: never depend on it.

        // 10: copyOf = immutable copy (and returns the same object if it is already immutable)
        List<Integer> editable = new java.util.ArrayList<>(Arrays.asList(5, 6));
        List<Integer> frozen = List.copyOf(editable);
        editable.add(7);
        System.out.println("editable=" + editable + " frozen=" + frozen);
    }

    // ------------------------------------------------------------------
    static void streamAdditions() {
        System.out.println("\n--- Stream additions (Java 9) ---");
        List<Integer> sorted = List.of(1, 2, 3, 10, 11, 4);

        // takeWhile: take items UNTIL the test fails (Java 8 had only limit/filter)
        System.out.println("takeWhile(<5): " + sorted.stream().takeWhile(n -> n < 5).collect(Collectors.toList())); // [1, 2, 3]
        // dropWhile: skip items WHILE the test is true
        System.out.println("dropWhile(<5): " + sorted.stream().dropWhile(n -> n < 5).collect(Collectors.toList())); // [10, 11, 4]
        // Compare with filter: filter(<5) would give [1, 2, 3, 4] (it never stops)

        // iterate with a stop condition = a for-loop as a stream
        System.out.println("iterate: " + Stream.iterate(1, n -> n < 100, n -> n * 3).collect(Collectors.toList()));

        // ofNullable: a stream of 0 or 1 item (null-safe)
        String maybeNull = null;
        System.out.println("ofNullable(null).count() = " + Stream.ofNullable(maybeNull).count());
        System.out.println("IntStream.takeWhile: " + IntStream.range(0, 100).takeWhile(n -> n * n < 50).boxed().collect(Collectors.toList()));

        // Behaviour change: count() may skip the pipeline when the size is known (Stream.count Javadoc)
        long[] calls = {0};
        long size = List.of(1, 2, 3).stream().peek(n -> calls[0]++).count();
        System.out.println("count=" + size + " but peek ran " + calls[0] + " times (it is skipped!)");
    }

    // ------------------------------------------------------------------
    static void collectorAdditions() {
        System.out.println("\n--- Collectors additions (Java 9 and 10) ---");
        List<Employee> staff = Sample.employees();

        // Java 8: filter BEFORE groupingBy removes whole groups.
        Map<String, List<String>> filterFirst = staff.stream()
                .filter(e -> e.salary() > 100000)
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.mapping(Employee::name, Collectors.toList())));
        // Java 9: Collectors.filtering filters INSIDE each group, so empty groups stay visible.
        Map<String, List<String>> filterInside = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.filtering(e -> e.salary() > 100000,
                                Collectors.mapping(Employee::name, Collectors.toList()))));
        System.out.println("filter before: " + filterFirst);
        System.out.println("filtering():   " + filterInside);

        // Java 9: flatMapping = flatMap inside a collector
        Map<String, Set<String>> lettersByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.flatMapping(e -> e.city().chars().limit(1).mapToObj(c -> String.valueOf((char) c)),
                                Collectors.toCollection(TreeSet::new))));
        System.out.println("first letters of cities per dept: " + lettersByDept);

        // Java 10: toUnmodifiableList / Set / Map. They reject nulls, unlike toList().
        List<String> names = staff.stream().map(Employee::name).limit(3).collect(Collectors.toUnmodifiableList());
        System.out.println("unmodifiable: " + names);
    }

    // ------------------------------------------------------------------
    static void predicateNotAndVar() {
        System.out.println("\n--- Predicate.not (Java 11) and var (Java 10, 11) ---");
        List<String> lines = Arrays.asList("a", "", "  ", "b");

        // Java 8: .filter(s -> !s.trim().isEmpty())   or a lambda with "!"
        // Java 11: Predicate.not works with method references
        System.out.println(lines.stream().filter(Predicate.not(String::isBlank)).collect(Collectors.toList()));

        // Java 10: var = the compiler works out the type (local variables only)
        var numbers = List.of(3, 1, 2);                         // type is List<Integer>
        var total = numbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("var total = " + total);
        // var is NOT dynamic typing: the type is fixed at compile time. Not allowed for fields or parameters.

        // Java 11: var in lambda parameters (lets you add annotations like @Nullable to them)
        BiFunction<Integer, Integer, Integer> add = (var a, var b) -> a + b;
        System.out.println("add = " + add.apply(2, 3));
    }

    // ------------------------------------------------------------------
    static void stringAndFileHelpers() throws Exception {
        System.out.println("\n--- String and file helpers (Java 11) ---");
        System.out.println("isBlank:  " + "   ".isBlank());                       // true (Java 8 only had isEmpty)
        System.out.println("strip:    [" + "  hi  ".strip() + "]");               // like trim, but Unicode-aware
        System.out.println("repeat:   " + "ab".repeat(3));                        // ababab
        System.out.println("lines:    " + "one\ntwo\nthree".lines().count());     // 3  (a Stream<String>)

        Path file = Files.createTempFile("modern01", ".txt");
        try {
            Files.writeString(file, "first\nsecond");                            // 11: write a String
            System.out.println("readString: " + Files.readString(file).replace('\n', '|'));
        } finally {
            Files.deleteIfExists(file);
        }

        // 9: Objects.requireNonNullElse - a null-safe default
        String name = null;
        System.out.println("requireNonNullElse: " + Objects.requireNonNullElse(name, "anonymous"));
    }

    // ------------------------------------------------------------------
    static void completableFutureTimeouts() {
        System.out.println("\n--- CompletableFuture timeouts (Java 9) ---");
        // Java 8 had NO timeout support. Java 9 added:
        String fallback = new CompletableFuture<String>()                       // a future that never completes
                .completeOnTimeout("default value", 100, TimeUnit.MILLISECONDS)
                .join();
        System.out.println("completeOnTimeout -> " + fallback);

        try {
            new CompletableFuture<String>().orTimeout(100, TimeUnit.MILLISECONDS).join();
        } catch (CompletionException e) {
            System.out.println("orTimeout -> " + (e.getCause() instanceof TimeoutException ? "TimeoutException" : e.getCause()));
        }

        try {
            CompletableFuture.failedFuture(new IllegalStateException("broken")).join();
        } catch (CompletionException e) {
            System.out.println("failedFuture -> " + e.getCause().getMessage());
        }
    }

    // ------------------------------------------------------------------
    static void smallLanguageChanges() {
        System.out.println("\n--- Small language changes (Java 9) ---");
        Shape square = () -> 4.0 * 4.0;                                         // a lambda implementing our interface
        System.out.println("private interface method used by default method: " + square.describe());

        // Diamond <> now works with anonymous classes
        Comparator<String> byLength = new Comparator<>() {
            @Override public int compare(String a, String b) { return Integer.compare(a.length(), b.length()); }
        };
        System.out.println("anonymous class with <>: " + byLength.compare("aa", "b"));

        // try-with-resources can use an existing effectively-final variable
        java.io.StringReader reader = new java.io.StringReader("text");
        try (reader) {
            System.out.println("read char: " + (char) reader.read());
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
