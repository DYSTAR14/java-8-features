import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 06 - Streams: the Big Picture
 *
 * A Stream is NOT a data structure. It does not store data.
 * It is a PIPELINE that describes what to do with data:
 *
 *      SOURCE  ->  intermediate steps (0 or more)  ->  terminal step (exactly 1)
 *      list        filter, map, sorted ...             collect, forEach, count ...
 *
 * Think of a factory belt: items enter, get filtered and changed, and come out at the end.
 */
public class Lesson06_StreamsIntroduction {

    public static void main(String[] args) {
        step1_firstStream();
        step2_waysToCreateStreams();
        step3_nothingHappensWithoutTerminal();
        step4_streamCanBeUsedOnlyOnce();
        step5_sourceIsNotChanged();
        step6_streamVsLoop();
    }

    // ------------------------------------------------------------------
    static void step1_firstStream() {
        System.out.println("--- 1. Your first stream ---");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // "Give me the squares of the even numbers"
        List<Integer> result = numbers.stream()        // SOURCE
                .filter(n -> n % 2 == 0)               // intermediate: keep evens
                .map(n -> n * n)                       // intermediate: square them
                .collect(Collectors.toList());         // TERMINAL: gather into a List

        System.out.println(result);   // [4, 16, 36, 64, 100]
    }

    // ------------------------------------------------------------------
    static void step2_waysToCreateStreams() {
        System.out.println("\n--- 2. Ways to create a stream ---");

        // From a collection (most common)
        Stream<String> s1 = Arrays.asList("a", "b", "c").stream();

        // From an array
        Stream<String> s2 = Arrays.stream(new String[]{"x", "y", "z"});

        // From values
        Stream<Integer> s3 = Stream.of(1, 2, 3);

        // From a range of numbers (primitive stream, no boxing)
        IntStream s4 = IntStream.range(0, 5);            // 0,1,2,3,4  (5 is excluded)
        IntStream s5 = IntStream.rangeClosed(1, 5);      // 1,2,3,4,5  (5 is included)

        // INFINITE stream: starts at 1, keeps doubling. Must be cut with limit()!
        Stream<Integer> s6 = Stream.iterate(1, n -> n * 2).limit(6);

        // INFINITE stream from a Supplier
        Stream<String> s7 = Stream.generate(() -> "hi").limit(3);

        // From the characters of a String
        IntStream s8 = "abc".chars();

        // Empty stream
        Stream<String> s9 = Stream.empty();

        System.out.println("s1: " + s1.collect(Collectors.toList()));
        System.out.println("s2: " + s2.collect(Collectors.toList()));
        System.out.println("s3: " + s3.collect(Collectors.toList()));
        System.out.println("s4: " + Arrays.toString(s4.toArray()));
        System.out.println("s5: " + Arrays.toString(s5.toArray()));
        System.out.println("s6: " + s6.collect(Collectors.toList()));
        System.out.println("s7: " + s7.collect(Collectors.toList()));
        System.out.println("s8: " + Arrays.toString(s8.toArray()) + "  (character codes)");
        System.out.println("s9: " + s9.collect(Collectors.toList()));
        // Also: Files.lines(path) reads a file line by line as a stream,
        //       Pattern.splitAsStream(...), random.ints(), map.entrySet().stream() ...
    }

    // ------------------------------------------------------------------
    static void step3_nothingHappensWithoutTerminal() {
        System.out.println("\n--- 3. Streams are LAZY ---");

        // Intermediate operations only DESCRIBE the work. Nothing runs yet:
        Stream<Integer> pipeline = Stream.of(1, 2, 3)
                .peek(n -> System.out.println("  processing " + n))   // peek = look at each item (debug tool)
                .map(n -> n * 10);
        System.out.println("pipeline built. Did anything print? No!");

        // The terminal operation starts the whole machine:
        System.out.println("now calling collect...");
        List<Integer> out = pipeline.collect(Collectors.toList());
        System.out.println("result: " + out);
    }

    // ------------------------------------------------------------------
    static void step4_streamCanBeUsedOnlyOnce() {
        System.out.println("\n--- 4. A stream can be used only ONCE ---");
        Stream<String> stream = Stream.of("a", "b");
        stream.forEach(s -> System.out.print(s + " "));
        System.out.println();
        try {
            stream.forEach(System.out::println);       // second use -> error
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
        // Fix: create a NEW stream from the source each time you need one.
    }

    // ------------------------------------------------------------------
    static void step5_sourceIsNotChanged() {
        System.out.println("\n--- 5. A stream never changes its source ---");
        List<Integer> original = Arrays.asList(3, 1, 2);
        List<Integer> sorted = original.stream().sorted().collect(Collectors.toList());
        System.out.println("original: " + original);   // [3, 1, 2]  unchanged
        System.out.println("sorted:   " + sorted);     // [1, 2, 3]  new list
    }

    // ------------------------------------------------------------------
    static void step6_streamVsLoop() {
        System.out.println("\n--- 6. Loop vs stream (same job) ---");
        List<String> names = Arrays.asList("anna", "bob", "alex", "chris", "amy");

        // Old style: HOW to do it (step by step)
        StringBuilder loopResult = new StringBuilder();
        for (String n : names) {
            if (n.startsWith("a")) {
                loopResult.append(n.toUpperCase()).append(" ");
            }
        }

        // Stream style: WHAT we want (declarative)
        String streamResult = names.stream()
                .filter(n -> n.startsWith("a"))
                .map(String::toUpperCase)
                .collect(Collectors.joining(" "));

        System.out.println("loop:   " + loopResult.toString().trim());
        System.out.println("stream: " + streamResult);
    }
}
