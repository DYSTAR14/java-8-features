import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 14 - Pitfalls and Best Practices
 *
 * Each mistake below is common. We show the problem, then the fix.
 */
public class Lesson14_PitfallsAndBestPractices {

    public static void main(String[] args) {
        pitfall1_noTerminalOperation();
        pitfall2_reuseStream();
        pitfall3_modifyingTheSource();
        pitfall4_sideEffects();
        pitfall5_infiniteStream();
        pitfall6_orderOfOperations();
        pitfall7_checkedExceptions();
        pitfall8_nullsAndOptionalGet();
        pitfall9_toMapDuplicates();
        pitfall10_boxing();
        pitfall11_peekIsNotForLogic();
        pitfall12_collectionsToListIsMutable();
        checklist();
    }

    static void pitfall1_noTerminalOperation() {
        System.out.println("--- 1. No terminal operation = nothing happens ---");
        Stream.of("a", "b").map(s -> { System.out.println("never printed"); return s; });
        // Fix: end with collect / forEach / count ...
        Stream.of("a", "b").map(String::toUpperCase).forEach(s -> System.out.println("printed " + s));
    }

    static void pitfall2_reuseStream() {
        System.out.println("\n--- 2. Reusing a stream ---");
        Stream<Integer> s = Stream.of(1, 2, 3);
        s.count();
        try {
            s.count();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
        // Fix: a Supplier that builds a fresh stream each time
        java.util.function.Supplier<Stream<Integer>> fresh = () -> Stream.of(1, 2, 3);
        System.out.println("count=" + fresh.get().count() + " sum=" + fresh.get().mapToInt(i -> i).sum());
    }

    static void pitfall3_modifyingTheSource() {
        System.out.println("\n--- 3. Changing the source while streaming ---");
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
        try {
            list.stream().forEach(n -> list.add(n + 10));   // changes the list while reading it
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("Error: ConcurrentModificationException");
        }
        // Fix: build a NEW list from the stream, do not touch the old one
        List<Integer> source = Arrays.asList(1, 2, 3);
        List<Integer> plus10 = source.stream().map(n -> n + 10).collect(Collectors.toList());
        System.out.println(plus10);
    }

    static void pitfall4_sideEffects() {
        System.out.println("\n--- 4. Side effects inside lambdas ---");
        // BAD: using the stream to fill another list (breaks in parallel, hard to read)
        List<Integer> bad = new ArrayList<>();
        Stream.of(1, 2, 3).map(n -> n * 2).forEach(bad::add);

        // GOOD: let collect() do the job
        List<Integer> good = Stream.of(1, 2, 3).map(n -> n * 2).collect(Collectors.toList());
        System.out.println("bad=" + bad + " good=" + good + "  (same here, but only 'good' is safe in parallel)");
        // Rule: lambdas in map/filter should be PURE - same input, same output, change nothing outside.
    }

    static void pitfall5_infiniteStream() {
        System.out.println("\n--- 5. Infinite streams need limit() ---");
        // Stream.iterate(1, n -> n + 1).forEach(System.out::println);   // never ends!
        System.out.println(Stream.iterate(1, n -> n + 1).limit(5).collect(Collectors.toList()));
        // Also dangerous: sorted() or collect() on an infinite stream (never finishes / out of memory).
        // Also: limit() AFTER distinct() on a stream with few unique values can hang forever.
    }

    static void pitfall6_orderOfOperations() {
        System.out.println("\n--- 6. Order of operations affects speed ---");
        int[] calls = {0, 0};
        List<Integer> data = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());

        data.stream().map(n -> { calls[0]++; return n * 2; }).filter(n -> n < 20).count();   // map FIRST
        data.stream().filter(n -> n * 2 < 20).map(n -> { calls[1]++; return n * 2; }).count();   // filter FIRST
        System.out.println("map-then-filter did " + calls[0] + " mappings; filter-then-map did " + calls[1]);
        // Rule: filter as early as possible.
    }

