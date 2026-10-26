import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

/** Every example of the master cheat sheet that needs Java 9 to 21. Compiled with --release 21. */
public class Cheat21 {
    static int ok = 0, bad = 0;

    static void check(String label, Object actual, String expected) {
        String a = String.valueOf(actual);
        if (a.equals(expected)) ok++;
        else { bad++; System.out.println("MISMATCH " + label + "\n   expected: " + expected + "\n   actual:   " + a); }
    }

    record Point(int x, int y) {}
    sealed interface Shape permits Circle, Square {}
    record Circle(double r) implements Shape {}
    record Square(double s) implements Shape {}

    static String kind(Object o) {
        return switch (o) {
            case null -> "null";
            case Integer i when i > 10 -> "big int";
            case Integer i -> "int";
            case String s -> "text";
            case Point(int x, int y) -> "point " + (x + y);
            default -> "other";
        };
    }

    static double area(Shape s) {
        return switch (s) { case Circle c -> 3 * c.r() * c.r(); case Square q -> q.s() * q.s(); };
    }

    public static void main(String[] args) throws Exception {
        List<Employee> staff = Sample.employees();
        List<Integer> mixed = List.of(1, 2, 3, 10, 11, 4);

        // ---- 9: streams
        check("takeWhile", mixed.stream().takeWhile(n -> n < 5).toList(), "[1, 2, 3]");
        check("dropWhile", mixed.stream().dropWhile(n -> n < 5).toList(), "[10, 11, 4]");
        check("filter vs takeWhile", mixed.stream().filter(n -> n < 5).toList(), "[1, 2, 3, 4]");
        check("iterate 3-arg", Stream.iterate(1, n -> n < 100, n -> n * 3).toList(), "[1, 3, 9, 27, 81]");
        check("ofNullable", Stream.ofNullable((String) null).count(), "0");
        // ---- 16: toList / mapMulti
        List<Integer> fixed = Stream.of(3, 1, 2).toList();
        boolean uoe = false;
        try { fixed.add(4); } catch (UnsupportedOperationException e) { uoe = true; }
        check("Stream.toList unmodifiable", uoe, "true");
        check("Stream.toList allows null", Stream.of(1, null).toList(), "[1, null]");
        List<Integer> mutable = Stream.of(3, 1, 2).collect(Collectors.toCollection(ArrayList::new));
        mutable.add(4);
        check("toCollection ArrayList", mutable, "[3, 1, 2, 4]");
        check("mapMulti", Stream.of(1, 2, 3, 4).<Integer>mapMulti((n, sink) -> { if (n % 2 == 1) { sink.accept(n); sink.accept(n * 10); } }).toList(), "[1, 10, 3, 30]");
        // ---- collectors 9, 10, 12
        check("filtering", staff.stream().collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.filtering(e -> e.salary() > 100000, Collectors.mapping(Employee::name, Collectors.toList())))), "{Engineering=[Alice, Frank], HR=[], Marketing=[], Sales=[]}");
        check("filter before groupingBy", staff.stream().filter(e -> e.salary() > 100000).collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.mapping(Employee::name, Collectors.toList()))), "{Engineering=[Alice, Frank]}");
        check("flatMapping", Stream.of("ab", "cd").collect(Collectors.flatMapping(s -> s.chars().mapToObj(c -> (char) c), Collectors.toList())), "[a, b, c, d]");
        boolean npe = false;
        try { Stream.of(1, null).collect(Collectors.toUnmodifiableList()); } catch (NullPointerException e) { npe = true; }
        check("toUnmodifiableList rejects null", npe, "true");
        check("teeing", staff.stream().collect(Collectors.teeing(Collectors.counting(), Collectors.averagingDouble(Employee::salary), (n, avg) -> n + " people, average " + Math.round(avg))), "10 people, average 79300");
        check("teeing min max", Stream.of(7, 2, 9).collect(Collectors.teeing(Collectors.minBy(Integer::compare), Collectors.maxBy(Integer::compare), (a, b) -> a.get() + ".." + b.get())), "2..9");
        // ---- Optional 9, 10, 11
        StringBuilder log = new StringBuilder();
        Optional.of("x").ifPresentOrElse(v -> log.append("have ").append(v), () -> log.append("none"));
        Optional.<String>empty().ifPresentOrElse(v -> log.append("have ").append(v), () -> log.append("|none"));
        check("ifPresentOrElse", log, "have x|none");
        check("Optional.or", Optional.<String>empty().or(() -> Optional.of("b")), "Optional[b]");
        check("Optional.stream", Stream.of(Optional.of("a"), Optional.<String>empty(), Optional.of("c")).flatMap(Optional::stream).toList(), "[a, c]");
        check("Optional.isEmpty", Optional.empty().isEmpty(), "true");
        check("orElseThrow()", Optional.of("v").orElseThrow(), "v");
        boolean nse = false;
        try { Optional.empty().orElseThrow(); } catch (NoSuchElementException e) { nse = true; }
        check("orElseThrow() on empty", nse, "true");
        // ---- collections 9, 10, 21
        boolean immutable = false;
        try { List.of(1, 2).add(3); } catch (UnsupportedOperationException e) { immutable = true; }
        check("List.of immutable", immutable, "true");
        boolean noNull = false;
        try { List.of(1, null); } catch (NullPointerException e) { noNull = true; }
        check("List.of rejects null", noNull, "true");
        check("Map.of", new TreeMap<>(Map.of("a", 1, "b", 2)), "{a=1, b=2}");
        check("Map.entry", Map.entry("k", 5), "k=5");
        List<Integer> src = new ArrayList<>(List.of(1, 2));
        List<Integer> snap = List.copyOf(src);
        src.add(3);
        check("List.copyOf is a snapshot", snap, "[1, 2]");
        List<String> letters = List.of("x", "y", "z");
        check("getFirst/getLast", letters.getFirst() + " " + letters.getLast(), "x z");
        check("reversed", letters.reversed(), "[z, y, x]");
        LinkedHashMap<String, Integer> lhm = new LinkedHashMap<>();
        lhm.put("a", 1); lhm.put("b", 2);
        check("firstEntry/lastEntry", lhm.firstEntry() + " " + lhm.lastEntry(), "a=1 b=2");
        // ---- lambdas / language
        check("Predicate.not", Stream.of("a", "", " ", "b").filter(Predicate.not(String::isBlank)).toList(), "[a, b]");
        var inferred = new HashMap<String, List<Integer>>();
        check("var", inferred.getClass().getSimpleName(), "HashMap");
        BiFunction<Integer, Integer, Integer> varLambda = (var a, var b) -> a + b;
        check("var in lambda", varLambda.apply(2, 3), "5");
        check("record", new Point(1, 2) + " " + new Point(1, 2).equals(new Point(1, 2)) + " " + new Point(1, 2).x(), "Point[x=1, y=2] true 1");
        Object o = "streams";
        check("instanceof pattern", (o instanceof String s && s.length() > 3) ? s.toUpperCase() : "no", "STREAMS");
        check("switch expression", switch (3) { case 1, 2 -> "low"; case 3 -> "three"; default -> "other"; }, "three");
        check("switch patterns", kind(5) + "|" + kind(50) + "|" + kind("t") + "|" + kind(new Point(1, 2)) + "|" + kind(null) + "|" + kind(2.5), "int|big int|text|point 3|null|other");
        check("sealed + switch (no default)", area(new Square(3)) + " " + area(new Circle(1)), "9.0 3.0");
        String block = """
                hi
                there""";
        check("text block", block.replace("\n", "|"), "hi|there");
        // ---- strings / math
        check("isBlank", "   ".isBlank(), "true");
        check("strip", "[" + "  hi  ".strip() + "]", "[hi]");
        check("repeat", "ab".repeat(3), "ababab");
        check("lines", "one\ntwo".lines().toList(), "[one, two]");
        check("formatted", "%s is %d".formatted("Asha", 30), "Asha is 30");
        check("Math.clamp", Math.clamp(15, 0, 10), "10");
        // ---- concurrency
        String fallback = new CompletableFuture<String>().completeOnTimeout("default", 50, TimeUnit.MILLISECONDS).join();
        check("completeOnTimeout", fallback, "default");
        String timeout = "no";
        try { new CompletableFuture<String>().orTimeout(50, TimeUnit.MILLISECONDS).join(); } catch (CompletionException e) { timeout = e.getCause().getClass().getSimpleName(); }
        check("orTimeout", timeout, "TimeoutException");
        String failed = "";
        try { CompletableFuture.failedFuture(new IllegalStateException("boom")).join(); } catch (CompletionException e) { failed = e.getCause().getMessage(); }
        check("failedFuture", failed, "boom");
        CompletableFuture<String> done = CompletableFuture.completedFuture("x");
        check("Future.state / resultNow", done.state() + " " + done.resultNow(), "SUCCESS x");
        try (var pool = Executors.newFixedThreadPool(2)) { check("ExecutorService try-with-resources", pool.submit(() -> 7).get(), "7"); }
        try (var vpool = Executors.newVirtualThreadPerTaskExecutor()) { check("virtual thread executor", vpool.submit(() -> Thread.currentThread().isVirtual()).get(), "true"); }
        Thread vt = Thread.ofVirtual().start(() -> {});
        vt.join();
        check("Thread.ofVirtual", vt.isVirtual(), "true");

        System.out.println("Cheat21: " + ok + " passed, " + bad + " failed");
        if (bad > 0) System.exit(1);
    }
}
