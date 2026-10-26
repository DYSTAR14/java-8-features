import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.LongAdder;
import java.nio.charset.StandardCharsets;

/**
 * LESSON 17 - More Java 8 Features (smaller but very useful)
 *
 *  1. New Map methods
 *  2. New List / Collection methods
 *  3. Comparator improvements
 *  4. String.join and StringJoiner
 *  5. Base64
 *  6. Objects and number helpers
 *  7. Arrays: parallelSort, setAll, parallelPrefix
 *  8. Repeating annotations
 *  9. LongAdder / ThreadLocal.withInitial
 */
public class Lesson17_MoreJava8Features {

    public static void main(String[] args) {
        mapMethods();
        listMethods();
        comparators();
        joiningStrings();
        base64();
        helpers();
        arrays();
        repeatingAnnotations();
        concurrencyExtras();
        notes();
    }

    // ------------------------------------------------------------------
    static void mapMethods() {
        System.out.println("--- 1. New Map methods ---");
        Map<String, Integer> stock = new HashMap<>();

        stock.put("apple", 5);
        System.out.println("getOrDefault: " + stock.getOrDefault("pear", 0));        // 0, no null check needed
        stock.putIfAbsent("pear", 3);                                                  // only if key is missing
        stock.putIfAbsent("apple", 99);                                                // ignored
        System.out.println(new java.util.TreeMap<>(stock));                            // {apple=5, pear=3}

        // computeIfAbsent: create value if missing. PERFECT for "group into lists"
        Map<Integer, List<String>> byLength = new HashMap<>();
        for (String w : Arrays.asList("hi", "yo", "hello", "world", "a")) {
            byLength.computeIfAbsent(w.length(), k -> new ArrayList<>()).add(w);
        }
        System.out.println("grouped by length: " + byLength);

        // merge: add new value or combine with old. PERFECT for counting
        Map<String, Integer> counts = new HashMap<>();
        for (String w : "to be or not to be".split(" ")) {
            counts.merge(w, 1, Integer::sum);
        }
        System.out.println("word counts: " + new java.util.TreeMap<>(counts));

        // compute / computeIfPresent: change a value
        stock.computeIfPresent("apple", (k, v) -> v + 10);
        stock.compute("kiwi", (k, v) -> v == null ? 1 : v + 1);
        System.out.println("after compute: " + new java.util.TreeMap<>(stock));

        // replaceAll and forEach
        stock.replaceAll((k, v) -> v * 2);
        new java.util.TreeMap<>(stock).forEach((k, v) -> System.out.println("  " + k + " -> " + v));

        // remove(key, value): remove only if the value matches
        System.out.println("removed? " + stock.remove("kiwi", 999) + " / " + stock.remove("kiwi", 2));
    }

    // ------------------------------------------------------------------
    static void listMethods() {
        System.out.println("\n--- 2. New List / Collection methods ---");
        List<Integer> list = new ArrayList<>(Arrays.asList(5, 8, 3, 12, 1));

        list.removeIf(n -> n > 10);          // remove by condition
        System.out.println("removeIf:   " + list);

        list.replaceAll(n -> n * 10);        // change every item
        System.out.println("replaceAll: " + list);

        list.sort(Comparator.reverseOrder());
        System.out.println("sort:       " + list);

        list.forEach(n -> System.out.print(n + ";"));
        System.out.println();
    }

    // ------------------------------------------------------------------
    static void comparators() {
        System.out.println("\n--- 3. Comparator improvements ---");
        List<String> names = new ArrayList<>(Arrays.asList("Bob", "alice", "Carl", "dave"));

        names.sort(Comparator.naturalOrder());
        System.out.println("natural:           " + names);          // uppercase first!
        names.sort(String.CASE_INSENSITIVE_ORDER);
        System.out.println("ignore case:       " + names);
        names.sort(Comparator.comparing(String::length).thenComparing(Comparator.reverseOrder()));
        System.out.println("length, then Z-A:  " + names);
    }

    // ------------------------------------------------------------------
    static void joiningStrings() {
        System.out.println("\n--- 4. String.join and StringJoiner ---");
        System.out.println(String.join("-", "2024", "03", "15"));                       // 2024-03-15
        System.out.println(String.join(", ", Arrays.asList("red", "green", "blue")));   // works with any Iterable

        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        joiner.add("one").add("two").add("three");
        System.out.println(joiner);                                                     // {one, two, three}
        System.out.println(new StringJoiner(",", "[", "]").setEmptyValue("EMPTY"));     // EMPTY
    }

