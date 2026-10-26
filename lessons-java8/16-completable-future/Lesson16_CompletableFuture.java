import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * LESSON 16 - CompletableFuture (asynchronous programming)
 *
 * A CompletableFuture<T> is a "promise" of a value that will arrive LATER.
 * You can attach steps to it ("when it arrives, do this") without blocking a thread.
 *
 * Old Future: you could only call get() and WAIT.
 * CompletableFuture: you can chain, combine and handle errors.
 */
public class Lesson16_CompletableFuture {

    // Pretend these are slow calls (database, web service...)
    static String fetchUserName(int id) {
        sleep(300);
        return "User" + id;
    }

    static int fetchScore(String user) {
        sleep(300);
        return user.length() * 10;
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) {
        // A pool of our own threads (better than relying on the shared common pool for slow tasks)
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            basics(pool);
            chaining(pool);
            combining(pool);
            runManyAtOnce(pool);
            errorHandling(pool);
        } finally {
            pool.shutdown();   // always shut down your own pool
        }
    }

    // ------------------------------------------------------------------
    static void basics(ExecutorService pool) {
        System.out.println("--- 1. Start a task in the background ---");
        // supplyAsync = run a Supplier on another thread, get a CompletableFuture back
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> fetchUserName(1), pool);
        System.out.println("main thread keeps working while the task runs...");

        // join() waits for the result (like get(), but without checked exceptions)
        System.out.println("result = " + future.join());

        // runAsync = no result (Runnable)
        CompletableFuture.runAsync(() -> System.out.println("fire and forget task"), pool).join();

        // Already-known value
        System.out.println("completed now: " + CompletableFuture.completedFuture("ready").join());
    }

    // ------------------------------------------------------------------
    static void chaining(ExecutorService pool) {
        System.out.println("\n--- 2. Chaining steps ---");

        // thenApply   : transform the result       (like Stream.map)
        // thenAccept  : use the result, return nothing (like forEach)
        // thenCompose : next step ALSO returns a CompletableFuture (like Stream.flatMap)
        CompletableFuture<Integer> score = CompletableFuture
                .supplyAsync(() -> fetchUserName(7), pool)                              // "User7"
                .thenApply(String::toUpperCase)                                         // "USER7"
                .thenCompose(name -> CompletableFuture.supplyAsync(() -> fetchScore(name), pool));  // 50
        System.out.println("score = " + score.join());

        CompletableFuture<Void> printed = CompletableFuture
                .supplyAsync(() -> "hello", pool)
                .thenApply(s -> s + " world")
                .thenAccept(s -> System.out.println("got: " + s));
        printed.join();
        // thenApply vs thenApplyAsync: the "Async" versions run the step on a pool thread again.
    }

    // ------------------------------------------------------------------
    static void combining(ExecutorService pool) {
        System.out.println("\n--- 3. Combine two independent tasks ---");
        long start = System.currentTimeMillis();

        CompletableFuture<String> name = CompletableFuture.supplyAsync(() -> fetchUserName(2), pool);   // 300 ms
        CompletableFuture<Integer> other = CompletableFuture.supplyAsync(() -> { sleep(300); return 42; }, pool); // 300 ms

        // thenCombine waits for BOTH and merges them
        String text = name.thenCombine(other, (n, v) -> n + " has lucky number " + v).join();
        long took = System.currentTimeMillis() - start;

        System.out.println(text);
        System.out.println("took about " + (took / 100 * 100) + " ms (not 600, because they ran at the same time)");
    }

    // ------------------------------------------------------------------
    static void runManyAtOnce(ExecutorService pool) {
        System.out.println("\n--- 4. Many tasks: allOf / anyOf ---");
        List<Integer> ids = Arrays.asList(1, 2, 3, 4);

        // Start ALL tasks first (important!), then collect results.
        List<CompletableFuture<String>> futures = ids.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> fetchUserName(id), pool))
                .collect(Collectors.toList());

        // allOf: completes when ALL are done
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<String> names = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println("all results: " + names);

        // anyOf: completes when the FIRST one is done
        CompletableFuture<Object> first = CompletableFuture.anyOf(
                CompletableFuture.supplyAsync(() -> { sleep(400); return "slow"; }, pool),
                CompletableFuture.supplyAsync(() -> { sleep(50); return "fast"; }, pool));
        System.out.println("first finished: " + first.join());

        // WARNING: if you .map(...join()) inside ONE stream, tasks run one after the other:
        //   ids.stream().map(id -> supplyAsync(...)).map(CompletableFuture::join)  <- sequential!
        // because the stream is lazy and joins each future before creating the next one.
    }

    // ------------------------------------------------------------------
    static void errorHandling(ExecutorService pool) {
        System.out.println("\n--- 5. Errors ---");

        // exceptionally: provide a fallback value if something failed
        CompletableFuture<String> safe = CompletableFuture
                .<String>supplyAsync(() -> { throw new IllegalStateException("database down"); }, pool)
                .exceptionally(ex -> "fallback (" + ex.getClass().getSimpleName() + " wrapped: "
                        + ex.getCause().getMessage() + ")");
        System.out.println(safe.join());

        // handle: gets BOTH result and error (one of them is null)
        CompletableFuture<String> handled = CompletableFuture
                .supplyAsync(() -> 10 / 2, pool)
                .handle((value, ex) -> ex != null ? "failed" : "ok, value=" + value);
        System.out.println(handled.join());

        // whenComplete: peek at the outcome without changing it (good for logging)
        CompletableFuture.supplyAsync(() -> "done", pool)
                .whenComplete((v, ex) -> System.out.println("finished with " + (ex == null ? v : ex)))
                .join();

        // Without handling, join() throws CompletionException
        try {
            CompletableFuture.supplyAsync(() -> { throw new RuntimeException("boom"); }, pool).join();
        } catch (java.util.concurrent.CompletionException e) {
            System.out.println("join threw CompletionException, cause = " + e.getCause().getMessage());
        }
        // Java 8 has no built-in timeout. (orTimeout / completeOnTimeout came in Java 9.)
    }
}
