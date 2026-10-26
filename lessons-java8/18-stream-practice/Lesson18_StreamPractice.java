import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 18 - 48 Stream Practice Problems (with solutions)
 *
 * HOW TO USE:
 *   1. Read the problem list in README.md.
 *   2. Try to solve each one yourself FIRST (even if you fail, you learn).
 *   3. Then compare with the solution here.
 *
 * Problems 1-20: the employee list (see lessons/common/Sample.java)
 * Problems 21-30: numbers and text (classic interview questions)
 * Problems 31-40: more interview favourites
 * Problems 41-48: arrays and strings (quick wins)
 */
public class Lesson18_StreamPractice {

    static final List<Employee> staff = Sample.employees();

    static void show(String question, Object answer) {
        System.out.println(question);
        System.out.println("   -> " + answer);
    }

    public static void main(String[] args) {
        System.out.println("===== PART A: employees =====");

        // 1
        show("Q1  Names of Engineering employees",
                staff.stream().filter(e -> e.getDepartment().equals("Engineering"))
                        .map(Employee::getName).collect(Collectors.toList()));

        // 2
        show("Q2  Total salary of everyone",
                staff.stream().mapToDouble(Employee::getSalary).sum());

        // 3
        show("Q3  Average age",
                staff.stream().mapToInt(Employee::getAge).average().orElse(0));

        // 4
        show("Q4  Highest paid employee",
                staff.stream().max(Comparator.comparingDouble(Employee::getSalary)).get());

        // 5  distinct() first, otherwise two people with the same top salary would hide the "second"
        show("Q5  Second highest salary",
                staff.stream().map(Employee::getSalary).distinct()
                        .sorted(Comparator.reverseOrder()).skip(1).findFirst().get());

        // 6
        show("Q6  Names sorted Z to A",
                staff.stream().map(Employee::getName).sorted(Comparator.reverseOrder()).collect(Collectors.toList()));

        // 7
        show("Q7  How many employees live in each city?",
                staff.stream().collect(Collectors.groupingBy(Employee::getCity, TreeMap::new, Collectors.counting())));

        // 8
        show("Q8  Department with the biggest total payroll",
                staff.stream()
                        .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.summingDouble(Employee::getSalary)))
                        .entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).get());

        // 9
        show("Q9  Youngest employee in each department",
                staff.stream().collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparingInt(Employee::getAge)),
                                o -> o.get().getName() + "(" + o.get().getAge() + ")"))));

        // 10
        show("Q10 Names of people older than 35, youngest first, joined with commas",
                staff.stream().filter(e -> e.getAge() > 35)
                        .sorted(Comparator.comparingInt(Employee::getAge))
                        .map(Employee::getName).collect(Collectors.joining(", ")));

        // 11  streams do NOT change the original: we make a new list of numbers
        show("Q11 Sales salaries after a 10% raise (original data unchanged)",
                staff.stream().filter(e -> e.getDepartment().equals("Sales"))
                        .map(e -> e.getName() + "=" + (long) (e.getSalary() * 110 / 100))
                        .collect(Collectors.toList()));

        // 12
        double avg = staff.stream().mapToDouble(Employee::getSalary).average().orElse(0);
        show("Q12 Who earns above the company average (" + avg + ")? true=yes",
                staff.stream().collect(Collectors.partitioningBy(e -> e.getSalary() > avg,
                        Collectors.mapping(Employee::getName, Collectors.toList()))));

        // 13
        show("Q13 Does anyone in HR earn more than 100000?",
                staff.stream().filter(e -> e.getDepartment().equals("HR")).anyMatch(e -> e.getSalary() > 100000));

        // 14
        show("Q14 Unique cities, alphabetically",
                staff.stream().map(Employee::getCity).distinct().sorted().collect(Collectors.toList()));

        // 15
        show("Q15 Map of name -> age for people in London",
                staff.stream().filter(e -> e.getCity().equals("London"))
                        .collect(Collectors.toMap(Employee::getName, Employee::getAge, (a, b) -> a, TreeMap::new)));

        // 16
        show("Q16 Top 3 earners (names)",
                staff.stream().sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                        .limit(3).map(Employee::getName).collect(Collectors.toList()));

        // 17
        show("Q17 Group names by age decade",
                staff.stream().collect(Collectors.groupingBy(e -> (e.getAge() / 10 * 10) + "s", TreeMap::new,
                        Collectors.mapping(Employee::getName, Collectors.toList()))));

        // 18
        show("Q18 Average salary per department, highest first",
                staff.stream()
                        .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.averagingDouble(Employee::getSalary)))
                        .entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .map(en -> en.getKey() + "=" + Math.round(en.getValue()))
                        .collect(Collectors.toList()));

        // 19
        show("Q19 Names that start with a vowel",
                staff.stream().map(Employee::getName)
                        .filter(n -> "AEIOU".indexOf(n.charAt(0)) >= 0).collect(Collectors.toList()));

        // 20
        show("Q20 Total salary per city",
                staff.stream().collect(Collectors.groupingBy(Employee::getCity, TreeMap::new,
                        Collectors.summingDouble(Employee::getSalary))));

        System.out.println("\n===== PART B: numbers and text =====");

        // 21
        show("Q21 Sum of squares of the odd numbers from 1 to 10",
                IntStream.rangeClosed(1, 10).filter(n -> n % 2 == 1).map(n -> n * n).sum());

        // 22
        show("Q22 First 10 prime numbers",
                IntStream.iterate(2, n -> n + 1).filter(Lesson18_StreamPractice::isPrime).limit(10)
                        .boxed().collect(Collectors.toList()));

        // 23
        show("Q23 Is 'racecar' a palindrome? Is 'hello'?",
                isPalindrome("racecar") + " / " + isPalindrome("hello"));

        // 24
        show("Q24 Duplicate numbers in [1,2,3,2,4,3,5]",
                Stream.of(1, 2, 3, 2, 4, 3, 5)
                        .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()))
                        .entrySet().stream().filter(en -> en.getValue() > 1)
                        .map(Map.Entry::getKey).collect(Collectors.toList()));

        // 25
        int[] numbers = {10, 40, 30, 40, 20};
        show("Q25 Second largest number in " + Arrays.toString(numbers),
                Arrays.stream(numbers).boxed().distinct().sorted(Comparator.reverseOrder()).skip(1).findFirst().get());

        // 26
        show("Q26 Count vowels in 'functional programming'",
                "functional programming".chars().filter(c -> "aeiou".indexOf(c) >= 0).count());

        // 27
        String[] words = "java is really fun".split(" ");
        show("Q27 Reverse the order of words in 'java is really fun'",
                IntStream.range(0, words.length).mapToObj(i -> words[words.length - 1 - i])
                        .collect(Collectors.joining(" ")));

        // 28
        show("Q28 Most frequent character in 'banana'",
                "banana".chars().mapToObj(c -> (char) c)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet().stream().max(Map.Entry.comparingByValue()).get());

        // 29
        show("Q29 FizzBuzz from 1 to 15",
                IntStream.rangeClosed(1, 15)
                        .mapToObj(n -> n % 15 == 0 ? "FizzBuzz" : n % 3 == 0 ? "Fizz" : n % 5 == 0 ? "Buzz" : String.valueOf(n))
                        .collect(Collectors.joining(" ")));

        // 30  words with the same letters have the same sorted-letters key
        show("Q30 Group anagrams in [listen, silent, enlist, google, gogole, cat, act, dog]",
                Stream.of("listen", "silent", "enlist", "google", "gogole", "cat", "act", "dog")
                        .collect(Collectors.groupingBy(Lesson18_StreamPractice::sortLetters, TreeMap::new, Collectors.toList()))
                        .values().stream().filter(g -> g.size() > 1).collect(Collectors.toList()));

        System.out.println("\n===== PART C: more interview favourites =====");

        // 31
        show("Q31 The 3rd highest salary (any n works)",
                nthHighestSalary(3));

        // 32
        show("Q32 Remove duplicates but keep the original order: [3,1,3,2,1]",
                Stream.of(3, 1, 3, 2, 1).distinct().collect(Collectors.toList()));

        // 33
        List<Integer> listA = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> listB = Arrays.asList(4, 5, 6, 7);
        show("Q33 Common elements of " + listA + " and " + listB,
                listA.stream().filter(listB::contains).collect(Collectors.toList()));

        // 34
        show("Q34 Merge [5,1,3] and [3,9,1], no duplicates, sorted",
                Stream.concat(Stream.of(5, 1, 3), Stream.of(3, 9, 1)).distinct().sorted().collect(Collectors.toList()));

        // 35
        show("Q35 Longest word in 'streams make java code short'",
                Arrays.stream("streams make java code short".split(" "))
                        .max(Comparator.comparingInt(String::length)).get());

        // 36  chars are numbers: '0' is 48, '5' is 53, so c - '0' gives 0..9
        show("Q36 Sum of the digits of 12345",
                String.valueOf(12345).chars().map(c -> c - '0').sum());

        // 37
        show("Q37 How many words of each length in [a, bb, cc, ddd, e]?",
                Stream.of("a", "bb", "cc", "ddd", "e")
                        .collect(Collectors.groupingBy(String::length, TreeMap::new, Collectors.counting())));

        // 38
        List<String> fruits = Arrays.asList("apple", "banana", "cherry");
        show("Q38 Map each fruit to its index in " + fruits,
                IntStream.range(0, fruits.size()).boxed()
                        .collect(Collectors.toMap(fruits::get, Function.identity(), (a, b) -> a, TreeMap::new)));

        // 39
        int[][] matrix = {{1, 2}, {3, 4}, {5}};
        show("Q39 Sum of all numbers in the 2D array {{1,2},{3,4},{5}}",
                Arrays.stream(matrix).flatMapToInt(Arrays::stream).sum());

        // 40
        show("Q40 Min, max and average of [7, 2, 9, 4] in ONE pass",
                IntStream.of(7, 2, 9, 4).summaryStatistics());

        System.out.println("\n===== PART D: arrays and strings (quick wins) =====");

        // 41
        show("Q41 Separate odd and even numbers from 1 to 10 (true = even)",
                IntStream.rangeClosed(1, 10).boxed().collect(Collectors.partitioningBy(n -> n % 2 == 0)));

        // 42
        show("Q42 Multiples of 5 in [5, 12, 15, 20, 22, 35]",
                Stream.of(5, 12, 15, 20, 22, 35).filter(n -> n % 5 == 0).collect(Collectors.toList()));

        // 43  IntStream.concat joins two int streams (no boxing)
        int[] a = {9, 3, 7};
        int[] b = {5, 1, 3};
        show("Q43 Merge two unsorted int arrays into one sorted array (with and without duplicates)",
                Arrays.toString(IntStream.concat(Arrays.stream(a), Arrays.stream(b)).sorted().toArray())
                        + " / "
                        + Arrays.toString(IntStream.concat(Arrays.stream(a), Arrays.stream(b)).sorted().distinct().toArray()));

        // 44
        List<Integer> mixed = Arrays.asList(7, 2, 9, 4, 8, 1, 6);
        show("Q44 Three largest and three smallest numbers in " + mixed,
                mixed.stream().sorted(Comparator.reverseOrder()).limit(3).collect(Collectors.toList())
                        + " / " + mixed.stream().sorted().limit(3).collect(Collectors.toList()));

        // 45
        int[] arr = {1, 2, 3, 4, 5};
        show("Q45 Reverse the int array " + Arrays.toString(arr),
                Arrays.toString(IntStream.rangeClosed(1, arr.length).map(i -> arr[arr.length - i]).toArray()));

        // 46  (list.get(list.size() - 1) is simpler - use the stream only if you are already in a pipeline)
        show("Q46 Last element of [apple, banana, cherry]",
                Stream.of("apple", "banana", "cherry").skip(2).findFirst().get());

        // 47
        show("Q47 Reverse each word of 'java is fun'",
                Arrays.stream("java is fun".split(" "))
                        .map(w -> new StringBuilder(w).reverse().toString())
                        .collect(Collectors.joining(" ")));

        // 48
        show("Q48 Strings that start with a digit in [1apple, banana, 2pears, cherry]",
                Stream.of("1apple", "banana", "2pears", "cherry")
                        .filter(s -> Character.isDigit(s.charAt(0))).collect(Collectors.toList()));

        System.out.println("\nFinished! Did you try them before reading the answers? Come back in a week and redo them.");
    }

    /** n-th highest DISTINCT salary. */
    static double nthHighestSalary(int n) {
        return staff.stream().map(Employee::getSalary).distinct()
                .sorted(Comparator.reverseOrder()).skip(n - 1).findFirst().get();
    }

    static boolean isPrime(int n) {
        return n > 1 && IntStream.rangeClosed(2, (int) Math.sqrt(n)).noneMatch(d -> n % d == 0);
    }

    static boolean isPalindrome(String s) {
        return IntStream.range(0, s.length() / 2).allMatch(i -> s.charAt(i) == s.charAt(s.length() - 1 - i));
    }

    static String sortLetters(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
