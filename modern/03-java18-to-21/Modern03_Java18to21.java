import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * MODERN 03 - What Java 18 to 21 added (Java 21 is a long-term-support release)
 *
 * Needs JDK 21. Run with:  .\run-modern.ps1 3   or   ./run-modern.sh 3
 */
public class Modern03_Java18to21 {

    // sealed + records: a closed family of shapes
    sealed interface Shape permits Circle, Rect, Square {}
    record Circle(double radius) implements Shape {}
    record Rect(double width, double height) implements Shape {}
    record Square(double side) implements Shape {}

    public static void main(String[] args) throws Exception {
        sequencedCollections();
        patternMatchingSwitch();
        virtualThreads();
        executorServiceAutoCloseAndFutureState();
        smallApis();
        platformChanges();
        previewFeaturesNote();
    }

    // ------------------------------------------------------------------
    static void sequencedCollections() {
        System.out.println("--- Sequenced collections (Java 21, JEP 431) ---");
        // Java 8: last element = list.get(list.size() - 1);  reverse = Collections.reverse(copy) ...
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        System.out.println("first=" + list.getFirst() + " last=" + list.getLast());   // new in 21
        list.addFirst("start");
        list.removeLast();
        System.out.println(list + " reversed view: " + list.reversed());              // a VIEW, not a copy

        // Works the same for LinkedHashSet, Deque, TreeSet (SequencedSet) and LinkedHashMap (SequencedMap)
        LinkedHashMap<String, Integer> ranking = new LinkedHashMap<>();
        ranking.put("gold", 1);
        ranking.put("silver", 2);
        ranking.putFirst("platinum", 0);
        System.out.println("first entry: " + ranking.firstEntry() + ", reversed: " + ranking.reversed());

        // Streams of the reversed view: no more copy-then-Collections.reverse
        System.out.println(list.reversed().stream().collect(Collectors.joining(",")));
    }

    // ------------------------------------------------------------------
    static double area(Shape shape) {
        // Pattern matching for switch (Java 21, JEP 441) + record patterns (JEP 440).
        // No 'default' is needed: Shape is sealed, so the compiler knows the cases are complete.
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rect(double w, double h) -> w * h;               // record pattern: takes the record apart
            case Square(double s) when s > 100 -> -1;             // a guard with 'when'
            case Square(double s) -> s * s;
        };
    }

    static String describe(Object o) {
        return switch (o) {
            case null -> "null value";                            // null can be a case in Java 21
            case Integer i when i > 10 -> "big int " + i;
            case Integer i -> "int " + i;
            case String s -> "string of length " + s.length();
            default -> "something else";
        };
    }

    static void patternMatchingSwitch() {
        System.out.println("\n--- Pattern matching for switch and record patterns (Java 21) ---");
        List<Shape> shapes = List.of(new Circle(1), new Rect(2, 3), new Square(4), new Square(500));
        shapes.forEach(s -> System.out.printf("%s -> %.2f%n", s, area(s)));

        for (Object o : Arrays.asList(5, 50, "hello", 2.5, null)) {
            System.out.println(describe(o));
        }
    }

    // ------------------------------------------------------------------
    static void virtualThreads() throws Exception {
        System.out.println("\n--- Virtual threads (Java 21, JEP 444) ---");
        // A virtual thread is a very cheap thread managed by the JVM. You can have MILLIONS.
        Thread t = Thread.ofVirtual().start(() -> System.out.println("hello from a virtual thread: " + Thread.currentThread().isVirtual()));
        t.join();

        // 2,000 tasks that each WAIT 200 ms (like a database or web call).
        long start = System.currentTimeMillis();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {      // one new virtual thread per task
            List<Future<Integer>> futures = IntStream.range(0, 2000)
                    .mapToObj(i -> executor.submit(() -> {
                        Thread.sleep(200);                                      // waiting costs almost nothing
                        return i;
                    }))
                    .toList();
            long sum = 0;
            for (Future<Integer> f : futures) sum += f.get();
            System.out.println("sum of results = " + sum);
        }                                                                       // close() waits for all tasks (Java 19+)
        long took = System.currentTimeMillis() - start;
        System.out.println("2000 tasks of 200 ms finished in under 2 seconds? " + (took < 2000));

        // From JEP 444 (official guidance):
        //  - Virtual threads give higher THROUGHPUT for many waiting tasks. They are not faster for one task.
        //  - Do NOT pool virtual threads: create one per task.
        //  - The Stream API "remains the preferred way to process large data sets in parallel":
        //    parallel streams are for CPU work, virtual threads are for waiting (I/O) work.
    }

    // ------------------------------------------------------------------
    static void executorServiceAutoCloseAndFutureState() throws Exception {
        System.out.println("\n--- ExecutorService is AutoCloseable, Future.state() (Java 19) ---");
        try (var pool = Executors.newFixedThreadPool(2)) {          // Java 8 needed shutdown() + awaitTermination()
            Future<String> f = pool.submit(() -> "done");
            f.get();
            System.out.println("state = " + f.state() + ", resultNow = " + f.resultNow());
        }
    }

    // ------------------------------------------------------------------
    static void smallApis() {
        System.out.println("\n--- Small API additions (18 to 21) ---");
        System.out.println("Math.clamp(15, 0, 10)        = " + Math.clamp(15, 0, 10));       // 21
        System.out.println("Math.ceilDiv(7, 2)           = " + Math.ceilDiv(7, 2));           // 18
        System.out.println("StringBuilder.repeat         = " + new StringBuilder().repeat("ab", 3));   // 21
        System.out.println("splitWithDelimiters          = " + Arrays.toString("a1b2c".splitWithDelimiters("\\d", 0)));   // 21

        // 19: DateTimeFormatter.ofLocalizedPattern - pick fields, the locale picks the order and punctuation
        System.out.println("ofLocalizedPattern           = "
                + LocalDate.of(2024, 3, 15).format(DateTimeFormatter.ofLocalizedPattern("yMMMd").withLocale(Locale.US)));
    }

    // ------------------------------------------------------------------
    static void platformChanges() {
        System.out.println("\n--- Platform changes that can affect Java 8 code ---");
        // Java 18 (JEP 400): UTF-8 is the default charset on every operating system.
        // On Java 8 (Windows) the default was often windows-1252 or another code page.
        System.out.println("default charset: " + Charset.defaultCharset());
        System.out.println("Java version:    " + Runtime.version());
    }

    // ------------------------------------------------------------------
    static void previewFeaturesNote() {
        System.out.println("\n--- Preview features in Java 21 (not final: do not use in production) ---");
        System.out.println("String Templates (JEP 430), Scoped Values (446), Structured Concurrency (453),");
        System.out.println("Unnamed Patterns and Variables (443), Unnamed Classes (445). They need --enable-preview.");
    }
}
