import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * LESSON 02 - Functional Interfaces (package java.util.function)
 *
 * Java 8 ships ready-made interfaces so you rarely write your own.
 * Memorise these 4 families - they power the whole Stream API:
 *
 *   Predicate<T>    : T -> boolean     "a yes/no question"
 *   Function<T,R>   : T -> R           "transform"
 *   Consumer<T>     : T -> nothing     "do something with it"
 *   Supplier<T>     : nothing -> T     "give me a value"
 *
 * JAVA 21 EDITION. The interfaces did not change after Java 8. New: Predicate.not (Java 11).
 * The original Java 8 file is in lessons-java8/02-functional-interfaces/.
 */
public class Lesson02_FunctionalInterfaces {

    public static void main(String[] args) {
        predicateDemo();
        functionDemo();
        consumerDemo();
        supplierDemo();
        operatorDemo();
        biVersionsDemo();
        primitiveVersionsDemo();
    }

    // ------------------------------------------------------------------
    static void predicateDemo() {
        System.out.println("--- Predicate<T>: T -> boolean ---");

        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isBig = n -> n > 100;

        System.out.println("isEven(4)   = " + isEven.test(4));    // true
        System.out.println("isEven(7)   = " + isEven.test(7));    // false

        // Predicates can be combined: and, or, negate
        System.out.println("even AND big (200) = " + isEven.and(isBig).test(200)); // true
        System.out.println("even OR big  (7)   = " + isEven.or(isBig).test(7));    // false
        System.out.println("NOT even     (7)   = " + isEven.negate().test(7));     // true

        // Predicate.isEqual builds a "equals this value" predicate
        System.out.println("isEqual(\"hi\")(\"hi\") = " + Predicate.isEqual("hi").test("hi"));

        // Java 11: Predicate.not(...) negates a METHOD REFERENCE.
        // Java 8 had to write a lambda:  s -> !s.isEmpty()
        Predicate<String> notEmpty = Predicate.not(String::isEmpty);
        System.out.println("notEmpty(\"\")  = " + notEmpty.test(""));        // false
        System.out.println("notEmpty(\"a\") = " + notEmpty.test("a"));       // true
    }

    // ------------------------------------------------------------------
    static void functionDemo() {
        System.out.println("\n--- Function<T,R>: T -> R ---");

        Function<String, Integer> length = s -> s.length();
        Function<Integer, Integer> doubleIt = n -> n * 2;

        System.out.println("length(\"hello\") = " + length.apply("hello")); // 5

        // andThen: first this, then that
        System.out.println("length then double = " + length.andThen(doubleIt).apply("hello")); // 10

        // compose: first that, then this (the reverse order of andThen)
        Function<Integer, Integer> plusOne = n -> n + 1;
        System.out.println("doubleIt.compose(plusOne)(5) = " + doubleIt.compose(plusOne).apply(5)); // (5+1)*2 = 12
        System.out.println("doubleIt.andThen(plusOne)(5) = " + doubleIt.andThen(plusOne).apply(5));  // 5*2+1 = 11

        // identity: returns the same thing it got (useful with collectors later)
        System.out.println("identity(\"same\") = " + Function.<String>identity().apply("same"));
    }

    // ------------------------------------------------------------------
    static void consumerDemo() {
        System.out.println("\n--- Consumer<T>: T -> void ---");

        Consumer<String> print = s -> System.out.println("print: " + s);
        Consumer<String> shout = s -> System.out.println("shout: " + s.toUpperCase());

        print.accept("hello");

        // andThen: run both, one after the other
        print.andThen(shout).accept("java");
    }

    // ------------------------------------------------------------------
    static void supplierDemo() {
        System.out.println("\n--- Supplier<T>: () -> T ---");

        Supplier<String> hello = () -> "Hello!";
        Supplier<Double> random = () -> Math.random();

        System.out.println(hello.get());
        System.out.println("random between 0 and 1: " + (random.get() < 1.0));

        // Great for "lazy" values: the code only runs when you call get().
    }

    // ------------------------------------------------------------------
    static void operatorDemo() {
        System.out.println("\n--- UnaryOperator<T> and BinaryOperator<T> ---");

        // UnaryOperator<T> is a Function<T,T>  (same type in and out)
        UnaryOperator<String> trim = s -> s.trim();
        System.out.println("[" + trim.apply("   spaces   ") + "]");

        // BinaryOperator<T> is a BiFunction<T,T,T> (two of the same type in, same type out)
        BinaryOperator<Integer> add = (a, b) -> a + b;
        System.out.println("add(3,4) = " + add.apply(3, 4));

        // Built-in helpers: minBy / maxBy
        BinaryOperator<Integer> smaller = BinaryOperator.minBy((a, b) -> a - b);
        System.out.println("minBy(8, 3) = " + smaller.apply(8, 3));
    }

    // ------------------------------------------------------------------
    static void biVersionsDemo() {
        System.out.println("\n--- Bi... versions take TWO inputs ---");

        BiPredicate<String, Integer> longerThan = (s, n) -> s.length() > n;
        BiFunction<Integer, Integer, String> describe = (a, b) -> a + " + " + b + " = " + (a + b);
        BiConsumer<String, Integer> show = (k, v) -> System.out.println(k + " -> " + v);

        System.out.println("\"banana\" longer than 3? " + longerThan.test("banana", 3));
        System.out.println(describe.apply(2, 3));
        show.accept("age", 30);
    }

    // ------------------------------------------------------------------
    static void primitiveVersionsDemo() {
        System.out.println("\n--- Primitive versions (avoid boxing = faster) ---");

        // Predicate<Integer> boxes int into Integer. IntPredicate does not.
        IntPredicate positive = n -> n > 0;
        ToIntFunction<String> lengthOf = s -> s.length();   // T -> int
        IntBinaryOperator multiply = (a, b) -> a * b;        // (int,int) -> int

        System.out.println("positive(-5)  = " + positive.test(-5));
        System.out.println("lengthOf(\"abc\") = " + lengthOf.applyAsInt("abc"));
        System.out.println("multiply(6,7) = " + multiply.applyAsInt(6, 7));

        // Naming pattern:  Int / Long / Double  +  Predicate / Function / Consumer / Supplier / UnaryOperator / BinaryOperator
        //                  ToInt / ToLong / ToDouble + Function
    }
}
