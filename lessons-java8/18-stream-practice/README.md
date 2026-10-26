# Lesson 18 - 48 Stream Practice Problems

> **Level:** All levels (this is how you become "pro")  |  **Run it:** `.\run-java8.ps1 18` / `./run-java8.sh 18`

Reading is not enough. **You learn streams by writing streams.**

## How to practise

1. Copy the problem. **Do not look at the solution.**
2. Write your own answer in a new file (e.g. `MyAnswers.java`). Use `Sample.employees()` for data.
3. Run it. Then compare with `Lesson18_StreamPractice.java`.
4. If your answer is different but gives the same result - great! There are many right ways.
5. Redo the ones you failed after a few days.

## Part A - Employees (Problems 1-20)

Data: 10 employees with `name`, `department`, `salary`, `age`, `city` (see [Sample.java](../common/Sample.java)).

| # | Problem | Main tools |
|---|---|---|
| 1 | Names of Engineering employees | `filter`, `map`, `collect` |
| 2 | Total salary of everyone | `mapToDouble`, `sum` |
| 3 | Average age | `mapToInt`, `average` |
| 4 | Highest paid employee | `max` + `Comparator` |
| 5 | Second highest salary | `distinct`, `sorted`, `skip`, `findFirst` |
| 6 | Names sorted Z to A | `sorted(reverseOrder())` |
| 7 | Employees per city | `groupingBy`, `counting` |
| 8 | Department with the biggest payroll | `groupingBy`, `summingDouble`, `max` on entries |
| 9 | Youngest employee in each department | `groupingBy`, `minBy`, `collectingAndThen` |
| 10 | Names of people older than 35, youngest first, joined by commas | `filter`, `sorted`, `joining` |
| 11 | Sales salaries after a 10% raise (do not change the originals) | `filter`, `map` |
| 12 | Who earns above the company average? | `partitioningBy` |
| 13 | Does anyone in HR earn more than 100000? | `anyMatch` |
| 14 | Unique cities, alphabetically | `distinct`, `sorted` |
| 15 | Map of name -> age for people in London | `toMap` |
| 16 | Top 3 earners | `sorted`, `limit` |
| 17 | Group names by age decade (20s, 30s, 40s) | `groupingBy` with calculated key |
| 18 | Average salary per department, highest first | `groupingBy`, `averagingDouble`, sort entries |
| 19 | Names that start with a vowel | `filter`, `indexOf` |
| 20 | Total salary per city | `groupingBy`, `summingDouble` |

## Part B - Numbers and text (Problems 21-30)

Classic interview questions.

| # | Problem | Main tools |
|---|---|---|
| 21 | Sum of squares of the odd numbers 1..10 | `IntStream.rangeClosed`, `filter`, `map`, `sum` |
| 22 | First 10 prime numbers | `IntStream.iterate`, `filter`, `limit`, `noneMatch` |
| 23 | Is a word a palindrome? | `IntStream.range`, `allMatch` |
| 24 | Find duplicates in `[1,2,3,2,4,3,5]` | `groupingBy`, `counting` |
| 25 | Second largest number in an `int[]` | `distinct`, `sorted`, `skip` |
| 26 | Count vowels in a sentence | `chars`, `filter`, `count` |
| 27 | Reverse the order of words in a sentence | `IntStream.range`, `joining` |
| 28 | Most frequent character in a word | `groupingBy`, `max` on entries |
| 29 | FizzBuzz 1..15 | `mapToObj`, `joining` |
| 30 | Group anagrams | `groupingBy` with a calculated key |

## Part C - More interview favourites (Problems 31-40)

| # | Problem | Main tools |
|---|---|---|
| 31 | The n-th highest salary (try n = 3) | `distinct`, `sorted`, `skip(n-1)`, `findFirst` |
| 32 | Remove duplicates, keep the original order | `distinct` |
| 33 | Common elements of two lists | `filter` + `contains` |
| 34 | Merge two lists, no duplicates, sorted | `Stream.concat`, `distinct`, `sorted` |
| 35 | Longest word in a sentence | `max` + `Comparator.comparingInt` |
| 36 | Sum of the digits of a number | `chars`, `map(c -> c - '0')`, `sum` |
| 37 | How many words of each length? | `groupingBy`, `counting` |
| 38 | Map each item of a list to its index | `IntStream.range`, `toMap` |
| 39 | Sum of all numbers in a 2D array | `flatMapToInt(Arrays::stream)` |
| 40 | Min, max, average in one pass | `summaryStatistics` |

## Part D - Arrays and strings, quick wins (Problems 41-48)

| # | Problem | Main tools |
|---|---|---|
| 41 | Separate odd and even numbers | `partitioningBy` |
| 42 | Multiples of 5 in a list | `filter` |
| 43 | Merge two unsorted int arrays into one sorted array | `IntStream.concat`, `sorted`, `distinct` |
| 44 | Three largest and three smallest numbers | `sorted`, `limit(3)` |
| 45 | Reverse an int array | `IntStream.rangeClosed`, `map`, `toArray` |
| 46 | Last element of a list | `skip(size - 1)`, `findFirst` |
| 47 | Reverse **each word** of a sentence | `split`, `StringBuilder.reverse`, `joining` |
| 48 | Strings that start with a digit | `filter` + `Character.isDigit` |

## Bonus challenges (no solutions - invent your own!)

- B1. For every department, print the name of its **oldest** employee and the **gap** between the highest and lowest salary.
- B2. Make a `Map<String, Map<String, Double>>`: city -> department -> average salary.
- B3. Given a sentence, print the **top 3 longest distinct words** (case-insensitive).
- B4. Given `List<List<Integer>>`, find the sum of all **even** numbers inside.
- B5. Implement your own `Collector` that returns the min and max of a stream as `[min, max]`.
- B6. Solve Q1-Q10 again, but using **only method references** where possible.

## Official sources

- Stream API: <https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html>
- Collectors: <https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html>

**Previous:** [Lesson 17](../17-more-java8-features/README.md) | **Next:** [Lesson 19 - Streams with Files and Text](../19-streams-with-files-and-text/README.md)
