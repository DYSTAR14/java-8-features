import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * LESSON 15 - The new Date and Time API (package java.time)
 *
 * The old java.util.Date and Calendar were confusing (months started at 0!),
 * mutable (not thread-safe) and hard to use.
 * The new API is clear, IMMUTABLE (every change gives a NEW object) and thread-safe.
 *
 * JAVA 21 EDITION. The core API did not change after Java 8. New: LocalDate.datesUntil (Java 9),
 * and Stream.toList() (Java 16). Also, since Java 9 CLDR locale data is the default (JEP 252), so text made by
 * locale-based formatters can differ from Java 8. The patterns used below are explicit, so output is stable.
 * The original Java 8 file is in lessons-java8/15-date-time-api/.
 */
public class Lesson15_DateTimeApi {

    public static void main(String[] args) {
        System.out.println("--- 1. The main classes ---");
        LocalDate date = LocalDate.of(2024, 3, 15);            // date only
        LocalTime time = LocalTime.of(14, 30, 0);              // time only
        LocalDateTime dateTime = LocalDateTime.of(date, time); // date + time, NO time zone
        System.out.println("LocalDate     : " + date);
        System.out.println("LocalTime     : " + time);
        System.out.println("LocalDateTime : " + dateTime);
        System.out.println("today (changes every day): " + LocalDate.now());

        System.out.println("\n--- 2. Reading parts ---");
        System.out.println("year=" + date.getYear() + " month=" + date.getMonth() + " (" + date.getMonthValue() + ")"
                + " day=" + date.getDayOfMonth() + " weekday=" + date.getDayOfWeek());
        System.out.println("day of year = " + date.getDayOfYear() + ", leap year? " + date.isLeapYear());

        System.out.println("\n--- 3. Changing = new object (the original is untouched) ---");
        LocalDate nextWeek = date.plusWeeks(1);
        LocalDate lastMonth = date.minusMonths(1);
        LocalDate changed = date.withDayOfMonth(1);
        System.out.println("original:   " + date);
        System.out.println("plus 1 week: " + nextWeek + ", minus 1 month: " + lastMonth + ", first of month: " + changed);

        // Smart: Jan 31 + 1 month gives the last valid day
        System.out.println("2024-01-31 plus 1 month = " + LocalDate.of(2024, 1, 31).plusMonths(1));

        // Java 9: datesUntil = a Stream of every date from the start (included) to the end (excluded).
        // Java 8 needed a for-loop with plusDays(1).
        System.out.println("datesUntil: " + date.datesUntil(date.plusDays(3)).toList());

        System.out.println("\n--- 4. Comparing ---");
        LocalDate other = LocalDate.of(2024, 12, 25);
        System.out.println("date before other? " + date.isBefore(other));
        System.out.println("date after other?  " + date.isAfter(other));
        System.out.println("equal?             " + date.equals(LocalDate.of(2024, 3, 15)));

        System.out.println("\n--- 5. Period (date-based) and Duration (time-based) ---");
        Period period = Period.between(date, other);
        System.out.println("between: " + period.getMonths() + " months, " + period.getDays() + " days  (" + period + ")");
        System.out.println("days between: " + ChronoUnit.DAYS.between(date, other));

        Duration duration = Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 45));
        System.out.println("work day: " + duration.toHours() + " h " + (duration.toMinutes() % 60) + " min  (" + duration + ")");

        // Age calculator
        LocalDate birthday = LocalDate.of(1990, 6, 20);
        System.out.println("age on " + date + " = " + Period.between(birthday, date).getYears());
        System.out.println("same age with ChronoUnit = " + ChronoUnit.YEARS.between(birthday, date));

        System.out.println("\n--- 6. Formatting and parsing ---");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("formatted: " + dateTime.format(fmt));
        System.out.println("ISO:       " + DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        System.out.println("text style: " + date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", java.util.Locale.ENGLISH)));

        LocalDate parsed = LocalDate.parse("2025-01-20");                               // ISO format is the default
        LocalDate parsed2 = LocalDate.parse("20-01-2025", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        System.out.println("parsed: " + parsed + " and " + parsed2);
        try {
            LocalDate.parse("not a date");
        } catch (DateTimeParseException e) {
            System.out.println("bad text -> DateTimeParseException");
        }

        System.out.println("\n--- 7. Smart date adjusters ---");
        System.out.println("next Monday after " + date + ": " + date.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
        System.out.println("last day of month:  " + date.with(TemporalAdjusters.lastDayOfMonth()));
        System.out.println("first day of next year: " + date.with(TemporalAdjusters.firstDayOfNextYear()));

        System.out.println("\n--- 8. Time zones ---");
        ZonedDateTime inNewYork = ZonedDateTime.of(dateTime, ZoneId.of("America/New_York"));
        ZonedDateTime inTokyo = inNewYork.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));   // same moment, other clock
        System.out.println("New York: " + inNewYork);
        System.out.println("Tokyo:    " + inTokyo);

        System.out.println("\n--- 9. Instant: a point on the global timeline (machine time) ---");
        Instant epoch = Instant.ofEpochSecond(0);
        System.out.println("epoch = " + epoch);                            // 1970-01-01T00:00:00Z
        System.out.println("now is after epoch? " + Instant.now().isAfter(epoch));
        // Use Instant for timestamps and measuring; use LocalDate/ZonedDateTime for human calendars.

        System.out.println("\n--- 10. Handy extras ---");
        System.out.println("Month enum: " + Month.FEBRUARY + ", length in 2024: " + Month.FEBRUARY.length(true));
        System.out.println("days in the month of " + date + " = " + date.lengthOfMonth());
        System.out.println("Working day? " + isWeekday(date));

        System.out.println("\n--- 11. Old <-> new conversion ---");
        Date oldDate = Date.from(inNewYork.toInstant());
        Instant back = oldDate.toInstant();
        System.out.println("old Date -> Instant -> " + back);
    }

    static boolean isWeekday(LocalDate d) {
        DayOfWeek w = d.getDayOfWeek();
        return w != DayOfWeek.SATURDAY && w != DayOfWeek.SUNDAY;
    }
}
