import java.util.Arrays;
import java.util.List;

/** Ready-made sample data, so every lesson starts with the same simple data. */
public class Sample {

    /** 10 employees. Same data in every lesson, so results are easy to compare. */
    public static List<Employee> employees() {
        return Arrays.asList(
            new Employee("Alice", "Engineering", 120000, 30, "London"),
            new Employee("Bob",   "Engineering",  95000, 45, "Paris"),
            new Employee("Carol", "Sales",        70000, 28, "London"),
            new Employee("Dave",  "Sales",        65000, 35, "Berlin"),
            new Employee("Eve",   "HR",           60000, 40, "Paris"),
            new Employee("Frank", "Engineering", 105000, 38, "Berlin"),
            new Employee("Grace", "HR",           58000, 26, "London"),
            new Employee("Heidi", "Marketing",    72000, 33, "Paris"),
            new Employee("Ivan",  "Marketing",    68000, 29, "Berlin"),
            new Employee("Judy",  "Sales",        80000, 42, "London")
        );
    }

    /** A few words, handy for String examples. */
    public static List<String> words() {
        return Arrays.asList("apple", "banana", "cherry", "date", "elderberry", "fig", "grape");
    }
}
