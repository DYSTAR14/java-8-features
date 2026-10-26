import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * LESSON 11 - groupingBy and partitioningBy
 *
 * groupingBy      = like SQL "GROUP BY": split items into groups by a key.
 * partitioningBy  = split into exactly TWO groups: true and false.
 *
 * The real power: the SECOND argument ("downstream collector") says
 * what to do with the items inside each group.
 *
 * JAVA 21 EDITION. groupingBy and partitioningBy did not change after Java 8. What changed here:
 *   - Collectors.filtering (Java 9): filter INSIDE each group - see section 2b
 *   - Employee is a record: Employee::department, Employee::salary ...  (Java 16)
 * The original Java 8 file is in lessons-java8/11-grouping-and-partitioning/.
 */
public class Lesson11_GroupingAndPartitioning {

    public static void main(String[] args) {
        List<Employee> staff = Sample.employees();

        System.out.println("--- 1. groupingBy: the basic form ---");
        // Map<key, List<items>>
        Map<String, List<Employee>> byDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::department));
        byDept.forEach((dept, list) -> System.out.println(dept + " -> " + names(list)));

        System.out.println("\n--- 2. groupingBy + downstream collector ---");
        // Counting
        Map<String, Long> countByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
        System.out.println("count:   " + countByDept);

        // Summing
        Map<String, Double> payrollByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.summingDouble(Employee::salary)));
        System.out.println("payroll: " + payrollByDept);

        // Averaging
        Map<String, Double> avgByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.averagingDouble(Employee::salary)));
        System.out.println("average: " + avgByDept);

        // mapping: keep only the names in each group
        Map<String, List<String>> namesByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.mapping(Employee::name, Collectors.toList())));
        System.out.println("names:   " + namesByDept);

        // joining names in each group
        Map<String, String> joined = staff.stream()
                .collect(Collectors.groupingBy(Employee::city,
                        Collectors.mapping(Employee::name, Collectors.joining("/"))));
        System.out.println("by city: " + joined);

        System.out.println("\n--- 2b. filtering INSIDE each group (Java 9) ---");
        // Java 8: filter BEFORE groupingBy. Groups that become empty disappear completely.
        Map<String, List<String>> filterFirst = staff.stream()
                .filter(e -> e.salary() > 100000)
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.mapping(Employee::name, Collectors.toList())));
        // Java 9: Collectors.filtering filters INSIDE each group, so every department still appears.
        Map<String, List<String>> filterInside = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.filtering(e -> e.salary() > 100000,
                                Collectors.mapping(Employee::name, Collectors.toList()))));
        System.out.println("filter first:  " + filterFirst);
        System.out.println("filtering():   " + filterInside);

        System.out.println("\n--- 3. Highest paid person in EACH department ---");
        Map<String, Optional<Employee>> topPerDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::salary))));
        topPerDept.forEach((d, e) -> System.out.println(d + " -> " + e.orElseThrow().name()));

        // Cleaner: unwrap the Optional with collectingAndThen
        Map<String, String> topNames = staff.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::salary)),
                                opt -> opt.orElseThrow().name())));
        System.out.println("clean: " + topNames);

        System.out.println("\n--- 4. Choose the Map type (sorted keys) ---");
        // groupingBy(classifier, mapFactory, downstream)
        TreeMap<String, Long> sortedCount = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.counting()));
        System.out.println(sortedCount);

        System.out.println("\n--- 5. Multi-level grouping (department, then city) ---");
        Map<String, Map<String, List<String>>> nested = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.groupingBy(Employee::city, TreeMap::new,
                                Collectors.mapping(Employee::name, Collectors.toList()))));
        nested.forEach((dept, cityMap) -> System.out.println(dept + " -> " + cityMap));

        System.out.println("\n--- 6. Group by a calculated key ---");
        Map<String, List<String>> bySalaryBand = staff.stream()
                .collect(Collectors.groupingBy(
                        e -> e.salary() >= 100000 ? "HIGH" : e.salary() >= 70000 ? "MEDIUM" : "LOW",
                        TreeMap::new,
                        Collectors.mapping(Employee::name, Collectors.toList())));
        System.out.println(bySalaryBand);

        System.out.println("\n--- 7. partitioningBy: exactly two groups (true / false) ---");
        Map<Boolean, List<String>> partition = staff.stream()
                .collect(Collectors.partitioningBy(e -> e.age() >= 40,
                        Collectors.mapping(Employee::name, Collectors.toList())));
        System.out.println("40 or older: " + partition.get(true));
        System.out.println("younger:     " + partition.get(false));

        Map<Boolean, Long> partitionCount = staff.stream()
                .collect(Collectors.partitioningBy(e -> e.salary() > 75000, Collectors.counting()));
        System.out.println("salary > 75000? " + partitionCount);
        // Difference from groupingBy: partitioningBy ALWAYS has both keys, even if a group is empty.

        System.out.println("\n--- 8. Statistics per group ---");
        Map<String, java.util.DoubleSummaryStatistics> statsByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.summarizingDouble(Employee::salary)));
        statsByDept.forEach((d, s) ->
                System.out.println(d + ": min=" + s.getMin() + " max=" + s.getMax() + " avg=" + s.getAverage()));

        System.out.println("\n--- 9. Sort a Map by value (stream over entrySet) ---");
        countByDept.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(en -> System.out.println(en.getKey() + " = " + en.getValue()));
    }

    static List<String> names(List<Employee> list) {
        return list.stream().map(Employee::name).toList();
    }
}
