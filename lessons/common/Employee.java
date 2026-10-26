/**
 * A tiny data class used in many lessons.
 * Think of it as one row in a company table: name, department, salary, age, city.
 *
 * JAVA 21 EDITION: this is a "record" (Java 16).
 * The compiler writes the constructor, the accessors (name(), salary() ...),
 * equals(), hashCode() and toString() for us. A record is immutable.
 *
 * Java 8 way (see lessons-java8/common/Employee.java): a normal class with private final fields,
 * a constructor, getName() / getSalary() ... and a hand-written toString() - about 30 lines.
 */
public record Employee(String name, String department, double salary, int age, String city) {
}
