import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * LESSON 03 - Method References
 *
 * A method reference is a shortcut for a lambda that ONLY calls one existing method.
 * Syntax:   ClassOrObject :: methodName     (no brackets after the method name!)
 */
public class Lesson03_MethodReferences {

    // A small helper method we can point to.
    static boolean isLong(String s) {
        return s.length() > 4;
    }

    // A small helper class for the constructor demo.
    static class Person {
        final String name;
        Person(String name) { this.name = name; }
        Person()            { this.name = "Unknown"; }
        @Override public String toString() { return "Person(" + name + ")"; }
    }

    public static void main(String[] args) {
        List<String> words = Arrays.asList("kiwi", "banana", "fig", "cherry");

        // ---------- Kind 1: STATIC method  ->  ClassName::staticMethod ----------
        System.out.println("--- 1. Static method reference ---");
        // lambda:  s -> Lesson03_MethodReferences.isLong(s)
        words.stream().filter(Lesson03_MethodReferences::isLong).forEach(System.out::println);

        Function<String, Integer> parse = Integer::parseInt;   // s -> Integer.parseInt(s)
        System.out.println("parse(\"42\") + 1 = " + (parse.apply("42") + 1));

        // ---------- Kind 2: method of a SPECIFIC object  ->  object::method ----------
        System.out.println("\n--- 2. Bound (specific object) reference ---");
        String prefix = "Item: ";
        Function<String, String> addPrefix = prefix::concat;   // s -> prefix.concat(s)
        System.out.println(addPrefix.apply("pen"));

        // System.out is an object too, so System.out::println is this kind.
        words.forEach(System.out::println);                    // s -> System.out.println(s)

        // ---------- Kind 3: method of ANY object of a type  ->  ClassName::instanceMethod ----------
        System.out.println("\n--- 3. Unbound (any object of a class) reference ---");
        // The first parameter becomes the object the method is called on.
        Function<String, Integer> len = String::length;        // s -> s.length()
        System.out.println("length of banana = " + len.apply("banana"));

        BiFunction<String, String, Boolean> startsWith = String::startsWith; // (a, b) -> a.startsWith(b)
        System.out.println("banana startsWith ban? " + startsWith.apply("banana", "ban"));

        // Sorting with a method reference
        List<String> sorted = new ArrayList<>(words);
        sorted.sort(String::compareToIgnoreCase);              // (a, b) -> a.compareToIgnoreCase(b)
        System.out.println("sorted: " + sorted);

        // ---------- Kind 4: CONSTRUCTOR  ->  ClassName::new ----------
        System.out.println("\n--- 4. Constructor reference ---");
        Function<String, Person> create = Person::new;         // name -> new Person(name)
        Supplier<Person> createEmpty = Person::new;            // () -> new Person()   (Java picks the right constructor)
        System.out.println(create.apply("Asha"));
        System.out.println(createEmpty.get());

        // Array constructor:  int[]::new   (size -> new int[size])
        IntFunction<int[]> makeArray = int[]::new;
        System.out.println("array length = " + makeArray.apply(5).length);

        // Very common in streams: toArray(String[]::new)
        String[] asArray = words.stream().toArray(String[]::new);
        System.out.println("as array: " + Arrays.toString(asArray));

        // Very common too: map(Person::new)
        List<Person> people = words.stream().map(Person::new).collect(java.util.stream.Collectors.toList());
        System.out.println(people);
    }
}
