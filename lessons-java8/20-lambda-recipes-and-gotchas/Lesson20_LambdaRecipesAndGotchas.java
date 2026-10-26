import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 20 - Lambda Recipes and Gotchas
 *
 * Patterns that experienced Java 8 developers use every day,
 * plus the confusing compiler situations that surprise beginners.
 */
public class Lesson20_LambdaRecipesAndGotchas {

    public static void main(String[] args) throws Exception {
        typeInferenceGotcha();
        lambdasAndThreads();
        lazyWithSupplier();
        functionsThatReturnFunctions();
        strategyPattern();
        checkedExceptionWrapper();
        moreStreamTools();
        unorderedAndParallel();
        nullKeyGotcha();
    }

    // ------------------------------------------------------------------
    static void typeInferenceGotcha() {
        System.out.println("--- 1. GOTCHA: type inference with comparing(...).reversed() ---");
        List<Employee> staff = new ArrayList<>(Sample.employees());

        // This does NOT compile:
        //     staff.sort(Comparator.comparing(e -> e.getSalary()).reversed());
        // Why? Java reads the chain left to right. When it sees  comparing(e -> ...)
        // it does not yet know that 'e' is an Employee, so it assumes Object.
        // (The  .sort(...)  part is too far away to help.)

        // Fix 1: a method reference (it carries the type with it)
        staff.sort(Comparator.comparing(Employee::getSalary).reversed());
        System.out.println("fix 1: " + staff.get(0).getName());

        // Fix 2: write the type of the lambda parameter
        staff.sort(Comparator.comparing((Employee e) -> e.getSalary()).reversed());
        System.out.println("fix 2: " + staff.get(0).getName());

        // Fix 3: tell the method its type arguments ("type witness")
        staff.sort(Comparator.<Employee, Double>comparing(e -> e.getSalary()).reversed());
        System.out.println("fix 3: " + staff.get(0).getName());

        // Fix 4: avoid reversed() - use a reversed comparator inside
        staff.sort(Comparator.comparing(Employee::getSalary, Comparator.reverseOrder()));
        System.out.println("fix 4: " + staff.get(0).getName());
        // Same problem appears with  thenComparing(...)  after a lambda-based comparing(...).
    }

