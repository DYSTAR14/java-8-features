import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 12 - Advanced Stream Techniques
 *
 *  1. flatMap for nested data and "pairs" (replaces nested loops)
 *  2. Stream.iterate / generate for sequences (Fibonacci, random data)
 *  3. reduce with a different result type
 *  4. Stream.concat
 *  5. Writing your own Collector
 *  6. Word frequency (the classic interview question)
 *  7. Comparator chains with nulls
 *  8. Stream from an Iterator / Map
 */
public class Lesson12_AdvancedStreamTechniques {

    public static void main(String[] args) {
        flatMapTricks();
        sequences();
        reduceWithDifferentType();
        concatenate();
        customCollector();
        wordFrequency();
        comparatorChains();
        otherSources();
    }

    // ------------------------------------------------------------------
    static void flatMapTricks() {
        System.out.println("--- 1. flatMap: pairs without nested loops ---");

        // Old way:  for (i in 1..3) for (j in 1..3) if (i < j) ...
        List<String> pairs = IntStream.rangeClosed(1, 3).boxed()
                .flatMap(i -> IntStream.rangeClosed(1, 3)
                        .filter(j -> i < j)
                        .mapToObj(j -> "(" + i + "," + j + ")"))
                .collect(Collectors.toList());
        System.out.println(pairs);   // [(1,2), (1,3), (2,3)]

        // All distinct letters of several words
        String letters = Stream.of("hello", "world")
                .flatMap(w -> w.chars().mapToObj(c -> String.valueOf((char) c)))
                .distinct()
                .sorted()
                .collect(Collectors.joining());
        System.out.println(letters);  // dehlorw

        // Orders -> items -> total (nested data)
        Map<String, List<Integer>> orders = new LinkedHashMap<>();
        orders.put("order1", Arrays.asList(10, 20));
        orders.put("order2", Arrays.asList(5));
        orders.put("order3", Arrays.asList(100, 1, 2));
        int grandTotal = orders.values().stream().flatMap(List::stream).mapToInt(Integer::intValue).sum();
        System.out.println("grand total = " + grandTotal);  // 138
    }