    // ------------------------------------------------------------------
    static void base64() {
        System.out.println("\n--- 5. Base64 (built in at last) ---");
        String original = "Hello, Java 8!";
        String encoded = Base64.getEncoder().encodeToString(original.getBytes(StandardCharsets.UTF_8));
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        System.out.println(encoded + " -> " + decoded);
        // Also: Base64.getUrlEncoder() (safe for URLs), Base64.getMimeEncoder() (email)
    }

    // ------------------------------------------------------------------
    static void helpers() {
        System.out.println("\n--- 6. Objects and number helpers ---");
        // Objects.requireNonNull with a message
        try {
            Objects.requireNonNull(null, "name must not be null");
        } catch (NullPointerException e) {
            System.out.println("NPE: " + e.getMessage());
        }
        System.out.println("Objects.nonNull(\"x\") = " + Objects.nonNull("x") + ", isNull(null) = " + Objects.isNull(null));

        // Method-reference friendly helpers
        System.out.println("Integer.sum(2,3)=" + Integer.sum(2, 3) + " max=" + Integer.max(2, 3) + " min=" + Integer.min(2, 3));
        System.out.println("Integer.compare / Boolean.logicalAnd = " + Integer.compare(5, 3) + " / " + Boolean.logicalAnd(true, false));

        // Math exact methods: throw instead of silently overflowing
        try {
            Math.addExact(Integer.MAX_VALUE, 1);
        } catch (ArithmeticException e) {
            System.out.println("Math.addExact overflow -> ArithmeticException");
        }
        System.out.println("Math.toIntExact(42L) = " + Math.toIntExact(42L));
        System.out.println("Math.floorMod(-7, 3) = " + Math.floorMod(-7, 3));      // 2 (always positive)

        // Unsigned numbers
        System.out.println("Integer.toUnsignedString(-1) = " + Integer.toUnsignedString(-1));  // 4294967295
    }

    // ------------------------------------------------------------------
    static void arrays() {
        System.out.println("\n--- 7. Arrays: parallelSort, setAll, parallelPrefix ---");
        int[] data = {5, 3, 9, 1, 7};
        Arrays.parallelSort(data);                         // fast sort for big arrays
        System.out.println("parallelSort: " + Arrays.toString(data));

        int[] squares = new int[6];
        Arrays.setAll(squares, i -> i * i);                // fill using the index
        System.out.println("setAll:       " + Arrays.toString(squares));

        int[] running = {1, 2, 3, 4, 5};
        Arrays.parallelPrefix(running, Integer::sum);      // running total
        System.out.println("prefix sums:  " + Arrays.toString(running));
    }

    // ------------------------------------------------------------------
    // Repeating annotations: use the SAME annotation more than once
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Roles { Role[] value(); }

    @Repeatable(Roles.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Role { String value(); }

    @Role("admin")
    @Role("editor")      // allowed twice in Java 8!
    static class Account { }

    static void repeatingAnnotations() {
        System.out.println("\n--- 8. Repeating annotations ---");
        Role[] roles = Account.class.getAnnotationsByType(Role.class);
        for (Role r : roles) System.out.println("role = " + r.value());
        // Java 8 also added "type annotations" (annotations on types, e.g. List<@NonNull String>).
    }

    // ------------------------------------------------------------------
    static void concurrencyExtras() {
        System.out.println("\n--- 9. LongAdder and ThreadLocal.withInitial ---");
        // LongAdder: a counter that is much faster than AtomicLong under heavy parallel use
        LongAdder adder = new LongAdder();
        java.util.stream.IntStream.range(0, 10_000).parallel().forEach(i -> adder.increment());
        System.out.println("LongAdder total = " + adder.sum());

        // ThreadLocal.withInitial: a lambda gives each thread its own starting value
        ThreadLocal<StringBuilder> local = ThreadLocal.withInitial(StringBuilder::new);
        local.get().append("per-thread value");
        System.out.println(local.get());
    }

    // ------------------------------------------------------------------
    static void notes() {
        System.out.println("\n--- Other Java 8 items to know about (not demonstrated) ---");
        System.out.println(" * Nashorn: a JavaScript engine in the JDK (removed in Java 15).");
        System.out.println(" * StampedLock: a faster read/write lock with optimistic reading.");
        System.out.println(" * PermGen removed: class metadata now lives in 'Metaspace'.");
        System.out.println(" * Parameter names via javac -parameters + reflection.");
        System.out.println(" * jjs, jdeps tools; JavaFX 8 in Oracle JDK 8.");
    }
}
