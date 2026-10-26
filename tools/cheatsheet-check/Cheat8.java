import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.*;

/** Every example of the master cheat sheet that is JAVA 8 code. Compiled with --release 8. */
public class Cheat8 {
    static int ok = 0, bad = 0;

    static void check(String label, Object actual, String expected) {
        String a = String.valueOf(actual);
        if (a.equals(expected)) ok++;
        else { bad++; System.out.println("MISMATCH " + label + "\n   expected: " + expected + "\n   actual:   " + a); }
    }

    static int[] fill() { int[] a = new int[4]; Arrays.setAll(a, i -> i * i); return a; }

    /** Speed tip 1: count how many times map runs when it comes BEFORE and AFTER the filter. */
    static String cheaper() {
        int[] calls = {0, 0};
        List<Integer> data = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        data.stream().map(n -> { calls[0]++; return n * 2; }).filter(n -> n < 20).count();
        data.stream().filter(n -> n * 2 < 20).map(n -> { calls[1]++; return n * 2; }).count();
        return calls[0] + " vs " + calls[1];
    }

    public static void main(String[] args) throws Exception {
        List<Employee> staff = Sample.employees();
        List<Integer> one5 = Arrays.asList(1, 2, 3, 4, 5);

        // ---- create
        check("range", Arrays.toString(IntStream.range(0, 5).toArray()), "[0, 1, 2, 3, 4]");
        check("rangeClosed", Arrays.toString(IntStream.rangeClosed(1, 5).toArray()), "[1, 2, 3, 4, 5]");
        check("iterate", Stream.iterate(1, n -> n * 2).limit(5).collect(Collectors.toList()), "[1, 2, 4, 8, 16]");
        check("generate", Stream.generate(() -> "x").limit(3).collect(Collectors.toList()), "[x, x, x]");
        check("chars", Arrays.toString("abc".chars().toArray()), "[97, 98, 99]");
        check("Stream.of(int[]) trap", Stream.of(new int[]{1, 2, 3}).count(), "1");
        check("Arrays.stream(int[])", Arrays.stream(new int[]{1, 2, 3}).sum(), "6");
        // ---- intermediate pictures
        check("filter", one5.stream().filter(x -> x % 2 == 0).collect(Collectors.toList()), "[2, 4]");
        check("map", Stream.of(1, 2, 3).map(x -> x * 10).collect(Collectors.toList()), "[10, 20, 30]");
        check("flatMap", Stream.of(1, 2, 3).flatMap(x -> Stream.of(x, -x)).collect(Collectors.toList()), "[1, -1, 2, -2, 3, -3]");
        check("flatMap lists", Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3), Arrays.asList(4, 5)).stream().flatMap(List::stream).collect(Collectors.toList()), "[1, 2, 3, 4, 5]");
        check("distinct", Stream.of(1, 1, 2, 2, 3).distinct().collect(Collectors.toList()), "[1, 2, 3]");
        check("sorted", Stream.of(4, 2, 1, 5, 3).sorted().collect(Collectors.toList()), "[1, 2, 3, 4, 5]");
        check("sorted reverse", Stream.of(4, 2, 1).sorted(Comparator.reverseOrder()).collect(Collectors.toList()), "[4, 2, 1]");
        check("limit", one5.stream().limit(3).collect(Collectors.toList()), "[1, 2, 3]");
        check("skip", one5.stream().skip(2).collect(Collectors.toList()), "[3, 4, 5]");
        check("page", IntStream.rangeClosed(1, 20).boxed().skip(5).limit(5).collect(Collectors.toList()), "[6, 7, 8, 9, 10]");
        check("concat", Stream.concat(Stream.of(1, 2), Stream.of(3, 4)).collect(Collectors.toList()), "[1, 2, 3, 4]");
        // ---- terminal pictures
        check("findFirst", one5.stream().filter(x -> x % 2 == 0).findFirst(), "Optional[2]");
        check("allMatch", one5.stream().allMatch(x -> x < 6), "true");
        check("anyMatch", one5.stream().anyMatch(x -> x > 4), "true");
        check("noneMatch", one5.stream().noneMatch(x -> x > 5), "true");
        check("count", one5.stream().count(), "5");
        check("min", one5.stream().min(Comparator.naturalOrder()), "Optional[1]");
        check("max", one5.stream().max(Comparator.naturalOrder()), "Optional[5]");
        check("reduce", one5.stream().reduce(0, Integer::sum), "15");
        check("reduce no identity", one5.stream().reduce(Integer::max), "Optional[5]");
        check("reduce 3-arg", Stream.of("ab", "cde").reduce(0, (acc, s) -> acc + s.length(), Integer::sum), "5");
        check("toArray", Stream.of("a", "b").toArray(String[]::new).length, "2");
        // ---- primitive streams
        check("IntStream sum", IntStream.of(3, 1, 2).sum(), "6");
        check("IntStream average", IntStream.of(3, 1, 2).average(), "OptionalDouble[2.0]");
        check("IntStream max", IntStream.of(3, 1, 2).max(), "OptionalInt[3]");
        check("empty average", IntStream.empty().average().orElse(0), "0.0");
        check("summaryStatistics", IntStream.of(3, 1, 2).summaryStatistics(), "IntSummaryStatistics{count=3, sum=6, min=1, average=2.000000, max=3}");
        check("boxed", IntStream.rangeClosed(1, 3).boxed().collect(Collectors.toList()), "[1, 2, 3]");
        check("mapToObj", IntStream.rangeClosed(1, 3).mapToObj(i -> "Item-" + i).collect(Collectors.toList()), "[Item-1, Item-2, Item-3]");
        check("sum 1..100", IntStream.rangeClosed(1, 100).sum(), "5050");
        check("20!", LongStream.rangeClosed(1, 20).reduce(1L, (a, b) -> a * b), "2432902008176640000");
        check("mapToDouble sum", staff.stream().mapToDouble(Employee::salary).sum(), "793000.0");
        // ---- collectors
        check("toCollection", Stream.of("b", "a", "c").collect(Collectors.toCollection(TreeSet::new)), "[a, b, c]");
        check("toMap", staff.stream().limit(3).collect(Collectors.toMap(Employee::name, Employee::age, (a, b) -> a, LinkedHashMap::new)), "{Alice=30, Bob=45, Carol=28}");
        boolean dup = false;
        try { staff.stream().collect(Collectors.toMap(Employee::city, Employee::name)); } catch (IllegalStateException e) { dup = true; }
        check("toMap duplicate key throws", dup, "true");
        check("toMap merge", staff.stream().collect(Collectors.toMap(Employee::city, Employee::name, (a, b) -> a + "," + b, TreeMap::new)), "{Berlin=Dave,Frank,Ivan, London=Alice,Carol,Grace,Judy, Paris=Bob,Eve,Heidi}");
        check("joining", Stream.of("a", "b", "c").collect(Collectors.joining(", ", "[", "]")), "[a, b, c]");
        check("counting", staff.stream().collect(Collectors.counting()), "10");
        check("summingInt", staff.stream().collect(Collectors.summingInt(Employee::age)), "346");
        check("averagingDouble", staff.stream().collect(Collectors.averagingDouble(Employee::salary)), "79300.0");
        check("summarizingInt", staff.stream().collect(Collectors.summarizingInt(Employee::age)), "IntSummaryStatistics{count=10, sum=346, min=26, average=34.600000, max=45}");
        check("maxBy", staff.stream().collect(Collectors.maxBy(Comparator.comparingDouble(Employee::salary))).map(Employee::name).get(), "Alice");
        check("mapping", staff.stream().limit(3).collect(Collectors.mapping(e -> e.name().toUpperCase(), Collectors.joining("|"))), "ALICE|BOB|CAROL");
        check("collectingAndThen", staff.stream().limit(2).map(Employee::name).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)), "[Alice, Bob]");
        boolean uoe = false;
        try { Stream.of(1).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)).add(2); } catch (UnsupportedOperationException e) { uoe = true; }
        check("read-only list", uoe, "true");
        check("groupingBy counting", staff.stream().collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.counting())), "{Engineering=3, HR=2, Marketing=2, Sales=3}");
        check("groupingBy avg", staff.stream().collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.averagingDouble(Employee::salary))).get("HR"), "59000.0");
        check("groupingBy names", staff.stream().collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.mapping(Employee::name, Collectors.toList()))).get("Sales"), "[Carol, Dave, Judy]");
        check("groupingBy max", staff.stream().collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingDouble(Employee::salary)), o -> o.get().name()))), "{Engineering=Alice, HR=Eve, Marketing=Heidi, Sales=Judy}");
        check("groupingBy 2 levels", staff.stream().collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.groupingBy(Employee::city, TreeMap::new, Collectors.counting()))).get("Sales"), "{Berlin=1, London=2}");
        check("partitioningBy", staff.stream().collect(Collectors.partitioningBy(e -> e.age() >= 40, Collectors.mapping(Employee::name, Collectors.toList()))), "{false=[Alice, Carol, Dave, Frank, Grace, Heidi, Ivan], true=[Bob, Eve, Judy]}");
        check("reducing", staff.stream().collect(Collectors.reducing(0.0, Employee::salary, Double::sum)), "793000.0");
        // ---- recipes
        check("names in Engineering", staff.stream().filter(e -> e.department().equals("Engineering")).map(Employee::name).collect(Collectors.toList()), "[Alice, Bob, Frank]");
        check("average age", staff.stream().mapToInt(Employee::age).average().orElse(0), "34.6");
        check("top 3 earners", staff.stream().sorted(Comparator.comparingDouble(Employee::salary).reversed()).limit(3).map(Employee::name).collect(Collectors.toList()), "[Alice, Frank, Bob]");
        check("2nd highest salary", staff.stream().map(Employee::salary).distinct().sorted(Comparator.reverseOrder()).skip(1).findFirst().get(), "105000.0");
        Map<String, Long> perDept = staff.stream().collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
        check("sort map by value", perDept.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey())).map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList()), "[Engineering=3, Sales=3, HR=2, Marketing=2]");
        check("first per city", staff.stream().collect(Collectors.toMap(Employee::city, Employee::name, (a, b) -> a, LinkedHashMap::new)), "{London=Alice, Paris=Bob, Berlin=Dave}");
        check("word frequency", Arrays.stream("the cat the dog the end".split(" ")).collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting())), "{cat=1, dog=1, end=1, the=3}");
        check("char frequency", "banana".chars().mapToObj(c -> (char) c).collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting())), "{a=3, b=1, n=2}");
        check("duplicates", Stream.of(1, 2, 3, 2, 4, 3).collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList()), "[2, 3]");
        check("palindrome", IntStream.range(0, "racecar".length() / 2).allMatch(i -> "racecar".charAt(i) == "racecar".charAt("racecar".length() - 1 - i)), "true");
        check("sum of digits", String.valueOf(12345).chars().map(c -> c - '0').sum(), "15");
        check("reverse each word", Arrays.stream("java is fun".split(" ")).map(w -> new StringBuilder(w).reverse().toString()).collect(Collectors.joining(" ")), "avaj si nuf");
        check("sum of odd squares", IntStream.rangeClosed(1, 10).filter(n -> n % 2 == 1).map(n -> n * n).sum(), "165");
        check("fibonacci", Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]}).limit(10).map(f -> f[0]).collect(Collectors.toList()), "[0, 1, 1, 2, 3, 5, 8, 13, 21, 34]");
        check("anagram key", Stream.of("listen", "silent", "enlist", "cat").collect(Collectors.groupingBy(s -> { char[] c = s.toCharArray(); Arrays.sort(c); return new String(c); }, TreeMap::new, Collectors.toList())).values().stream().filter(g -> g.size() > 1).collect(Collectors.toList()), "[[listen, silent, enlist]]");
        // ---- Comparator
        check("sort dept then salary desc", staff.stream().sorted(Comparator.comparing(Employee::department).thenComparing(Employee::salary, Comparator.reverseOrder())).limit(3).map(Employee::name).collect(Collectors.toList()), "[Alice, Frank, Bob]");
        check("comparing reversed (method ref)", staff.stream().sorted(Comparator.comparing(Employee::salary).reversed()).findFirst().get().name(), "Alice");
        check("comparing reversed (typed lambda)", staff.stream().sorted(Comparator.comparing((Employee e) -> e.salary()).reversed()).findFirst().get().name(), "Alice");
        check("nullsLast", Stream.of("b", null, "a").sorted(Comparator.nullsLast(Comparator.<String>naturalOrder())).collect(Collectors.toList()), "[a, b, null]");
        check("by length then A-Z", Stream.of("pear", "fig", "apple", "kiwi").sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())).collect(Collectors.toList()), "[fig, kiwi, pear, apple]");
        // ---- functional interfaces
        Predicate<Integer> even = n -> n % 2 == 0, big = n -> n > 100;
        check("Predicate and/or/negate", even.and(big).test(200) + " " + even.or(big).test(7) + " " + even.negate().test(7), "true false true");
        Function<Integer, Integer> dbl = n -> n * 2, plus1 = n -> n + 1;
        check("andThen", dbl.andThen(plus1).apply(5), "11");
        check("compose", dbl.compose(plus1).apply(5), "12");
        check("BinaryOperator.minBy", BinaryOperator.minBy(Comparator.<Integer>naturalOrder()).apply(8, 3), "3");
        BiFunction<Integer, Integer, String> bf = (a, b) -> a + "+" + b;
        check("BiFunction", bf.apply(2, 3), "2+3");
        Supplier<String> sup = () -> "hi";
        check("Supplier", sup.get(), "hi");
        UnaryOperator<String> up = String::toUpperCase;
        check("UnaryOperator", up.apply("a"), "A");
        Function<Integer, Function<Integer, Integer>> add = a -> b -> a + b;
        check("curry", add.apply(10).apply(5), "15");
        // ---- method references (4 kinds)
        Function<String, Integer> parse = Integer::parseInt;
        check("static ref", parse.apply("42"), "42");
        Function<String, Integer> len = String::length;
        check("unbound ref", len.apply("abc"), "3");
        String prefix = "Item: ";
        Function<String, String> pre = prefix::concat;
        check("bound ref", pre.apply("pen"), "Item: pen");
        Supplier<List<String>> mk = ArrayList::new;
        check("constructor ref", mk.get(), "[]");
        // ---- Optional
        check("Optional chain", Optional.of(" java ").map(String::trim).filter(s -> !s.isEmpty()).map(String::toUpperCase).orElse("?"), "JAVA");
        check("Optional empty orElse", Optional.empty().orElse("x"), "x");
        check("ofNullable", Optional.ofNullable(null).isPresent(), "false");
        check("orElseGet", Optional.of("v").orElseGet(() -> "z"), "v");
        check("flatMap Optional", Optional.of("a").flatMap(s -> Optional.of(s + "b")).get(), "ab");
        // ---- Map / List / String helpers
        Map<String, Integer> counts = new TreeMap<String, Integer>();
        for (String w : "to be or not to be".split(" ")) counts.merge(w, 1, Integer::sum);
        check("Map.merge", counts, "{be=2, not=1, or=1, to=2}");
        Map<Integer, List<String>> byLen = new TreeMap<Integer, List<String>>();
        for (String w : Arrays.asList("hi", "yo", "hello")) byLen.computeIfAbsent(w.length(), k -> new ArrayList<String>()).add(w);
        check("computeIfAbsent", byLen, "{2=[hi, yo], 5=[hello]}");
        check("getOrDefault", new HashMap<String, Integer>().getOrDefault("x", 0), "0");
        List<Integer> rm = new ArrayList<Integer>(Arrays.asList(5, 8, 3, 12));
        rm.removeIf(n -> n > 10);
        check("removeIf", rm, "[5, 8, 3]");
        rm.replaceAll(n -> n * 10);
        check("replaceAll", rm, "[50, 80, 30]");
        rm.sort(Comparator.reverseOrder());
        check("List.sort", rm, "[80, 50, 30]");
        check("String.join", String.join("-", "2024", "03", "15"), "2024-03-15");
        check("StringJoiner", new StringJoiner(", ", "{", "}").add("a").add("b"), "{a, b}");
        check("Base64", Base64.getEncoder().encodeToString("Hi".getBytes("UTF-8")), "SGk=");
        check("Arrays.setAll", Arrays.toString(fill()), "[0, 1, 4, 9]");
        // ---- java.time
        LocalDate d = LocalDate.of(2024, 3, 15);
        check("plusDays", d.plusDays(10), "2024-03-25");
        check("day of week", d.getDayOfWeek(), "FRIDAY");
        check("days between", ChronoUnit.DAYS.between(d, LocalDate.of(2024, 12, 25)), "285");
        check("Period", Period.between(d, LocalDate.of(2024, 12, 25)), "P9M10D");
        check("format", d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "15/03/2024");
        check("parse", LocalDate.parse("2024-03-15").equals(d), "true");
        check("Duration", Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 45)), "PT8H45M");
        check("Duration hours", Duration.ofMinutes(125).toHours(), "2");
        check("age", Period.between(LocalDate.of(1990, 6, 20), d).getYears(), "33");
        // ---- traps (each one must really behave as the sheet says)
        check("sorted reverseOrder", Stream.of(1, 3, 2).sorted(Comparator.reverseOrder()).collect(Collectors.toList()), "[3, 2, 1]");
        String t1 = "none";
        try { Stream.of("a").collect(Collectors.toMap(s -> s, s -> (String) null)); } catch (NullPointerException e) { t1 = "NPE"; }
        check("toMap null value", t1, "NPE");
        String t2 = "none";
        try { Stream.of("a").collect(Collectors.groupingBy(s -> (String) null)); } catch (NullPointerException e) { t2 = "NPE"; }
        check("groupingBy null key", t2, "NPE");
        String t3 = "none";
        try { Optional.of((String) null); } catch (NullPointerException e) { t3 = "NPE"; }
        check("Optional.of(null)", t3, "NPE");
        String t4 = "none";
        try { Optional.empty().get(); } catch (NoSuchElementException e) { t4 = "NSEE"; }
        check("Optional.get() on empty", t4, "NSEE");
        Stream<Integer> used = Stream.of(1);
        used.count();
        String t5 = "none";
        try { used.count(); } catch (IllegalStateException e) { t5 = "ISE"; }
        check("stream used twice", t5, "ISE");
        List<Integer> src = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
        String t6 = "none";
        try { src.stream().forEach(n -> src.add(n + 10)); } catch (ConcurrentModificationException e) { t6 = "CME"; }
        check("change source while streaming", t6, "CME");
        check("filter then map is cheaper", cheaper(), "1000 vs 9");
        // ---- parallel + CompletableFuture
        check("parallel sum", IntStream.rangeClosed(1, 1000).parallel().sum(), "500500");
        check("parallel keeps collect order", IntStream.rangeClosed(1, 5).boxed().parallel().map(n -> n * 10).collect(Collectors.toList()), "[10, 20, 30, 40, 50]");
        check("CF thenCombine", CompletableFuture.supplyAsync(() -> 2).thenApply(x -> x * 10).thenCombine(CompletableFuture.supplyAsync(() -> 5), Integer::sum).join(), "25");
        check("CF exceptionally", CompletableFuture.<Integer>supplyAsync(() -> { throw new IllegalStateException("x"); }).exceptionally(ex -> -1).join(), "-1");
        check("CF thenCompose", CompletableFuture.supplyAsync(() -> 3).thenCompose(x -> CompletableFuture.supplyAsync(() -> x + 1)).join(), "4");

        System.out.println("Cheat8: " + ok + " passed, " + bad + " failed");
        if (bad > 0) System.exit(1);
    }
}
