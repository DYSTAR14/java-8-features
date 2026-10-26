import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LESSON 10 - Collectors (the basics)
 *
 * stream.collect(collector) is the most powerful terminal operation.
 * A Collector describes HOW to gather stream items into a final result:
 * a List, a Set, a Map, a String, a number, statistics ...
 *
 * The helper class Collectors has ready-made collectors.
 */
public class Lesson10_Collectors {

    public static void main(String[] args) {
        List<Employee> staff = Sample.employees();

        System.out.println("--- toList / toSet / toCollection ---");
        List<String> names = staff.stream().map(Employee::getName).collect(Collectors.toList());
        Set<String> departments = staff.stream().map(Employee::getDepartment).collect(Collectors.toSet());
        TreeSet<String> sortedCities = staff.stream().map(Employee::getCity)
                .collect(Collectors.toCollection(TreeSet::new));   // choose the exact collection type
        System.out.println(names);
        System.out.println("departments (set, no duplicates): " + departments.size());
        System.out.println("cities sorted: " + sortedCities);

        System.out.println("\n--- joining: build one String ---");
        System.out.println(staff.stream().map(Employee::getName).limit(3).collect(Collectors.joining()));
        System.out.println(staff.stream().map(Employee::getName).limit(3).collect(Collectors.joining(", ")));
        System.out.println(staff.stream().map(Employee::getName).limit(3).collect(Collectors.joining(", ", "[", "]")));
        //                                                         separator ^         prefix ^   suffix ^

        System.out.println("\n--- toMap: key and value from each item ---");
        Map<String, Double> salaryByName = staff.stream()
                .collect(Collectors.toMap(Employee::getName, Employee::getSalary));
        System.out.println("Alice earns " + salaryByName.get("Alice"));

        // Keep the item itself as value: Function.identity()
        Map<String, Employee> byName = staff.stream()
                .collect(Collectors.toMap(Employee::getName, Function.identity()));
        System.out.println("Bob -> " + byName.get("Bob"));

        // DUPLICATE KEYS throw IllegalStateException unless you give a merge function.
        try {
            staff.stream().collect(Collectors.toMap(Employee::getCity, Employee::getName));
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
        // Fix 1: merge function says what to do when two items have the same key
        Map<String, String> namesByCity = staff.stream()
                .collect(Collectors.toMap(Employee::getCity, Employee::getName, (a, b) -> a + "," + b));
        System.out.println("names by city: " + namesByCity);

        // Fix 2: pick the Map type too (LinkedHashMap keeps insertion order)
        Map<String, Double> ordered = staff.stream().limit(3).collect(
                Collectors.toMap(Employee::getName, Employee::getSalary, (a, b) -> a, LinkedHashMap::new));
        System.out.println("ordered map: " + ordered);

        System.out.println("\n--- counting, summing, averaging ---");
        System.out.println("count   = " + staff.stream().collect(Collectors.counting()));
        System.out.println("sum age = " + staff.stream().collect(Collectors.summingInt(Employee::getAge)));
        System.out.println("avg sal = " + staff.stream().collect(Collectors.averagingDouble(Employee::getSalary)));

        System.out.println("\n--- minBy / maxBy (return Optional) ---");
        System.out.println(staff.stream()
                .collect(Collectors.maxBy(java.util.Comparator.comparingDouble(Employee::getSalary)))
                .get().getName());

        System.out.println("\n--- summarizingDouble: count, sum, min, avg, max together ---");
        DoubleSummaryStatistics stats = staff.stream().collect(Collectors.summarizingDouble(Employee::getSalary));
        System.out.println("min=" + stats.getMin() + " max=" + stats.getMax() + " avg=" + stats.getAverage());

        System.out.println("\n--- mapping: transform inside a collector ---");
        // Handy when used as a "downstream" collector (see Lesson 11)
        String upper = staff.stream().limit(3)
                .collect(Collectors.mapping(e -> e.getName().toUpperCase(), Collectors.joining("|")));
        System.out.println(upper);

        System.out.println("\n--- reducing ---");
        double total = staff.stream().collect(Collectors.reducing(0.0, Employee::getSalary, Double::sum));
        System.out.println("total = " + total);

        System.out.println("\n--- collectingAndThen: collect, then change the result ---");
        List<String> readOnly = staff.stream().map(Employee::getName)
                .collect(Collectors.collectingAndThen(Collectors.toList(), java.util.Collections::unmodifiableList));
        try {
            readOnly.add("hacker");
        } catch (UnsupportedOperationException e) {
            System.out.println("cannot modify: list is read-only");
        }

        System.out.println("\n--- Quick word count with Collectors ---");
        Map<String, Long> wordCount = Stream.of("to", "be", "or", "not", "to", "be")
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));
        System.out.println(wordCount);   // groupingBy is explained in Lesson 11
    }
}
