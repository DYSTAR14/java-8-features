# Lesson 15 - Date and Time API (`java.time`)

> **Level:** Intermediate  |  **Time:** 40 minutes  |  **Run it:** `.\run.ps1 15` / `./run.sh 15`
>
> **Java 21 edition.** The code in this lesson uses Java 21 style. The original Java 8 version is in [lessons-java8/15-date-time-api](../../lessons-java8/15-date-time-api/README.md).

## Why a new API?

The old `Date` and `Calendar` had real problems: months started at **0**, objects were **mutable** (not thread-safe), and code was confusing.
Java 8 added `java.time` (based on the Joda-Time library). It is **clear**, **immutable** and **thread-safe**.

> **Immutable** = once created, never changed. `date.plusDays(1)` returns a **new** object. Remember to use the result!
> `date.plusDays(1);` (ignoring the result) does nothing useful.

## Which class do I choose?

| Class | Holds | Example | Use for |
|---|---|---|---|
| `LocalDate` | date | `2024-03-15` | birthdays, holidays |
| `LocalTime` | time | `14:30` | opening hours |
| `LocalDateTime` | date + time (no zone) | `2024-03-15T14:30` | local events |
| `ZonedDateTime` | date + time + **zone** | `...[America/New_York]` | meetings across the world |
| `Instant` | one moment on the timeline | `2024-03-15T18:30:00Z` | timestamps, logs, measuring |
| `Period` | amount in years/months/days | `P1M10D` | "3 months from now" |
| `Duration` | amount in hours/min/sec | `PT8H45M` | "how long did it take" |

## Quick recipes

```java
LocalDate d = LocalDate.of(2024, 3, 15);        // create
LocalDate today = LocalDate.now();              // today

d.plusDays(10);  d.minusMonths(2);  d.withDayOfMonth(1);   // change (returns new object)
d.isBefore(other);  d.isAfter(other);                       // compare

ChronoUnit.DAYS.between(d1, d2);                // days between two dates
Period.between(d1, d2).getYears();              // e.g. age

d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));   // object -> text
LocalDate.parse("2024-03-15");                          // text -> object (ISO default)
```

## Pattern letters (for `ofPattern`)

| Letter | Meaning | Example |
|---|---|---|
| `yyyy` | year | 2024 |
| `MM` | month number | 03 |
| `MMM` / `MMMM` | month name | Mar / March |
| `dd` | day of month | 15 |
| `EEEE` | weekday name | Friday |
| `HH` / `mm` / `ss` | hour (24h) / minute / second | 14:30:00 |
| `hh` + `a` | hour (12h) + AM/PM | 02:30 PM |

> **Common bug:** `mm` = minutes, `MM` = month. Do not mix them.

## Convert from old classes

```java
Date old = Date.from(instant);          // Instant -> Date
Instant i = old.toInstant();            // Date -> Instant
java.sql.Date -> toLocalDate()          // and so on
```

## Good habits

- Store and send times as **UTC `Instant`** (or ISO-8601 text), show in the user's zone.
- Never use `SimpleDateFormat` in new code (not thread-safe). `DateTimeFormatter` is thread-safe.
- Use `Clock` in tests so you can fake "now".

## Try it yourself

1. How many days until your next birthday?
2. Print the date of the **last Friday** of this month (`TemporalAdjusters.lastInMonth`).
3. A meeting is at 10:00 in London. What time is it in Sydney? (`ZonedDateTime` + `withZoneSameInstant`)

## What changed after Java 8 (the code in this lesson already uses it)

The core API is **unchanged**. Java 9 added `LocalDate.datesUntil`, `LocalDate.ofInstant`, `Duration.toSeconds/toDaysPart`. Java 17 added `InstantSource`. Java 19 added `DateTimeFormatter.ofLocalizedPattern`. Since Java 9 CLDR locale data is the default (JEP 252), so some formatted text may differ from Java 8. See [After Java 8](../../docs/AFTER_JAVA8.md) and the hands-on [Modern lessons](../../modern/01-java9-to-11/README.md).

## Official sources

- Oracle Tutorial - Date Time: <https://docs.oracle.com/javase/tutorial/datetime/index.html>
- Package doc: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html>

**Previous:** [Lesson 14](../14-pitfalls-and-best-practices/README.md) | **Next:** [Lesson 16 - CompletableFuture](../16-completable-future/README.md)
