# lessons-java8: the original Java 8 edition

This folder keeps the **original Java 8 version** of all 20 lessons, unchanged in meaning.
The main course in [`../lessons/`](../lessons/README.md) is the **Java 21 edition**.

| | `lessons-java8/` (this folder) | `lessons/` |
|---|---|---|
| Java version | **Java 8** (compiled with `--release 8`, so newer APIs are rejected) | **Java 21** (compiled with `--release 21`) |
| Run | `.\run-java8.ps1 11` / `./run-java8.sh 11` | `.\run.ps1 11` / `./run.sh 11` |
| `Employee` | normal class with `getSalary()` | `record` with `salary()` |
| Lists | `Arrays.asList`, `collect(Collectors.toList())` | `List.of`, `.toList()` |
| Needs | JDK 8 or newer | JDK 21 |

Why keep both?

1. You can **see exactly what is Java 8** and what is newer: compare the two files side by side.
2. If you work on an old project that must stay on Java 8, these are the lessons to follow.
3. The compiler proves it: every file here compiles with `--release 8`.

Start with this folder if your goal is "what is a Java 8 feature". Use `lessons/` if you write code on Java 21.
