import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LESSON 08 - Terminal Operations
 *
 * A terminal operation STARTS the pipeline and produces the final result.
 * After it, the stream is finished (closed).
 *
 *   forEach  collect  toArray  reduce  count  min  max
 *   anyMatch  allMatch  noneMatch  findFirst  findAny
 */
public class Lesson08_TerminalOperations {

    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(4, 8, 15, 16, 23, 42);
        List<Employee> staff = Sample.employees();

        System.out.println("--- forEach: do something with every item ---");
        nums.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.println("\n--- count ---");
        long evens = nums.stream().filter(n -> n % 2 == 0).count();
        System.out.println("even numbers: " + evens);   // 4  (4, 8, 16, 42)

        System.out.println("\n--- collect: gather items into a collection (details in Lesson 10) ---");
        List<String> names = staff.stream().map(Employee::getName).collect(Collectors.toList());
        System.out.println(names);

        System.out.println("\n--- toArray ---");
        String[] array = Stream.of("x", "y", "z").toArray(String[]::new);
        System.out.println(Arrays.toString(array));

        System.out.println("\n--- min and max (return Optional, because the stream may be empty) ---");
        Optional<Integer> smallest = nums.stream().min(Integer::compare);
        Optional<Employee> oldest = staff.stream().max(Comparator.comparingInt(Employee::getAge));
        System.out.println("smallest = " + smallest.get());
        System.out.println("oldest   = " + oldest.get());

        System.out.println("\n--- findFirst / findAny (Optional) ---");
        Optional<Employee> firstInParis = staff.stream()
                .filter(e -> e.getCity().equals("Paris"))
                .findFirst();
        System.out.println("first in Paris: " + firstInParis.map(Employee::getName).orElse("nobody"));
        // findAny: may return any item. It only matters in parallel streams (can be faster).

        System.out.println("\n--- anyMatch / allMatch / noneMatch (true/false questions) ---");
        System.out.println("anyone older than 44?    " + staff.stream().anyMatch(e -> e.getAge() > 44));       // true
        System.out.println("everyone earns > 50000?  " + staff.stream().allMatch(e -> e.getSalary() > 50000)); // true
        System.out.println("nobody lives in Rome?    " + staff.stream().noneMatch(e -> e.getCity().equals("Rome"))); // true
        // These are SHORT-CIRCUIT: they stop as soon as the answer is known.

        System.out.println("\n--- reduce: combine all items into ONE value ---");
        // Form 1: with identity (a starting value). Returns the value itself.
        int sum = nums.stream().reduce(0, (a, b) -> a + b);
        System.out.println("sum  = " + sum);                       // 108

        int product = Stream.of(1, 2, 3, 4).reduce(1, (a, b) -> a * b);
        System.out.println("product = " + product);                // 24

        // Form 2: no identity. Returns an Optional (stream could be empty).
        Optional<Integer> max = nums.stream().reduce(Integer::max);
        System.out.println("max  = " + max.get());                 // 42

        // Form 3: identity + accumulator + combiner (used when result type differs; see Lesson 12)
        int totalLetters = Stream.of("ab", "cde", "f")
                .reduce(0, (acc, s) -> acc + s.length(), Integer::sum);
        System.out.println("total letters = " + totalLetters);     // 6

        System.out.println("\n--- How reduce works, step by step ---");
        // reduce(0, (a,b) -> a+b) over [1,2,3]:
        //   a=0 b=1 -> 1
        //   a=1 b=2 -> 3
        //   a=3 b=3 -> 6
        int traced = Stream.of(1, 2, 3).reduce(0, (a, b) -> {
            System.out.println("  a=" + a + " b=" + b + " -> " + (a + b));
            return a + b;
        });
        System.out.println("result = " + traced);

        System.out.println("\n--- Remember: a stream can have only ONE terminal operation ---");
        Stream<Integer> once = nums.stream();
        System.out.println("count = " + once.count());
        // once.count();  // <- second terminal op would throw IllegalStateException
    }
}