    static void pitfall7_checkedExceptions() {
        System.out.println("\n--- 7. Checked exceptions inside lambdas ---");
        // Lambdas for Function/Predicate cannot throw checked exceptions directly.
        // Fix: write a tiny helper that wraps the checked exception.
        List<String> result = Stream.of("1", "2", "x").map(s -> {
            try {
                return String.valueOf(parseOrThrow(s));
            } catch (Exception e) {
                return "invalid(" + s + ")";
            }
        }).collect(Collectors.toList());
        System.out.println(result);
    }

    static int parseOrThrow(String s) throws Exception {   // pretend this is a method with a checked exception
        if (!s.matches("\\d+")) throw new Exception("bad number");
        return Integer.parseInt(s);
    }

    static void pitfall8_nullsAndOptionalGet() {
        System.out.println("\n--- 8. Nulls and Optional.get() ---");
        List<String> withNulls = Arrays.asList("a", null, "b");
        try {
            withNulls.stream().map(String::toUpperCase).collect(Collectors.toList());
        } catch (NullPointerException e) {
            System.out.println("Error: NullPointerException from the null element");
        }
        // Fix: filter nulls out first
        System.out.println(withNulls.stream().filter(java.util.Objects::nonNull).map(String::toUpperCase)
                .collect(Collectors.toList()));

        Optional<String> none = Stream.<String>empty().findFirst();
        try {
            none.get();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("Error: NoSuchElementException from get() on empty Optional");
        }
        System.out.println("Fix: " + none.orElse("default"));
    }

    static void pitfall9_toMapDuplicates() {
        System.out.println("\n--- 9. toMap and duplicate keys ---");
        try {
            Stream.of("apple", "avocado").collect(Collectors.toMap(s -> s.charAt(0), Function.identity()));
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
        Map<Character, String> ok = Stream.of("apple", "avocado")
                .collect(Collectors.toMap(s -> s.charAt(0), Function.identity(), (first, second) -> first + "+" + second));
        System.out.println(ok);
        // Also: toMap throws NullPointerException if a VALUE is null.
    }

    static void pitfall10_boxing() {
        System.out.println("\n--- 10. Boxing cost ---");
        // Slower: Stream<Integer> boxes every number
        int a = Stream.of(1, 2, 3).reduce(0, Integer::sum);
        // Faster: IntStream
        int b = IntStream.of(1, 2, 3).sum();
        System.out.println(a + " == " + b + "   -> prefer mapToInt / IntStream for numbers");
    }

    static void pitfall11_peekIsNotForLogic() {
        System.out.println("\n--- 11. peek is for debugging only ---");
        // Java may skip peek if it can compute the result without running the pipeline.
        long count = Stream.of(1, 2, 3).peek(n -> System.out.println("peek " + n)).map(n -> n * 2).count();
        System.out.println("count = " + count + "  (Java 9+ skips 'peek' here because size is known; Java 8 runs it)");
    }

    static void pitfall12_collectionsToListIsMutable() {
        System.out.println("\n--- 12. Collectors.toList() gives no promises ---");
        // The docs do not say which List type or if it can be modified.
        // If you need a certain type, ask for it:
        ArrayList<Integer> exact = Stream.of(3, 1, 2).collect(Collectors.toCollection(ArrayList::new));
        System.out.println(exact);
        // Need read-only? collectingAndThen(toList(), Collections::unmodifiableList)  (see Lesson 10)
    }

    static void checklist() {
        System.out.println("\n=== BEST PRACTICES CHECKLIST ===");
        String[] rules = {
                "Always end a pipeline with a terminal operation.",
                "Do not reuse a stream. Create a new one.",
                "Do not change the source while streaming.",
                "Keep lambdas pure (no changes to outside variables).",
                "filter early, map later, sort last.",
                "Use IntStream / mapToInt for numbers.",
                "Prefer method references when they are clearer.",
                "Long lambdas? Move the code into a named method.",
                "Use limit() with infinite streams.",
                "Use parallel streams only after measuring.",
                "Use Optional's orElse/orElseGet/orElseThrow, not get().",
                "Do not use a stream when a simple for-loop is clearer."
        };
        for (String r : rules) System.out.println(" * " + r);
    }
}
