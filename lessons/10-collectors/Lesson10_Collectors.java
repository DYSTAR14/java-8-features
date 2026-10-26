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
 *
 * JAVA 21 EDITION. This lesson keeps collect(Collectors.toList()) on purpose: you must know collect(...)
 * (Java 16 added the shortcut stream.toList(), shown at the end). New in this edition:
 *   - Collectors.toUnmodifiableList (Java 10) and Collectors.teeing (Java 12)
 *   - Employee is a record: Employee::name, Employee::salary ...  (Java 16)
 * The Java 21 Javadoc says of Collectors.toList(): "There are no guarantees on the type, mutability,
 * serializability, or thread-safety of the List returned" - use toCollection(ArrayList::new) if you need one.
 * The original Java 8 file is in lessons-java8/10-collectors/.
 */
public class Lesson10_Collectors {

    public static void main(String[] args) {
        List<Employee> staff = Sample.employees();

        System.out.println("--- toList / toSet / toCollection ---");
        List<String> names = staff.stream().map(Employee::name).collect(Collectors.toList());
        Set<String> departments = staff.stream().map(Employee::department).collect(Collectors.toSet());
        TreeSet<String> sortedCities = staff.stream().map(Employee::city)
                .collect(Collectors.toCollection(TreeSet::new));   // choose the exact collection type
        System.out.println(names);
        System.out.println("departments (set, no duplicates): " + departments.size());
        System.out.println("cities sorted: " + sortedCities);

        System.out.println("\n--- joining: build one String ---");
        System.out.println(staff.stream().map(Employee::name).limit(3).collect(Collectors.joining()));
        System.out.println(staff.stream().map(Employee::name).limit(3).collect(Collectors.joining(", ")));
        System.out.println(staff.stream().map(Employee::name).limit(3).collect(Collectors.joining(", ", "[", "]")));
        //                                                         separator ^         prefix ^   suffix ^

        System.out.println("\n--- toMap: key and value from each item ---");
        Map<String, Double> salaryByName = staff.stream()
                .collect(Collectors.toMap(Employee::name, Employee::salary));
        System.out.println("Alice earns " + salaryByName.get("Alice"));

        // Keep the item itself as value: Function.identity()
        Map<String, Employee> byName = staff.stream()
                .collect(Collectors.toMap(Employee::name, Function.identity()));
        System.out.println("Bob -> " + byName.get("Bob"));

        // DUPLICATE KEYS throw IllegalStateException unless you give a merge function.
        try {
            staff.stream().collect(Collectors.toMap(Employee::city, Employee::name));
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
        // Fix 1: merge function says what to do when two items have the same key
        Map<String, String> namesByCity = staff.stream()
                .collect(Collectors.toMap(Employee::city, Employee::name, (a, b) -> a + "," + b));
        System.out.println("names by city: " + namesByCity);

        // Fix 2: pick the Map type too (LinkedHashMap keeps insertion order)
        Map<String, Double> ordered = staff.stream().limit(3).collect(
                Collectors.toMap(Employee::name, Employee::salary, (a, b) -> a, LinkedHashMap::new));
        System.out.println("ordered map: " + ordered);

        System.out.println("\n--- counting, summing, averaging ---");
        System.out.println("count   = " + staff.stream().collect(Collectors.counting()));
        System.out.println("sum age = " + staff.stream().collect(Collectors.summingInt(Employee::age)));
        System.out.println("avg sal = " + staff.stream().collect(Collectors.averagingDouble(Employee::salary)));

        System.out.println("\n--- minBy / maxBy (return Optional) ---");
        System.out.println(staff.stream()
                .collect(Collectors.maxBy(java.util.Comparator.comparingDouble(Employee::salary)))
                .orElseThrow().name());

        System.out.println("\n--- summarizingDouble: count, sum, min, avg, max together ---");
        DoubleSummaryStatistics stats = staff.stream().collect(Collectors.summarizingDouble(Employee::salary));
        System.out.println("min=" + stats.getMin() + " max=" + stats.getMax() + " avg=" + stats.getAverage());

        System.out.println("\n--- mapping: transform inside a collector ---");
        // Handy when used as a "downstream" collector (see Lesson 11)
        String upper = staff.stream().limit(3)
                .collect(Collectors.mapping(e -> e.name().toUpperCase(), Collectors.joining("|")));
        System.out.println(upper);

        System.out.println("\n--- reducing ---");
        double total = staff.stream().collect(Collectors.reducing(0.0, Employee::salary, Double::sum));
        System.out.println("total = " + total);

        System.out.println("\n--- collectingAndThen: collect, then change the result ---");
        List<String> readOnly = staff.stream().map(Employee::name)
                .collect(Collectors.collectingAndThen(Collectors.toList(), java.util.Collections::unmodifiableList));
        try {
            readOnly.add("hacker");
        } catch (UnsupportedOperationException e) {
            System.out.println("cannot modify: list is read-only");
        }

        System.out.println("\n--- Collectors added after Java 8 ---");
        // Java 10: toUnmodifiableList() = the short way to the read-only list above.
        // It also REJECTS null items (toList() accepts them).
        List<String> readOnly2 = staff.stream().map(Employee::name).limit(3).collect(Collectors.toUnmodifiableList());
        System.out.println("toUnmodifiableList: " + readOnly2);

        // Java 12: teeing = run TWO collectors over the SAME stream, then merge the two results.
        // Java 8 needed two passes (two streams) or a hand-written Collector.
        String summary = staff.stream().collect(Collectors.teeing(
                Collectors.counting(),
                Collectors.averagingDouble(Employee::salary),
                (count, avg) -> count + " people, average salary " + Math.round(avg)));
        System.out.println("teeing: " + summary);

        // Java 9: Collectors.filtering and Collectors.flatMapping - used inside groupingBy (Lesson 11).
        // Java 16: stream.toList() - the shortcut for collect(Collectors.toList()), but UNMODIFIABLE.
        List<String> shortcut = staff.stream().map(Employee::name).limit(3).toList();
        System.out.println("stream.toList():    " + shortcut);

        System.out.println("\n--- Quick word count with Collectors ---");
        Map<String, Long> wordCount = Stream.of("to", "be", "or", "not", "to", "be")
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));
        System.out.println(wordCount);   // groupingBy is explained in Lesson 11
    }
}
