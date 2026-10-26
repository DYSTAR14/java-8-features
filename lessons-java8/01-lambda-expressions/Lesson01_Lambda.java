import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * LESSON 01 - Lambda Expressions
 *
 * A lambda is a SHORT way to write a small function without a name.
 * Syntax:   (parameters) -> body
 */
public class Lesson01_Lambda {

    // A "functional interface" = an interface with exactly ONE abstract method.
    // A lambda can be used anywhere such an interface is expected.
    @FunctionalInterface
    interface Greeter {
        String greet(String name);
    }

    // An instance field, used in the "this" demo below.
    private final String label = "I am the outer object";

    public static void main(String[] args) {
        step1_oldWayVsNewWay();
        step2_syntaxForms();
        step3_sortingWithLambda();
        step4_capturingVariables();
        step5_ownFunctionalInterface();
        new Lesson01_Lambda().step6_thisKeyword();
    }

    // ------------------------------------------------------------------
    static void step1_oldWayVsNewWay() {
        System.out.println("--- 1. Old way vs new way ---");

        // Before Java 8: an anonymous class (5 lines for 1 idea)
        Runnable oldWay = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello from anonymous class");
            }
        };

        // Java 8: a lambda (1 line)
        Runnable newWay = () -> System.out.println("Hello from lambda");

        oldWay.run();
        newWay.run();
    }

    // ------------------------------------------------------------------
    static void step2_syntaxForms() {
        System.out.println("\n--- 2. Syntax forms ---");

        // No parameter:  ()
        Runnable noParam = () -> System.out.println("no parameter");

        // One parameter: brackets are optional
        Greeter oneParam = name -> "Hello, " + name;

        // Many parameters: brackets are needed. Types can be left out (Java guesses them).
        Comparator<Integer> twoParams = (a, b) -> a - b;

        // You may write the types if you like
        Comparator<Integer> withTypes = (Integer a, Integer b) -> b - a;

        // Block body { } : use it for many lines, and write "return" yourself
        Greeter blockBody = name -> {
            String upper = name.toUpperCase();
            return "HELLO, " + upper + "!";
        };

        // Tiny forms you will also see:
        Runnable doNothing = () -> { };                                   // no parameter, no result, empty body
        java.util.function.Supplier<Integer> answer = () -> 42;           // no parameter, returns a constant
        java.util.function.Supplier<Object> nothing = () -> null;         // returns null
        java.util.function.Function<String, Integer> typed = (String s) -> s.length();   // parameter type written
        doNothing.run();
        System.out.println("constant lambda = " + answer.get() + ", null lambda = " + nothing.get()
                + ", typed lambda = " + typed.apply("four"));

        noParam.run();
        System.out.println(oneParam.greet("Asha"));
        System.out.println("compare 3 and 5 -> " + twoParams.compare(3, 5)); // negative
        System.out.println("compare 3 and 5 (reverse) -> " + withTypes.compare(3, 5)); // positive
        System.out.println(blockBody.greet("Asha"));
    }

    // ------------------------------------------------------------------
    static void step3_sortingWithLambda() {
        System.out.println("\n--- 3. Sorting with a lambda ---");

        List<String> names = new ArrayList<>(Arrays.asList("Charlie", "Al", "Bob", "Diana"));

        // Sort by length (shortest first)
        Collections.sort(names, (a, b) -> a.length() - b.length());
        System.out.println("by length: " + names);

        // List has its own sort method in Java 8
        names.sort((a, b) -> a.compareTo(b));
        System.out.println("alphabetical: " + names);

        // forEach takes a lambda too
        names.forEach(n -> System.out.print(n + " "));
        System.out.println();
    }

    // ------------------------------------------------------------------
    static void step4_capturingVariables() {
        System.out.println("\n--- 4. Using outside variables ---");

        // A lambda can READ a local variable from outside,
        // but that variable must be "effectively final" (never changed after being set).
        String prefix = "Mr. ";
        Greeter polite = name -> prefix + name;
        System.out.println(polite.greet("Khan"));

        // prefix = "Dr. ";   // <-- if you un-comment this, the lambda above will NOT compile!

        // Trick if you really need a changing value: use a 1-element array (or AtomicInteger).
        int[] counter = {0};
        Runnable inc = () -> counter[0]++;
        inc.run();
        inc.run();
        inc.run();
        System.out.println("counter = " + counter[0]); // 3
    }

    // ------------------------------------------------------------------
    static void step5_ownFunctionalInterface() {
        System.out.println("\n--- 5. Your own functional interface ---");

        // Any interface with ONE abstract method works.
        Greeter shout = n -> n.toUpperCase() + "!!!";
        Greeter whisper = n -> n.toLowerCase() + "...";

        System.out.println(shout.greet("Maria"));
        System.out.println(whisper.greet("Maria"));
        // Big idea: we pass BEHAVIOUR (what to do) like we pass data.
    }

    // ------------------------------------------------------------------
    void step6_thisKeyword() {
        System.out.println("\n--- 6. 'this' inside a lambda ---");

        // Inside a LAMBDA, 'this' means the outer object (the class around it).
        Runnable lambda = () -> System.out.println("lambda   -> this.label = " + this.label);

        // Inside an ANONYMOUS CLASS, 'this' means the anonymous object itself.
        Runnable anonymous = new Runnable() {
            private final String label = "I am the anonymous object";
            @Override
            public void run() {
                System.out.println("anonymous-> this.label = " + this.label);
            }
        };

        lambda.run();
        anonymous.run();
    }
}
