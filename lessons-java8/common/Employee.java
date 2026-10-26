/**
 * A tiny data class used in many lessons.
 * Think of it as one row in a company table: name, department, salary, age, city.
 * It is "immutable": once created, nothing can change inside it.
 */
public class Employee {
    private final String name;
    private final String department;
    private final double salary;
    private final int age;
    private final String city;

    public Employee(String name, String department, double salary, int age, String city) {
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.age = age;
        this.city = city;
    }

    public String getName()       { return name; }
    public String getDepartment() { return department; }
    public double getSalary()     { return salary; }
    public int getAge()           { return age; }
    public String getCity()       { return city; }

    @Override
    public String toString() {
        return name + "(" + department + ", " + (long) salary + ", age " + age + ", " + city + ")";
    }
}
