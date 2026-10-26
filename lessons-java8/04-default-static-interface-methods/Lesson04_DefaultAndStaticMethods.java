import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LESSON 04 - Default and Static Methods in Interfaces
 *
 * Before Java 8, an interface could only have abstract methods.
 * If you added a new method, EVERY class that implemented it would break.
 * Java 8 allows:
 *   - default methods : have a body, classes get them for free (and may override)
 *   - static methods  : utility methods that live inside the interface
 */
public class Lesson04_DefaultAndStaticMethods {

    // ---------- 1. A default method ----------
    interface Vehicle {
        String name();                         // abstract: every class must write this

        default String describe() {            // default: free for everyone
            return "This is a " + name();
        }
    }

    static class Car implements Vehicle {
        public String name() { return "car"; }
        // describe() is inherited. Nothing to write!
    }

    static class Bike implements Vehicle {
        public String name() { return "bike"; }

        @Override
        public String describe() {             // we may override if we want
            return "A bike has two wheels";
        }
    }

    // ---------- 2. A static method in an interface ----------
    interface MathUtil {
        static int square(int x) {
            return x * x;
        }
    }

    // ---------- 3. The "diamond problem" ----------
    interface Walker {
        default String move() { return "walking"; }
    }

    interface Swimmer {
        default String move() { return "swimming"; }
    }

    // Duck gets move() from TWO interfaces -> the compiler forces us to decide.
    static class Duck implements Walker, Swimmer {
        @Override
        public String move() {
            // Pick one, or combine both, using InterfaceName.super.method()
            return Walker.super.move() + " and " + Swimmer.super.move();
        }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Default method ---");
        Vehicle car = new Car();
        Vehicle bike = new Bike();
        System.out.println(car.describe());    // uses the default
        System.out.println(bike.describe());   // uses the override

        System.out.println("\n--- 2. Static method in interface ---");
        // Call it with the INTERFACE name (not on an object)
        System.out.println("square(7) = " + MathUtil.square(7));

        System.out.println("\n--- 3. Two defaults with the same name ---");
        System.out.println("Duck is " + new Duck().move());

        System.out.println("\n--- 4. Java itself uses this: new methods on old interfaces ---");
        // Iterable.forEach and Collection.removeIf and List.sort did not exist in Java 7.
        // They were added as default methods, so old code did not break.
        List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 12, 7, 20, 3));
        numbers.removeIf(n -> n < 6);              // Collection.removeIf (default method)
        System.out.println("after removeIf: " + numbers);
        numbers.sort(null);                        // List.sort (default method); null = natural order
        System.out.println("after sort:     " + numbers);
        numbers.forEach(n -> System.out.print(n + " "));   // Iterable.forEach (default method)
        System.out.println();
    }
}
