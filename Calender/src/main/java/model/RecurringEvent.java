package model;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RecurringEvent extends Event {
  private String weekdays; // e.g., "MWF" for Mon, Wed, Fri
  private Integer occurrences;
  private ZonedDateTime recurrenceEnd;

  public RecurringEvent(String subject, ZonedDateTime start, ZonedDateTime end, String location, String description,
      boolean isPublic, String weekdays, Integer occurrences, ZonedDateTime recurrenceEnd) {
    super(subject, start, end, location, description, isPublic);
    this.weekdays = weekdays != null ? weekdays.toUpperCase() : "";
    this.occurrences = occurrences;
    this.recurrenceEnd = recurrenceEnd;
    validateRecurrence();
  }

  private void validateRecurrence() {
    if (occurrences != null && occurrences <= 0) {
      throw new IllegalArgumentException("Occurrences must be positive");
    }
    if (recurrenceEnd != null && recurrenceEnd.isBefore(getStart())) {
      throw new IllegalArgumentException("Recurrence end cannot be before start");
    }
  }

  public List<Event> expandInstances() {
    List<Event> instances = new ArrayList<>();
    ZonedDateTime currentStart = getStart();
    ZonedDateTime currentEnd = getEnd();
    int count = 0;
    int duration = (currentEnd != null) ? (int) ChronoUnit.MINUTES.between(currentStart, currentEnd) : 0;

    while (true) {
      if (recurrenceEnd != null && currentStart.isAfter(recurrenceEnd)) break;
      if (occurrences != null && count >= occurrences) break;
      if (matchesWeekday(currentStart)) {
        Event instance = new Event(getSubject(), currentStart,
            duration > 0 ? currentStart.plusMinutes(duration) : null,
            getLocation(), getDescription(), isPublic());
        instances.add(instance);
        count++;
      }
      currentStart = currentStart.plusDays(1);
      if (currentEnd != null) currentEnd = currentEnd.plusDays(1);
    }
    return instances;
  }

  private boolean matchesWeekday(ZonedDateTime date) {
    if (weekdays.isEmpty()) return true; // Daily recurrence if no weekdays specified
    String day = date.getDayOfWeek().toString().substring(0, 1);
    return weekdays.contains(day);
  }
}