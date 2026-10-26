import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * LESSON 13 - Parallel Streams
 *
 * A parallel stream splits the data into chunks, processes them on several
 * CPU cores at the same time, and joins the results.
 *
 *    list.stream()          -> one thread
 *    list.parallelStream()  -> many threads (ForkJoin common pool)
 *
 * Parallel is NOT automatically faster. Measure first!
 *
 * JAVA 21 EDITION. The parallel stream API did not change after Java 8, so the lesson is the same.
 * Only Stream.toList() (Java 16) replaced collect(Collectors.toList()).
 * Java 21 added virtual threads (JEP 444). Official guidance: the Stream API "remains the preferred way to
 * process large data sets in parallel"; virtual threads are for MANY tasks that WAIT (I/O). See modern/03.
 * The original Java 8 file is in lessons-java8/13-parallel-streams/.
 */
public class Lesson13_ParallelStreams {

    public static void main(String[] args) {
        System.out.println("CPU cores available: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Common pool parallelism: " + ForkJoinPool.commonPool().getParallelism());

        howToUse();
        orderDemo();
        speedDemo();
        pitfallSharedState();
        pitfallNonAssociative();
        whenToUse();
    }

    // ------------------------------------------------------------------
    static void howToUse() {
        System.out.println("\n--- 1. How to switch ---");
        System.out.println("stream().isParallel()         = " + IntStream.range(0, 5).isParallel());
        System.out.println("parallel().isParallel()       = " + IntStream.range(0, 5).parallel().isParallel());
        System.out.println("parallelStream().isParallel() = " + new ArrayList<Integer>().parallelStream().isParallel());
        // .parallel() / .sequential() can be placed anywhere; the LAST one wins for the WHOLE pipeline.

        // The result is the same, only the way it is computed changes
        System.out.println("sum sequential = " + IntStream.rangeClosed(1, 1000).sum());
        System.out.println("sum parallel   = " + IntStream.rangeClosed(1, 1000).parallel().sum());
    }

    // ------------------------------------------------------------------
    static void orderDemo() {
        System.out.println("\n--- 2. Order ---");
        List<Integer> data = IntStream.rangeClosed(1, 8).boxed().toList();

        System.out.print("forEach        (order may be mixed): ");
        data.parallelStream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.print("forEachOrdered (always in order)   : ");
        data.parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();

        // collect() and toList keep the original order even in parallel
        System.out.println("collect keeps order: " + data.parallelStream().map(n -> n * 10).toList());
    }

    // ------------------------------------------------------------------
    static void speedDemo() {
        System.out.println("\n--- 3. Speed (numbers vary per computer) ---");
        long n = 200_000_000L;

        long t1 = System.nanoTime();
        long s1 = LongStream.rangeClosed(1, n).filter(x -> x % 3 == 0).sum();
        long ms1 = (System.nanoTime() - t1) / 1_000_000;

        long t2 = System.nanoTime();
        long s2 = LongStream.rangeClosed(1, n).parallel().filter(x -> x % 3 == 0).sum();
        long ms2 = (System.nanoTime() - t2) / 1_000_000;

        System.out.println("sequential: " + ms1 + " ms (sum=" + s1 + ")");
        System.out.println("parallel:   " + ms2 + " ms (sum=" + s2 + ")");
        System.out.println("same answer? " + (s1 == s2));
        // For tiny data, parallel is usually SLOWER: splitting threads has a cost.
    }

    // ------------------------------------------------------------------
    static void pitfallSharedState() {
        System.out.println("\n--- 4. DANGER: changing shared variables ---");

        // BAD: many threads write to one plain int -> some updates are lost
        int[] unsafeCounter = {0};
        IntStream.range(0, 100_000).parallel().forEach(i -> unsafeCounter[0]++);
        System.out.println("unsafe counter (expected 100000): " + unsafeCounter[0] + "   <- usually WRONG");

        // BAD: ArrayList is not thread-safe -> may lose items or even crash
        // GOOD options:
        AtomicInteger safeCounter = new AtomicInteger();
        IntStream.range(0, 100_000).parallel().forEach(i -> safeCounter.incrementAndGet());
        System.out.println("atomic counter (expected 100000): " + safeCounter.get());

        // BEST: do not share state at all. Let the stream calculate and return.
        long count = IntStream.range(0, 100_000).parallel().count();
        System.out.println("count()        (expected 100000): " + count);

        List<Integer> safeList = IntStream.range(0, 1000).parallel().boxed().toList();
        System.out.println("collect() size (expected 1000)  : " + safeList.size());
    }

    // ------------------------------------------------------------------
    static void pitfallNonAssociative() {
        System.out.println("\n--- 5. DANGER: reduce needs an ASSOCIATIVE operation ---");
        // Subtraction is not associative: (a-b)-c != a-(b-c)
        int seq = IntStream.rangeClosed(1, 100).reduce(0, (a, b) -> a - b);
        int par = IntStream.rangeClosed(1, 100).parallel().reduce(0, (a, b) -> a - b);
        System.out.println("sequential minus: " + seq);
        System.out.println("parallel minus:   " + par + "   <- different! (usually)");
        // Addition, multiplication, min, max, string concat are associative: safe.
    }

    // ------------------------------------------------------------------
    static void whenToUse() {
        System.out.println("\n--- 6. When should I use parallel? ---");
        System.out.println("USE when:   a LOT of items (10,000+), each item needs real CPU work,");
        System.out.println("            source splits well (ArrayList, array, IntStream.range),");
        System.out.println("            operations are independent, no shared state.");
        System.out.println("AVOID when: small data, quick operations, LinkedList / Stream.iterate sources,");
        System.out.println("            I/O or blocking calls (they block the shared common pool),");
        System.out.println("            order-heavy operations (limit, findFirst, forEachOrdered).");
        System.out.println("ALWAYS:     measure with a real benchmark (e.g. JMH) before deciding.");
    }
}
