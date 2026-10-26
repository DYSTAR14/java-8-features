import java.util.Optional;

/**
 * LESSON 05 - Optional
 *
 * Optional<T> is a small box that holds EITHER one value OR nothing.
 * It makes "this might be missing" visible in the method signature,
 * so you stop getting surprise NullPointerExceptions.
 */
public class Lesson05_Optional {

    static class Address {
        private final String city;
        Address(String city) { this.city = city; }
        Optional<String> getCity() { return Optional.ofNullable(city); }
    }

    static class User {
        private final String name;
        private final Address address;   // may be null
        User(String name, Address address) { this.name = name; this.address = address; }
        String getName() { return name; }
        Optional<Address> getAddress() { return Optional.ofNullable(address); }
    }

    // A method that MIGHT not find the user: the return type says it honestly.
    static Optional<User> findUser(String name) {
        if (name.equals("asha")) return Optional.of(new User("Asha", new Address("Pune")));
        if (name.equals("raj"))  return Optional.of(new User("Raj", null));   // no address
        return Optional.empty();                                              // not found
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Creating an Optional ---");
        Optional<String> full = Optional.of("hello");           // value must NOT be null (else NPE)
        Optional<String> maybe = Optional.ofNullable(null);     // null is allowed -> becomes empty
        Optional<String> empty = Optional.empty();              // clearly empty
        System.out.println(full);       // Optional[hello]
        System.out.println(maybe);      // Optional.empty
        System.out.println(empty);      // Optional.empty

        System.out.println("\n--- 2. Checking and using the value ---");
        System.out.println("isPresent: " + full.isPresent());   // true
        full.ifPresent(v -> System.out.println("value is " + v)); // runs only if present
        empty.ifPresent(v -> System.out.println("never printed"));

        System.out.println("\n--- 3. Getting a value safely (avoid plain get()!) ---");
        System.out.println(empty.orElse("default value"));                  // fallback value
        System.out.println(empty.orElseGet(() -> "computed default"));      // fallback computed ONLY if needed
        try {
            empty.orElseThrow(() -> new IllegalStateException("Nothing here!"));
        } catch (IllegalStateException e) {
            System.out.println("caught: " + e.getMessage());
        }
        // empty.get() would throw NoSuchElementException. Prefer the methods above.

        System.out.println("\n--- 4. orElse vs orElseGet ---");
        // orElse(x): x is ALWAYS created, even when not needed.
        // orElseGet(supplier): the supplier runs ONLY when the Optional is empty.
        full.orElse(expensive("orElse"));              // prints "creating (orElse)" even though value exists!
        full.orElseGet(() -> expensive("orElseGet"));  // prints nothing
        System.out.println("(only 'orElse' did useless work)");

        System.out.println("\n--- 5. map, filter, flatMap ---");
        Optional<String> name = Optional.of("  Java  ");
        System.out.println(name.map(String::trim).map(String::toUpperCase).orElse("?"));   // JAVA
        System.out.println(name.map(String::trim).filter(s -> s.length() > 10).isPresent()); // false

        // flatMap: use it when your function itself returns an Optional (avoids Optional<Optional<..>>)
        String city1 = findUser("asha")
                .flatMap(User::getAddress)
                .flatMap(Address::getCity)
                .orElse("City unknown");
        String city2 = findUser("raj")                 // user exists, address is null
                .flatMap(User::getAddress)
                .flatMap(Address::getCity)
                .orElse("City unknown");
        String city3 = findUser("zed")                 // user does not exist
                .flatMap(User::getAddress)
                .flatMap(Address::getCity)
                .orElse("City unknown");
        System.out.println("asha -> " + city1);
        System.out.println("raj  -> " + city2);
        System.out.println("zed  -> " + city3);
        // The same with null-checks would need 3 nested if-statements.

        System.out.println("\n--- 6. Good habits ---");
        System.out.println("DO    : return Optional from methods that may find nothing.");
        System.out.println("DON'T : use Optional as a field, a method parameter, or in collections.");
        System.out.println("DON'T : call get() without checking. Use orElse / orElseGet / orElseThrow / ifPresent.");
        System.out.println("DON'T : return Optional<List<..>>  -> just return an empty list.");
    }

    static String expensive(String who) {
        System.out.println("  creating default (" + who + ")");
        return "default";
    }
}
