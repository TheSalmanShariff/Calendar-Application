package model;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages multiple calendars, each with a unique name and timezone.
 * Ensures that there is always at least one calendar and that names are unique.
 */
public class CalendarManager {
  private final Map<String, ICalendar> calendars;
  private ICalendar currentCalendar;

  /**
   * Initializes the CalendarManager with a default calendar.
   */
  public CalendarManager() {
    calendars = new HashMap<>();
    ICalendar defaultCalendar = new Calendar("default", ZoneId.of("America/New_York"));
    calendars.put("default", defaultCalendar);
    currentCalendar = defaultCalendar;
  }

  public void createCalendar(String name, ZoneId timezone) {
    if (calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar name already exists");
    }
    ICalendar calendar = new Calendar(name, timezone);
    calendars.put(name, calendar);
  }

  public void setCurrentCalendar(String name) {
    ICalendar calendar = calendars.get(name);
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar not found");
    }
    this.currentCalendar = calendar;
  }

  public ICalendar getCurrentCalendar() {
    return currentCalendar;
  }

  public ICalendar getCalendar(String name) {
    ICalendar calendar = calendars.get(name);
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar not found");
    }
    return calendar;
  }

  public void renameCalendar(String oldName, String newName) {
    if (calendars.containsKey(newName)) {
      throw new IllegalArgumentException("Calendar name already exists");
    }
    ICalendar calendar = calendars.remove(oldName);
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar not found");
    }
    calendar.setName(newName);
    calendars.put(newName, calendar);
    if (currentCalendar == calendar) {
      currentCalendar = calendar;
    }
  }

  public void deleteCalendar(String name) {
    if (calendars.size() <= 1) {
      throw new IllegalArgumentException("Cannot delete the last calendar");
    }
    ICalendar calendar = calendars.remove(name);
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar not found");
    }
    if (currentCalendar == calendar) {
      currentCalendar = calendars.values().iterator().next();
    }
  }
}