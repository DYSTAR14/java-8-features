# Lesson 11 - groupingBy and partitioningBy

> **Level:** Intermediate to Advanced  |  **Time:** 50 minutes  |  **Run it:** `.\run-java8.ps1 11` / `./run-java8.sh 11`

This is the lesson that makes people say: *"Oh, now I feel like a stream pro."*

## `groupingBy` = SQL `GROUP BY`

```sql
SELECT department, COUNT(*) FROM employees GROUP BY department;
```
```java
Map<String, Long> r = staff.stream()
        .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
```

### Three forms

| Form | Result |
|---|---|
| `groupingBy(classifier)` | `Map<K, List<T>>` |
| `groupingBy(classifier, downstream)` | `Map<K, D>` where D is what the downstream makes |
| `groupingBy(classifier, mapFactory, downstream)` | same, but you choose the Map type (`TreeMap::new` for sorted keys) |

- **classifier** = a function that returns the group key for each item.
- **downstream** = another collector that is applied to the items inside each group.

## Picture

```
  items:  Alice(Eng) Bob(Eng) Carol(Sales) Dave(Sales) Eve(HR)
                      |
        groupingBy(department, counting())
                      |
  Eng -> 2     Sales -> 2     HR -> 1
```

## Downstream cheat sheet

| I want, per group... | Downstream |
|---|---|
| number of items | `counting()` |
| sum of a number | `summingInt/Long/Double(f)` |
| average | `averagingInt/Long/Double(f)` |
| statistics | `summarizingInt/Long/Double(f)` |
| a list of one property | `mapping(f, toList())` |
| a joined text | `mapping(f, joining(", "))` |
| the biggest / smallest item | `maxBy(cmp)` / `minBy(cmp)` |
| biggest item, no `Optional` | `collectingAndThen(maxBy(cmp), Optional::get)` |
| a group inside a group | `groupingBy(f2, ...)` |

## `partitioningBy`

Same idea, but the key is always `true` or `false`.

```java
Map<Boolean, List<Employee>> p = staff.stream().collect(partitioningBy(e -> e.getAge() >= 40));
p.get(true);    // 40 or older
p.get(false);   // younger
```

| | `groupingBy` | `partitioningBy` |
|---|---|---|
| Number of groups | any | exactly 2 |
| Input | function returning a key | `Predicate` |
| Empty group | key is missing | key is present with empty result |

## Sorting a Map

Maps have no sort. Stream the `entrySet()` instead:

```java
map.entrySet().stream()
   .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
   .forEach(...);
```

## Note about the order of keys

The default `groupingBy` returns a `HashMap`, so the **order of keys is not guaranteed** (it can differ on other computers or Java versions). If order matters, pass `TreeMap::new` (sorted keys) or `LinkedHashMap::new` (insertion order).

## Try it yourself

1. Group employees by city and print the **average age** per city.
2. Find the **youngest** employee in each department.
3. How many employees per `(department, city)` pair? (two-level grouping + `counting()`)
4. Partition employees by "salary above the company average".

## Official sources

- Collectors.groupingBy: <https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html#groupingBy-java.util.function.Function->
- Tutorial - Grouping: <https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html#collect>

**Previous:** [Lesson 10](../10-collectors/README.md) | **Next:** [Lesson 12 - Advanced Stream Techniques](../12-advanced-stream-techniques/README.md)
