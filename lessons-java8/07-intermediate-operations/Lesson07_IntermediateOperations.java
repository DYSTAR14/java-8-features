import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 07 - Intermediate Operations
 *
 * They take a stream and return a NEW stream, so you can chain them.
 * They are lazy: nothing runs until a terminal operation is called.
 *
 *   filter  map  mapToInt  flatMap  distinct  sorted  limit  skip  peek  boxed
 */
public class Lesson07_IntermediateOperations {

    public static void main(String[] args) {
        List<Employee> staff = Sample.employees();

        System.out.println("--- filter: keep items that pass a test (Predicate) ---");
        List<String> richPeople = staff.stream()
                .filter(e -> e.getSalary() > 90000)
                .map(Employee::getName)
                .collect(Collectors.toList());
        System.out.println(richPeople);   // [Alice, Bob, Frank]

        System.out.println("\n--- map: transform each item (Function) ---");
        List<Integer> nameLengths = Stream.of("kiwi", "banana", "fig")
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println(nameLengths);  // [4, 6, 3]

        System.out.println("\n--- mapToInt / mapToDouble: transform into a primitive stream ---");
        double totalSalary = staff.stream().mapToDouble(Employee::getSalary).sum();
        System.out.println("total salary = " + totalSalary);

        System.out.println("\n--- distinct: remove duplicates (uses equals) ---");
        List<String> cities = staff.stream()
                .map(Employee::getCity)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(cities);       // [London, Paris, Berlin]

        System.out.println("\n--- sorted: natural order, or with a Comparator ---");
        System.out.println(Stream.of(5, 3, 9, 1).sorted().collect(Collectors.toList()));                     // [1, 3, 5, 9]
        System.out.println(Stream.of(5, 3, 9, 1).sorted(Comparator.reverseOrder()).collect(Collectors.toList())); // [9, 5, 3, 1]

        List<String> bySalaryDesc = staff.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .map(e -> e.getName() + ":" + (long) e.getSalary())
                .collect(Collectors.toList());
        System.out.println(bySalaryDesc);

        // Sort by department, then by salary (highest first) inside each department
        List<String> twoLevel = staff.stream()
                .sorted(Comparator.comparing(Employee::getDepartment)
                        .thenComparing(Employee::getSalary, Comparator.reverseOrder()))
                .map(e -> e.getDepartment() + "/" + e.getName())
                .collect(Collectors.toList());
        System.out.println(twoLevel);

        System.out.println("\n--- limit and skip: paging ---");
        List<Integer> page = IntStream.rangeClosed(1, 20).boxed()
                .skip(5)      // drop first 5
                .limit(5)     // then take 5
                .collect(Collectors.toList());
        System.out.println("page 2 (size 5): " + page);   // [6, 7, 8, 9, 10]

        System.out.println("\n--- flatMap: one item -> many items, then flatten ---");
        List<List<Integer>> listOfLists = Arrays.asList(
                Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5));
        List<Integer> flat = listOfLists.stream()
                .flatMap(List::stream)             // each inner list becomes a stream, then all are merged
                .collect(Collectors.toList());
        System.out.println("flatten: " + flat);    // [1, 2, 3, 4, 5]

        List<String> sentences = Arrays.asList("hello world", "java streams");
        List<String> allWords = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .collect(Collectors.toList());
        System.out.println("words: " + allWords);  // [hello, world, java, streams]
        // Compare: map would give Stream<String[]> - flatMap gives Stream<String>.

        System.out.println("\n--- peek: look at items while they pass (for debugging only) ---");
        List<Integer> peeked = Stream.of(1, 2, 3, 4)
                .peek(n -> System.out.println("  before filter: " + n))
                .filter(n -> n % 2 == 0)
                .peek(n -> System.out.println("  after filter:  " + n))
                .collect(Collectors.toList());
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
