/** Java 8 flavour: a plain class whose methods are named like record accessors, so the SAME snippets compile on both. */
public class Employee {
    private final String name, department, city;
    private final double salary;
    private final int age;

    public Employee(String name, String department, double salary, int age, String city) {
        this.name = name; this.department = department; this.salary = salary; this.age = age; this.city = city;
    }
    public String name() { return name; }
    public String department() { return department; }
    public double salary() { return salary; }
    public int age() { return age; }
    public String city() { return city; }
}
