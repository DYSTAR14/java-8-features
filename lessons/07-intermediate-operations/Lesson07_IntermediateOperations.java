import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 07 - Intermediate Operations
 *
 * They take a stream and return a NEW stream, so you can chain them.
 * They are lazy: nothing runs until a terminal operation is called.
 *
 *   filter  map  mapToInt  flatMap  distinct  sorted  limit  skip  peek  boxed
 *
 * JAVA 21 EDITION. These operations did not change after Java 8. What changed here:
 *   - takeWhile / dropWhile were added (Java 9)           - see the new section below
 *   - Employee is a record, so we write Employee::name instead of Employee::getName (Java 16)
 *   - Stream.toList() instead of collect(Collectors.toList())  (Java 16)
 * The original Java 8 file is in lessons-java8/07-intermediate-operations/.
 */
public class Lesson07_IntermediateOperations {

    public static void main(String[] args) {
        List<Employee> staff = Sample.employees();

        System.out.println("--- filter: keep items that pass a test (Predicate) ---");
        List<String> richPeople = staff.stream()
                .filter(e -> e.salary() > 90000)
                .map(Employee::name)
                .toList();
        System.out.println(richPeople);   // [Alice, Bob, Frank]

        System.out.println("\n--- map: transform each item (Function) ---");
        List<Integer> nameLengths = Stream.of("kiwi", "banana", "fig")
                .map(String::length)
                .toList();
        System.out.println(nameLengths);  // [4, 6, 3]

        System.out.println("\n--- mapToInt / mapToDouble: transform into a primitive stream ---");
        double totalSalary = staff.stream().mapToDouble(Employee::salary).sum();
        System.out.println("total salary = " + totalSalary);

        System.out.println("\n--- distinct: remove duplicates (uses equals) ---");
        List<String> cities = staff.stream()
                .map(Employee::city)
                .distinct()
                .toList();
        System.out.println(cities);       // [London, Paris, Berlin]

        System.out.println("\n--- sorted: natural order, or with a Comparator ---");
        System.out.println(Stream.of(5, 3, 9, 1).sorted().toList());                     // [1, 3, 5, 9]
        System.out.println(Stream.of(5, 3, 9, 1).sorted(Comparator.reverseOrder()).toList()); // [9, 5, 3, 1]

        List<String> bySalaryDesc = staff.stream()
                .sorted(Comparator.comparingDouble(Employee::salary).reversed())
                .map(e -> e.name() + ":" + (long) e.salary())
                .toList();
        System.out.println(bySalaryDesc);

        // Sort by department, then by salary (highest first) inside each department
        List<String> twoLevel = staff.stream()
                .sorted(Comparator.comparing(Employee::department)
                        .thenComparing(Employee::salary, Comparator.reverseOrder()))
                .map(e -> e.department() + "/" + e.name())
                .toList();
        System.out.println(twoLevel);

        System.out.println("\n--- limit and skip: paging ---");
        List<Integer> page = IntStream.rangeClosed(1, 20).boxed()
                .skip(5)      // drop first 5
                .limit(5)     // then take 5
                .toList();
        System.out.println("page 2 (size 5): " + page);   // [6, 7, 8, 9, 10]

        System.out.println("\n--- takeWhile and dropWhile (Java 9): stop or skip when a test fails ---");
        List<Integer> mixed = List.of(1, 2, 3, 10, 11, 4);
        // takeWhile: take items UNTIL the test fails, then STOP.   (Java 8 had no direct way.)
        System.out.println("takeWhile(<5): " + mixed.stream().takeWhile(n -> n < 5).toList());   // [1, 2, 3]
        // dropWhile: skip items WHILE the test is true, then keep EVERYTHING after.
        System.out.println("dropWhile(<5): " + mixed.stream().dropWhile(n -> n < 5).toList());   // [10, 11, 4]
        // filter(<5) would give [1, 2, 3, 4]: filter looks at EVERY item and never stops.
        System.out.println("filter(<5):    " + mixed.stream().filter(n -> n < 5).toList());

        System.out.println("\n--- flatMap: one item -> many items, then flatten ---");
        List<List<Integer>> listOfLists = List.of(
                List.of(1, 2), List.of(3, 4), List.of(5));
        List<Integer> flat = listOfLists.stream()
                .flatMap(List::stream)             // each inner list becomes a stream, then all are merged
                .toList();
        System.out.println("flatten: " + flat);    // [1, 2, 3, 4, 5]

        List<String> sentences = List.of("hello world", "java streams");
        List<String> allWords = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .toList();
        System.out.println("words: " + allWords);  // [hello, world, java, streams]
        // Compare: map would give Stream<String[]> - flatMap gives Stream<String>.

        System.out.println("\n--- peek: look at items while they pass (for debugging only) ---");
        List<Integer> peeked = Stream.of(1, 2, 3, 4)
                .peek(n -> System.out.println("  before filter: " + n))
                .filter(n -> n % 2 == 0)
                .peek(n -> System.out.println("  after filter:  " + n))
                .toList();
        System.out.println(peeked);

        System.out.println("\n--- IMPORTANT: items flow ONE BY ONE through the whole pipeline ---");
        // You might expect "all filters, then all maps". It is NOT like that:
        Stream.of("a", "bb", "ccc")
                .filter(s -> { System.out.println("  filter " + s); return s.length() > 1; })
                .map(s -> { System.out.println("  map    " + s); return s.toUpperCase(); })
                .forEach(s -> System.out.println("  result " + s));
        // 'a' stops at filter. 'bb' goes all the way. THEN 'ccc' starts.

        System.out.println("\n--- short-circuit: limit stops the work early ---");
        Stream.of(10, 20, 30, 40, 50)
                .peek(n -> System.out.println("  visiting " + n))
                .limit(2)
                .forEach(n -> { });                // only 10 and 20 are ever visited
    }
}