    // ------------------------------------------------------------------
    static void sequences() {
        System.out.println("\n--- 2. Stream.iterate and Stream.generate ---");

        // Powers of 3
        System.out.println(Stream.iterate(1, n -> n * 3).limit(6).collect(Collectors.toList()));

        // Fibonacci: carry TWO numbers as an array {a, b} -> {b, a+b}
        List<Long> fib = Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]})
                .limit(10)
                .map(f -> f[0])
                .collect(Collectors.toList());
        System.out.println("fibonacci: " + fib);   // [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]

        // Java 8 has no takeWhile (that came in Java 9). Use limit() to stop an infinite stream.
        // generate: values from a Supplier
        Random random = new Random(42);   // fixed seed -> same numbers each run
        System.out.println(Stream.generate(() -> random.nextInt(100)).limit(5).collect(Collectors.toList()));

        // Even numbers: iterate with a step
        System.out.println(IntStream.iterate(0, n -> n + 2).limit(5).boxed().collect(Collectors.toList()));
    }

    // ------------------------------------------------------------------
    static void reduceWithDifferentType() {
        System.out.println("\n--- 3. reduce(identity, accumulator, combiner) ---");
        // Items are Strings, result is an Integer (total length).
        //   identity    : starting value 0
        //   accumulator : (partialResult, nextItem) -> newPartialResult
        //   combiner    : (partial1, partial2) -> merged. Used in PARALLEL streams to join pieces.
        int totalLength = Stream.of("java", "stream", "api")
                .reduce(0, (acc, s) -> acc + s.length(), (a, b) -> a + b);
        System.out.println("total length = " + totalLength);   // 13

        // Often mapToInt(...).sum() is simpler. Use 3-arg reduce only when you really need it.
        System.out.println("same thing    = " + Stream.of("java", "stream", "api").mapToInt(String::length).sum());
    }

    // ------------------------------------------------------------------
    static void concatenate() {
        System.out.println("\n--- 4. Stream.concat ---");
        Stream<String> a = Stream.of("a", "b");
        Stream<String> b = Stream.of("c", "d");
        System.out.println(Stream.concat(a, b).collect(Collectors.toList()));
        // For 3 or more streams use flatMap: Stream.of(s1, s2, s3).flatMap(Function.identity())
    }

    // ------------------------------------------------------------------
    static void customCollector() {
        System.out.println("\n--- 5. Your own Collector ---");
        // Collector.of(supplier, accumulator, combiner, finisher)
        //   supplier    : create an empty "bucket"
        //   accumulator : put ONE item into the bucket
        //   combiner    : merge two buckets (for parallel use)
        //   finisher    : turn the bucket into the final result
        Collector<String, StringBuilder, String> upperConcat = Collector.of(
                StringBuilder::new,                                  // supplier
                (sb, s) -> sb.append(s.toUpperCase()).append(';'),   // accumulator
                StringBuilder::append,                               // combiner
                StringBuilder::toString);                            // finisher

        System.out.println(Stream.of("x", "y", "z").collect(upperConcat));   // X;Y;Z;

        // Another one: collect into an unmodifiable List
        Collector<Integer, List<Integer>, List<Integer>> frozen = Collector.of(
                ArrayList::new, List::add,
                (l1, l2) -> { l1.addAll(l2); return l1; },
                Collections::unmodifiableList);
        System.out.println(Stream.of(3, 1, 2).collect(frozen));
    }

    // ------------------------------------------------------------------
    static void wordFrequency() {
        System.out.println("\n--- 6. Word frequency (top 3) ---");
        String text = "the quick brown fox jumps over the lazy dog the fox";

        Map<String, Long> freq = Arrays.stream(text.split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        freq.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(3)
                .forEach(e -> System.out.println(e.getKey() + " = " + e.getValue()));

        // Character frequency, same idea
        Map<Character, Long> chars = "banana".chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println(chars);

        // First non-repeating character
        String firstUnique = "swiss".chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream().filter(e -> e.getValue() == 1)
                .map(e -> String.valueOf(e.getKey())).findFirst().orElse("none");
        System.out.println("first unique in 'swiss' = " + firstUnique);   // w
    }

    // ------------------------------------------------------------------
    static void comparatorChains() {
        System.out.println("\n--- 7. Comparator chains and nulls ---");
        List<String> names = Arrays.asList("bob", null, "alice", "Carl", null);

        // Put nulls last, compare ignoring case
        List<String> sorted = names.stream()
                .sorted(Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        System.out.println(sorted);   // [alice, bob, Carl, null, null]

        // Sort by length, then alphabetically
        List<String> words = Arrays.asList("pear", "fig", "apple", "kiwi", "date");
        System.out.println(words.stream()
                .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toList()));   // [fig, date, kiwi, pear, apple]
    }

    // ------------------------------------------------------------------
    static void otherSources() {
        System.out.println("\n--- 8. Streams from other sources ---");

        // From a Map
        Map<String, Integer> stock = new LinkedHashMap<>();
        stock.put("apple", 5);
        stock.put("pear", 0);
        stock.put("fig", 12);
        System.out.println("in stock: " + stock.entrySet().stream()
                .filter(e -> e.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toList()));

        // From an Iterator (via Spliterators)
        java.util.Iterator<String> it = Arrays.asList("p", "q", "r").iterator();
        Stream<String> fromIterator = java.util.stream.StreamSupport.stream(
                java.util.Spliterators.spliteratorUnknownSize(it, java.util.Spliterator.ORDERED), false);
        System.out.println(fromIterator.collect(Collectors.toList()));

        // From a regex split
        System.out.println(java.util.regex.Pattern.compile(",").splitAsStream("1,2,3")
                .map(Integer::parseInt).collect(Collectors.toList()));

        // Files.lines(Paths.get("file.txt")) -> Stream<String> of lines. Always close it:
        //   try (Stream<String> lines = Files.lines(path)) { ... }
    }
}
