# Glossary (simple meanings)

Words are in alphabetical order.

| Word | Simple meaning |
|---|---|
| **Accumulator** | The rule in `reduce` that adds one more item to the running result. |
| **Anonymous class** | A class with no name, written on the spot. Lambdas often replace them. |
| **Associative** | `(a op b) op c` gives the same answer as `a op (b op c)`. `+` yes, `-` no. Needed for parallel `reduce`. |
| **Autoboxing / boxing** | Java automatically wraps `int` into an `Integer` object. It costs a little time and memory. |
| **Bound method reference** | `object::method`. Points to a method of one specific object (`System.out::println`). |
| **Collector** | A recipe that says how `collect` gathers stream items into a result. |
| **Combiner** | The rule that merges two partial results (used in parallel streams). |
| **Consumer** | A function that takes a value and returns nothing. |
| **Default method** | A method with a body inside an interface (keyword `default`). |
| **Downstream collector** | A collector given to another collector, for example the second argument of `groupingBy`. |
| **Effectively final** | A local variable that is never changed after it gets its value. Lambdas can use only these. |
| **Eager** | Does the work immediately. (Opposite of lazy.) |
| **Functional interface** | An interface with exactly one abstract method. A lambda can implement it. |
| **Function** | Takes one value, returns another value. |
| **Higher-order function** | A method that takes a function as input or returns a function. `map` and `filter` are examples. |
| **Identity** | A starting value that does not change the result: 0 for addition, 1 for multiplication. |
| **Immutable** | Cannot be changed after it is created (`String`, `LocalDate`). |
| **Intermediate operation** | A stream step that returns another stream (`filter`, `map`). Lazy. |
| **Lambda expression** | A short, nameless function: `x -> x * 2`. |
| **Lazy** | Does nothing until the result is really needed. |
| **Method reference** | A shortcut for a lambda that only calls one method: `String::length`. |
| **Non-interference** | Rule: do not change the stream's source while the stream is running. |
| **Optional** | A box that holds one value or nothing. |
| **Parallel stream** | A stream that uses several threads. |
| **Pipeline** | The full chain: source, intermediate operations, terminal operation. |
| **Predicate** | A function that returns `true` or `false`. |
| **Pure function** | Same input always gives the same output, and it changes nothing outside. |
| **Short-circuit** | Stops early when the answer is already known (`limit`, `anyMatch`, `findFirst`). |
| **Side effect** | A change outside the function: changing a variable, printing, writing a file. |
| **Source** | Where a stream gets its data (list, array, file, generator). |
| **Spliterator** | The helper that can split data into parts for parallel work. |
| **Stateful operation** | An operation that needs to remember other items (`sorted`, `distinct`). |
| **Stateless operation** | An operation that works on each item alone (`filter`, `map`). |
| **Stream** | A pipeline of operations over a sequence of data. It does not store data. |
| **Supplier** | A function that takes nothing and returns a value. |
| **Terminal operation** | The last step. It starts the work and ends the stream (`collect`, `forEach`, `count`). |
| **Unbound method reference** | `Class::instanceMethod`. The first parameter becomes the object (`String::length`). |
