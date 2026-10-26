import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MODERN 02 - What Java 12 to 17 added (Java 17 is a long-term-support release)
 *
 * Needs JDK 17 or newer (JDK 21 recommended).
 * Run with:  .\run-modern.ps1 2   or   ./run-modern.sh 2
 */
public class Modern02_Java12to17 {

    // ---- Java 16: records = a short way to write an immutable data class -------------
    // The compiler writes the constructor, getters (name(), dept()...), equals, hashCode, toString.
    record Emp(String name, String dept, double salary) {
        Emp {                                            // "compact constructor": validate here
            if (salary < 0) throw new IllegalArgumentException("salary < 0");
        }
        String label() { return name + "@" + dept; }     // records can have normal methods too
    }

    // ---- Java 17: sealed types = you decide exactly who may implement them -----------
    sealed interface Payment permits Card, Cash {}
    record Card(String number) implements Payment {}
    record Cash(double amount) implements Payment {}

    static String NAME;   // null on purpose, for the NullPointerException demo

    public static void main(String[] args) {
        teeingAndToList();
        mapMultiDemo();
        textAndStringHelpers();
        switchExpressions();
        records();
        patternMatchingInstanceof();
        sealedTypes();
        smallApiAdditions();
        helpfulNullPointerExceptions();
    }

    // ------------------------------------------------------------------
    static void teeingAndToList() {
        System.out.println("--- Collectors.teeing (12) and Stream.toList (16) ---");
        List<Employee> staff = Sample.employees();

        // teeing: run TWO collectors over the same stream, then combine the results.
        // Java 8 needed two passes (two streams) or a custom collector.
        String report = staff.stream().collect(Collectors.teeing(
                Collectors.counting(),
                Collectors.summingDouble(Employee::salary),
                (count, sum) -> count + " people, average salary " + (long) (sum / count)));
        System.out.println(report);

        String minMax = Stream.of(7, 2, 9, 4).collect(Collectors.teeing(
                Collectors.minBy(Integer::compare),
                Collectors.maxBy(Integer::compare),
                (min, max) -> "min=" + min.get() + " max=" + max.get()));
        System.out.println(minMax);

        // Stream.toList(): shorter than collect(Collectors.toList())
        List<String> names = staff.stream().map(Employee::name).limit(3).toList();
        System.out.println(names);
        // BUT it returns an UNMODIFIABLE list (and allows null). Collectors.toList() gives a normal ArrayList.
        try {
            names.add("x");
        } catch (UnsupportedOperationException e) {
            System.out.println("Stream.toList() result cannot be changed");
        }
    }

    // ------------------------------------------------------------------
    static void mapMultiDemo() {
        System.out.println("\n--- Stream.mapMulti (16) ---");
        // Like flatMap, but you push 0, 1 or many items into a "sink" instead of building a stream per item.
        // Good when most items produce only a few results (no extra Stream objects).
        List<Integer> out = Stream.of(1, 2, 3)
                .<Integer>mapMulti((n, sink) -> {
                    if (n % 2 == 1) {          // odd numbers: emit the number and ten times the number
                        sink.accept(n);
                        sink.accept(n * 10);
                    }                           // even numbers: emit nothing
                })
                .toList();
        System.out.println(out);   // [1, 10, 3, 30]
    }

    // ------------------------------------------------------------------
    static void textAndStringHelpers() {
        System.out.println("\n--- Text blocks (15) and new String methods (12, 15) ---");
        // Text block: write multi-line text without \n and \" noise.
        String json = """
                {
                  "name": "Asha",
                  "city": "Pune"
                }
                """;
        System.out.print(json);

        System.out.println("indent(4):   [" + "hi".indent(4).stripTrailing() + "]");        // 12
        System.out.println("transform:   " + "java".transform(s -> s + "!"));               // 12
        System.out.println("formatted:   " + "%s is %d years old".formatted("Asha", 30));   // 15 (instead of String.format)
    }

    // ------------------------------------------------------------------
    static void switchExpressions() {
        System.out.println("\n--- switch expressions (14) ---");
        // Java 8: a switch STATEMENT with break and a temp variable.
        // Java 14: a switch EXPRESSION that returns a value. No fall-through, no break.
        for (String day : List.of("SAT", "MON", "WED")) {
            String type = switch (day) {
                case "SAT", "SUN" -> "weekend";
                case "MON", "TUE", "WED", "THU", "FRI" -> {
                    String t = "work";           // a block may use 'yield' to return a value
                    yield t + "day";
                }
                default -> throw new IllegalArgumentException(day);
            };
            System.out.println(day + " -> " + type);
        }
    }

    // ------------------------------------------------------------------
    static void records() {
        System.out.println("\n--- records (16) ---");
        List<Emp> emps = Sample.employees().stream()
                .map(e -> new Emp(e.name(), e.department(), e.salary()))
                .toList();

        System.out.println(emps.get(0));                                  // toString for free
        System.out.println(emps.get(0).equals(new Emp("Alice", "Engineering", 120000)));   // equals for free
        System.out.println(emps.get(0).label());

        // Records work great with streams and method references: Emp::dept, Emp::salary
        Map<String, Double> payroll = emps.stream().collect(
                Collectors.groupingBy(Emp::dept, TreeMap::new, Collectors.summingDouble(Emp::salary)));
        System.out.println(payroll);

        try {
            new Emp("Bad", "X", -1);
        } catch (IllegalArgumentException e) {
            System.out.println("compact constructor validation works");
        }
    }

    // ------------------------------------------------------------------
    static void patternMatchingInstanceof() {
        System.out.println("\n--- pattern matching for instanceof (16) ---");
        Object o = "streams";
        // Java 8:  if (o instanceof String) { String s = (String) o; ... }
        if (o instanceof String s && s.length() > 3) {      // 's' is ready to use, no cast
            System.out.println("long string: " + s.toUpperCase());
        }
    }

    // ------------------------------------------------------------------
    static void sealedTypes() {
        System.out.println("\n--- sealed types (17) ---");
        for (Payment p : List.of(new Card("1234"), new Cash(20))) {
            // The compiler knows ONLY Card and Cash can exist.
            // (In Java 21 a switch can check this for you: see Modern 03.)
            String text = (p instanceof Card c) ? "card " + c.number() : "cash " + ((Cash) p).amount();
            System.out.println(text);
        }
    }

    // ------------------------------------------------------------------
    static void smallApiAdditions() {
        System.out.println("\n--- small API additions (12 to 17) ---");
        // 17: HexFormat
        System.out.println("HexFormat: " + HexFormat.of().formatHex(new byte[]{1, 10, (byte) 255}));
        // 17: RandomGenerator - one interface for all random number generators (Random now implements it)
        RandomGenerator rng = new java.util.Random(42);
        System.out.println("RandomGenerator ints in 0..9: " + rng.ints(5, 0, 10).allMatch(n -> n >= 0 && n < 10));
        // 12: Files.mismatch, 15: Math.absExact are other small helpers
        System.out.println("Math.absExact(-5) = " + Math.absExact(-5));
    }

    // ------------------------------------------------------------------
    static void helpfulNullPointerExceptions() {
        System.out.println("\n--- helpful NullPointerExceptions (JEP 358, Java 14) ---");
        try {
            NAME.length();
        } catch (NullPointerException e) {
            // Java 8 only said: java.lang.NullPointerException  (with no detail)
            System.out.println(e.getMessage());
        }
    }
}