    // ------------------------------------------------------------------
    static void lambdasAndThreads() throws Exception {
        System.out.println("\n--- 2. Lambdas with Thread / Runnable / Callable ---");
        Thread t = new Thread(() -> System.out.println("hello from " + Thread.currentThread().getName()), "worker-1");
        t.start();
        t.join();

        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            // Runnable: no result.   Callable: returns a result and may throw.
            Future<?> f1 = pool.submit(() -> System.out.println("a Runnable ran"));
            f1.get();

            Callable<Integer> job = () -> 6 * 7;
            Future<Integer> f2 = pool.submit(job);
            System.out.println("Callable result = " + f2.get());

            // A lambda that returns a value is treated as Callable; one that returns nothing as Runnable.
            Future<String> f3 = pool.submit(() -> "text result");
            System.out.println(f3.get());
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    static String expensiveReport() {
        System.out.println("   (building expensive report...)");
        return "report";
    }

    static void log(boolean enabled, Supplier<String> message) {
        if (enabled) System.out.println("LOG: " + message.get());   // get() runs only when needed
    }

    static void lazyWithSupplier() {
        System.out.println("\n--- 3. Lazy values with Supplier ---");
        System.out.println("logging OFF:");
        log(false, () -> "value is " + expensiveReport());   // report is NOT built
        System.out.println("logging ON:");
        log(true, () -> "value is " + expensiveReport());    // built only now
        // Same idea as Optional.orElseGet and Objects.requireNonNull(x, supplier).
    }

    // ------------------------------------------------------------------
    static Predicate<String> longerThan(int n) {              // a method that RETURNS a function
        return s -> s.length() > n;
    }

    static void functionsThatReturnFunctions() {
        System.out.println("\n--- 4. Functions that return functions (currying) ---");

        List<String> words = Arrays.asList("hi", "hello", "wonderful", "yes");
        System.out.println("longer than 2: " + words.stream().filter(longerThan(2)).collect(Collectors.toList()));
        System.out.println("longer than 4: " + words.stream().filter(longerThan(4)).collect(Collectors.toList()));

        // Currying: turn a 2-argument function into a chain of 1-argument functions
        Function<Integer, Function<Integer, Integer>> add = a -> b -> a + b;
        Function<Integer, Integer> add10 = add.apply(10);
        System.out.println("add10(5) = " + add10.apply(5));

        // Build a pipeline from a list of steps with reduce + andThen
        List<Function<String, String>> steps = Arrays.asList(String::trim, String::toUpperCase, s -> s + "!");
        Function<String, String> pipeline = steps.stream().reduce(Function.identity(), Function::andThen);
        System.out.println(pipeline.apply("  hello  "));    // HELLO!
    }

    // ------------------------------------------------------------------
    static void strategyPattern() {
        System.out.println("\n--- 5. Strategy pattern: a Map of lambdas replaces if/else or switch ---");
        Map<String, BinaryOperator<Integer>> calculator = new HashMap<>();
        calculator.put("+", (a, b) -> a + b);
        calculator.put("-", (a, b) -> a - b);
        calculator.put("*", (a, b) -> a * b);
        calculator.put("max", Math::max);

        for (String op : new String[]{"+", "-", "*", "max"}) {
            System.out.println("8 " + op + " 3 = " + calculator.get(op).apply(8, 3));
        }
        // To add a new operation you add one line. No if/else to edit.
    }

    // ------------------------------------------------------------------
    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    /** Turns a function that may throw a checked exception into a normal Function. */
    static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static int strictParse(String s) throws Exception {   // pretend it throws a CHECKED exception
        if (!s.matches("\\d+")) throw new Exception("not a number: " + s);
        return Integer.parseInt(s);
    }

    static void checkedExceptionWrapper() {
        System.out.println("\n--- 6. Reusable wrapper for checked exceptions ---");
        // map(Lesson20::strictParse) would NOT compile (checked exception). The wrapper fixes it:
        List<Integer> ok = Stream.of("1", "2", "3").map(unchecked(Lesson20_LambdaRecipesAndGotchas::strictParse))
                .collect(Collectors.toList());
        System.out.println(ok);
        try {
            Stream.of("1", "x").map(unchecked(Lesson20_LambdaRecipesAndGotchas::strictParse)).collect(Collectors.toList());
        } catch (RuntimeException e) {
            System.out.println("wrapped: " + e.getCause().getMessage());
        }
    }

    // ------------------------------------------------------------------
    static void moreStreamTools() {
        System.out.println("\n--- 7. More stream tools ---");

        // groupingBy with toSet as downstream (no duplicates inside each group)
        Map<String, Set<String>> citiesByDept = Sample.employees().stream().collect(
                Collectors.groupingBy(Employee::getDepartment, java.util.TreeMap::new,
                        Collectors.mapping(Employee::getCity, Collectors.toCollection(java.util.TreeSet::new))));
        System.out.println("cities per department: " + citiesByDept);

        // Stream.Builder: add items one by one, then build
        Stream.Builder<String> builder = Stream.builder();
        builder.add("x").add("y");
        if (citiesByDept.size() > 3) builder.add("z");
        System.out.println("builder: " + builder.build().collect(Collectors.toList()));

        // flatMapToInt: from objects to a primitive stream (no boxing)
        int totalLetters = Stream.of("ab", "cde").flatMapToInt(String::chars).map(c -> 1).sum();
        int matrixSum = Arrays.stream(new int[][]{{1, 2}, {3, 4}, {5}}).flatMapToInt(Arrays::stream).sum();
        System.out.println("letters=" + totalLetters + " matrixSum=" + matrixSum);

        // Concurrent collectors: build the map in parallel safely
        ConcurrentMap<Integer, Long> byLength = Stream.of("a", "bb", "cc", "ddd").parallel()
                .collect(Collectors.groupingByConcurrent(String::length, Collectors.counting()));
        System.out.println("groupingByConcurrent: " + new java.util.TreeMap<>(byLength));

        // Prefer a stream filter over forEach + if
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6);
        nums.stream().forEach(n -> { if (n % 2 == 0) System.out.print("[if-style " + n + "] "); });
        nums.stream().filter(n -> n % 2 == 0).forEach(n -> System.out.print("[filter-style " + n + "] "));
        System.out.println();

        // Stream characteristics: ORDERED, SORTED, DISTINCT, SIZED ... streams track these to optimise.
        // e.g. calling sorted() on an already sorted source (TreeSet) can skip work.
    }

    // ------------------------------------------------------------------
    static void unorderedAndParallel() {
        System.out.println("\n--- 8. unordered(): allow faster parallel distinct / limit ---");
        // Ordered parallel streams must remember the original order. That costs time.
        // If you do NOT care about order, say so with unordered():
        long distinct = IntStream.range(0, 1_000_000).boxed().parallel()
                .unordered()
                .map(n -> n % 1000)
                .distinct()
                .count();
        System.out.println("distinct values = " + distinct);   // 1000 (same answer, can be faster)

        // With limit() the result items may differ between runs, but there are always n of them:
        long limited = IntStream.range(0, 1_000_000).boxed().parallel().unordered().limit(5).count();
        System.out.println("limit(5) gave " + limited + " items (which 5 can vary)");
    }

    // ------------------------------------------------------------------
    static void nullKeyGotcha() {
        System.out.println("\n--- 9. GOTCHA: groupingBy does not allow a null key ---");
        List<String> data = Arrays.asList("apple", null, "avocado");
        try {
            // the classifier returns null for the null element -> NullPointerException
            data.stream().collect(Collectors.groupingBy(s -> s == null ? null : s.charAt(0)));
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: element classified to a null key");
        }
        // Fix: give null a real key, e.g. s -> s == null ? '?' : s.charAt(0)
        System.out.println(data.stream().collect(Collectors.groupingBy(s -> s == null ? '?' : s.charAt(0))));
    }
}
