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
 */
public class Lesson11_GroupingAndPartitioning {

    public static void main(String[] args) {
        List<Employee> staff = Sample.employees();

        System.out.println("--- 1. groupingBy: the basic form ---");
        // Map<key, List<items>>
        Map<String, List<Employee>> byDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
        byDept.forEach((dept, list) -> System.out.println(dept + " -> " + names(list)));

        System.out.println("\n--- 2. groupingBy + downstream collector ---");
        // Counting
        Map<String, Long> countByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        System.out.println("count:   " + countByDept);

        // Summing
        Map<String, Double> payrollByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.summingDouble(Employee::getSalary)));
        System.out.println("payroll: " + payrollByDept);

        // Averaging
        Map<String, Double> avgByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.averagingDouble(Employee::getSalary)));
        System.out.println("average: " + avgByDept);

        // mapping: keep only the names in each group
        Map<String, List<String>> namesByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.mapping(Employee::getName, Collectors.toList())));
        System.out.println("names:   " + namesByDept);

        // joining names in each group
        Map<String, String> joined = staff.stream()
                .collect(Collectors.groupingBy(Employee::getCity,
                        Collectors.mapping(Employee::getName, Collectors.joining("/"))));
        System.out.println("by city: " + joined);

        System.out.println("\n--- 3. Highest paid person in EACH department ---");
        Map<String, Optional<Employee>> topPerDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))));
        topPerDept.forEach((d, e) -> System.out.println(d + " -> " + e.get().getName()));

        // Cleaner: unwrap the Optional with collectingAndThen
        Map<String, String> topNames = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
                                opt -> opt.get().getName())));
        System.out.println("clean: " + topNames);

        System.out.println("\n--- 4. Choose the Map type (sorted keys) ---");
        // groupingBy(classifier, mapFactory, downstream)
        TreeMap<String, Long> sortedCount = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new, Collectors.counting()));
        System.out.println(sortedCount);

        System.out.println("\n--- 5. Multi-level grouping (department, then city) ---");
        Map<String, Map<String, List<String>>> nested = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.groupingBy(Employee::getCity, TreeMap::new,
                                Collectors.mapping(Employee::getName, Collectors.toList()))));
        nested.forEach((dept, cityMap) -> System.out.println(dept + " -> " + cityMap));

        System.out.println("\n--- 6. Group by a calculated key ---");
        Map<String, List<String>> bySalaryBand = staff.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getSalary() >= 100000 ? "HIGH" : e.getSalary() >= 70000 ? "MEDIUM" : "LOW",
                        TreeMap::new,
                        Collectors.mapping(Employee::getName, Collectors.toList())));
        System.out.println(bySalaryBand);

        System.out.println("\n--- 7. partitioningBy: exactly two groups (true / false) ---");
        Map<Boolean, List<String>> partition = staff.stream()
                .collect(Collectors.partitioningBy(e -> e.getAge() >= 40,
                        Collectors.mapping(Employee::getName, Collectors.toList())));
        System.out.println("40 or older: " + partition.get(true));
        System.out.println("younger:     " + partition.get(false));

        Map<Boolean, Long> partitionCount = staff.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() > 75000, Collectors.counting()));
        System.out.println("salary > 75000? " + partitionCount);
        // Difference from groupingBy: partitioningBy ALWAYS has both keys, even if a group is empty.

        System.out.println("\n--- 8. Statistics per group ---");
        Map<String, java.util.DoubleSummaryStatistics> statsByDept = staff.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.summarizingDouble(Employee::getSalary)));
        statsByDept.forEach((d, s) ->
                System.out.println(d + ": min=" + s.getMin() + " max=" + s.getMax() + " avg=" + s.getAverage()));

        System.out.println("\n--- 9. Sort a Map by value (stream over entrySet) ---");
        countByDept.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(en -> System.out.println(en.getKey() + " = " + en.getValue()));
    }

    static List<String> names(List<Employee> list) {
        return list.stream().map(Employee::getName).collect(Collectors.toList());
    }
}
