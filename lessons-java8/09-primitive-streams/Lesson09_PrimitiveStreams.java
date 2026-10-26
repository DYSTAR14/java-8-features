import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * LESSON 09 - Primitive Streams: IntStream, LongStream, DoubleStream
 *
 * Stream<Integer> stores Integer OBJECTS (boxing = slower, more memory).
 * IntStream works with plain int values: faster, and it has helpful extras
 * like sum(), average(), range(), summaryStatistics().
 */
public class Lesson09_PrimitiveStreams {

    public static void main(String[] args) {
        List<Employee> staff = Sample.employees();

        System.out.println("--- Creating ---");
        System.out.println(Arrays.toString(IntStream.of(3, 1, 2).toArray()));          // [3, 1, 2]
        System.out.println(Arrays.toString(IntStream.range(0, 5).toArray()));          // [0, 1, 2, 3, 4]
        System.out.println(Arrays.toString(IntStream.rangeClosed(1, 5).toArray()));    // [1, 2, 3, 4, 5]
        System.out.println(Arrays.toString(Arrays.stream(new int[]{7, 8, 9}).toArray()));

        System.out.println("\n--- sum, average, min, max, count ---");
        System.out.println("sum   = " + IntStream.rangeClosed(1, 100).sum());           // 5050
        System.out.println("count = " + IntStream.rangeClosed(1, 100).count());         // 100

        // average(), min(), max() return OPTIONAL types because the stream might be empty
        OptionalDouble avg = IntStream.of(2, 4, 9).average();
        OptionalInt max = IntStream.of(2, 4, 9).max();
        System.out.println("avg   = " + avg.getAsDouble());                             // 5.0
        System.out.println("max   = " + max.getAsInt());                                // 9
        System.out.println("avg of empty = " + IntStream.empty().average().orElse(0));  // 0.0

        System.out.println("\n--- summaryStatistics: everything in ONE pass ---");
        java.util.IntSummaryStatistics stats = staff.stream().mapToInt(Employee::getAge).summaryStatistics();
        System.out.println(stats);
        System.out.println("min=" + stats.getMin() + " max=" + stats.getMax()
                + " avg=" + stats.getAverage() + " sum=" + stats.getSum() + " count=" + stats.getCount());

        System.out.println("\n--- Switching between object streams and primitive streams ---");
        // Object stream -> primitive:  mapToInt / mapToLong / mapToDouble
        double totalSalary = staff.stream().mapToDouble(Employee::getSalary).sum();
        System.out.println("total salary = " + totalSalary);

        // Primitive -> object stream:  boxed()  or  mapToObj()
        List<Integer> boxed = IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());
        List<String> labels = IntStream.rangeClosed(1, 3).mapToObj(i -> "Item-" + i).collect(Collectors.toList());
        System.out.println(boxed);
        System.out.println(labels);

        // int -> long / double
        System.out.println("asDoubleStream avg = " + IntStream.of(1, 2).asDoubleStream().average().getAsDouble());

        System.out.println("\n--- LongStream: big numbers (int would overflow) ---");
        long factorial20 = LongStream.rangeClosed(1, 20).reduce(1L, (a, b) -> a * b);
        System.out.println("20! = " + factorial20);                                      // 2432902008176640000

        System.out.println("\n--- DoubleStream ---");
        System.out.println("sum = " + DoubleStream.of(1.5, 2.5, 3.0).sum());            // 7.0

        System.out.println("\n--- Handy tricks ---");
        // Replace a classic for-loop
        IntStream.range(0, 3).forEach(i -> System.out.println("loop index " + i));

        // Characters of a String (chars() gives int codes, so cast to char)
        long vowels = "streams are fun".chars().filter(c -> "aeiou".indexOf(c) >= 0).count();
        System.out.println("vowels = " + vowels);                                        // 5

        String upperLetters = "hello".chars().mapToObj(c -> String.valueOf((char) c).toUpperCase())
                .collect(Collectors.joining("-"));
        System.out.println(upperLetters);                                                // H-E-L-L-O

        // Sum of squares 1..5 -> 55
        System.out.println("sum of squares = " + IntStream.rangeClosed(1, 5).map(n -> n * n).sum());

        // Random numbers as a stream
        System.out.println("5 dice rolls are all in 1..6? "
                + new java.util.Random().ints(5, 1, 7).allMatch(n -> n >= 1 && n <= 6));
    }
}
