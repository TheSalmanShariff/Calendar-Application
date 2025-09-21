package controller;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import model.*;
import view.TextView;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controls calendar operations for multiple calendars via CalendarManager.
 */
public class CalendarController {
  private CalendarManager calendarManager;
  private TextView view;
  private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public CalendarController(CalendarManager calendarManager, TextView view) {
    this.calendarManager = calendarManager;
    this.view = view;
  }

  public boolean processCommand(String command) {
    List<String> parts = tokenizeCommand(command);
    try {
      switch (parts.get(0).toLowerCase()) {
        case "create":
          if (parts.get(1).equalsIgnoreCase("calendar")) {
            handleCreateCalendar(parts);
          } else {
            handleCreate(parts);
          }
          break;
        case "edit":
          if (parts.get(1).equalsIgnoreCase("calendar")) {
            handleEditCalendar(parts);
          } else {
            handleEdit(parts);
          }
          break;
        case "use":
          handleUseCalendar(parts);
          break;
        case "copy":
          handleCopy(parts);
          break;
        case "print":
          handlePrint(parts);
          break;
        case "export":
          handleExport(parts);
          break;
        case "show":
          handleShowBusy(parts);
          break;
        case "exit":
          return false;
        default:
          throw new IllegalArgumentException("Unknown command: " + parts.get(0));
      }
    } catch (Exception e) {
      view.display("Invalid command: " + command + " - " + e.getMessage());
      return true;
    }
    return true;
  }

  private List<String> tokenizeCommand(String command) {
    List<String> tokens = new ArrayList<>();
    StringBuilder currentToken = new StringBuilder();
    boolean inQuotes = false;
    for (char c : command.toCharArray()) {
      if (c == '"') {
        inQuotes = !inQuotes;
        if (!inQuotes && currentToken.length() > 0) {
          tokens.add(currentToken.toString());
          currentToken.setLength(0);
        }
      } else if (Character.isWhitespace(c) && !inQuotes) {
        if (currentToken.length() > 0) {
          tokens.add(currentToken.toString());
          currentToken.setLength(0);
        }
      } else {
        currentToken.append(c);
      }
    }
    if (currentToken.length() > 0) {
      tokens.add(currentToken.toString());
    }
    return tokens;
  }

  private void handleCreateCalendar(List<String> parts) {
    if (!parts.get(2).equals("--name") || !parts.get(4).equals("--timezone")) {
      throw new IllegalArgumentException("Invalid create calendar syntax");
    }
    String name = parts.get(3);
    String timezoneStr = parts.get(5);
    ZoneId timezone = ZoneId.of(timezoneStr);
    calendarManager.createCalendar(name, timezone);
    view.display("Calendar '" + name + "' created.");
  }

  private void handleUseCalendar(List<String> parts) {
    if (!parts.get(1).equals("calendar") || !parts.get(2).equals("--name")) {
      throw new IllegalArgumentException("Invalid use calendar syntax");
    }
    String name = parts.get(3);
    calendarManager.setCurrentCalendar(name);
    view.display("Switched to calendar '" + name + "'.");
  }

  private void handleEditCalendar(List<String> parts) {
    if (!parts.get(2).equals("--name") || !parts.get(4).equals("--property")) {
      throw new IllegalArgumentException("Invalid edit calendar syntax");
    }
    String name = parts.get(3);
    String property = parts.get(5);
    String newValue = parts.get(6);
    ICalendar calendar = calendarManager.getCalendar(name);
    if (property.equals("name")) {
      calendarManager.renameCalendar(name, newValue);
      view.display("Calendar renamed to '" + newValue + "'.");
    } else if (property.equals("timezone")) {
      ZoneId newTimezone = ZoneId.of(newValue);
      calendar.setTimezone(newTimezone);
      view.display("Timezone updated for '" + name + "'.");
    } else {
      throw new IllegalArgumentException("Unknown property: " + property);
    }
  }

  private void handleCreate(List<String> parts) {
    int index = 0;
    boolean isRecurring = false;
    if (!parts.get(index++).equalsIgnoreCase("create")) {
      throw new IllegalArgumentException("Invalid create command");
    }
    if (parts.get(index).equalsIgnoreCase("recurring")) {
      isRecurring = true;
      index++;
    }
    if (!parts.get(index++).equalsIgnoreCase("event")) {
      throw new IllegalArgumentException("Expected 'event'");
    }
    String name = parts.get(index++);
    ZoneId timezone = calendarManager.getCurrentCalendar().getTimezone();
    ZonedDateTime start;
    ZonedDateTime end = null;
    if (parts.get(index).equalsIgnoreCase("on")) {
      index++;
      LocalDate date = LocalDate.parse(parts.get(index++));
      start = date.atStartOfDay(timezone);
      end = null; // All-day event
    } else if (parts.get(index).equalsIgnoreCase("from")) {
      index++;
      start = parseDateTime(parts.get(index++) + " " + parts.get(index++), timezone);
      if (index < parts.size() && parts.get(index).equalsIgnoreCase("to")) {
        index++;
        end = parseDateTime(parts.get(index++) + " " + parts.get(index++), timezone);
      }
    } else {
      throw new IllegalArgumentException("Expected 'on' or 'from'");
    }
    String weekdays = null;
    Integer occurrences = null;
    ZonedDateTime recurrenceEnd = null;
    String location = null;
    String description = null;
    while (index < parts.size()) {
      switch (parts.get(index)) {
        case "--weekdays":
          weekdays = parts.get(++index);
          break;
        case "--occurrences":
          occurrences = Integer.parseInt(parts.get(++index));
          break;
        case "--end-date":
          recurrenceEnd = parseDateTime(parts.get(++index) + " " + parts.get(++index), timezone);
          break;
        case "--location":
          location = parts.get(++index);
          break;
        case "--description":
          description = parts.get(++index);
          break;
        default:
          throw new IllegalArgumentException("Unknown parameter: " + parts.get(index));
      }
      index++;
    }
    if (isRecurring) {
      if (weekdays == null) {
        throw new IllegalArgumentException("--weekdays required for recurring events");
      }
      RecurringEvent event = new RecurringEvent(name, start, end, location, description, true,
          weekdays, occurrences, recurrenceEnd);
      if (!calendarManager.getCurrentCalendar().addRecurringEvent(event)) {
        view.display("Recurring event declined due to conflict");
      } else {
        view.display("Recurring event '" + name + "' created.");
      }
    } else {
      Event event = new Event(name, start, end, location, description, true);
      if (!calendarManager.getCurrentCalendar().addEvent(event)) {
        view.display("Event declined due to conflict");
      } else {
        view.display("Event '" + name + "' created.");
      }
    }
  }

  private void handleEdit(List<String> parts) {
    if (parts.size() < 9 || !parts.get(4).equals("from") || !parts.get(7).equals("with")) {
      throw new IllegalArgumentException("Invalid edit command format");
    }
    String property = parts.get(2);
    String eventName = parts.get(3);
    ZoneId timezone = calendarManager.getCurrentCalendar().getTimezone();
    ZonedDateTime from = parseDateTime(parts.get(5) + " " + parts.get(6), timezone);
    String newValue = parts.get(8);
    ZonedDateTime farFuture = from.plusYears(100);
    List<IEvent> eventsToEdit = calendarManager.getCurrentCalendar()
        .getEventsInRange(from, farFuture).stream()
        .filter(e -> e.getSubject().equals(eventName))
        .collect(Collectors.toList());
    for (IEvent e : eventsToEdit) {
      try {
        calendarManager.getCurrentCalendar().editEventInstance(e.getStart(), property, newValue);
      } catch (IllegalArgumentException ex) {
        view.display("Cannot edit event '" + e.getSubject() + "' at " + e.getStart() + ": " + ex.getMessage());
      }
    }
    if (!eventsToEdit.isEmpty()) {
      view.display("Events updated where applicable.");
    }
  }

  private void handleCopy(List<String> parts) {
    if (parts.get(1).equals("event")) {
      handleCopyEvent(parts);
    } else if (parts.get(1).equals("events") && parts.get(2).equals("on")) {
      handleCopyEventsOn(parts);
    } else if (parts.get(1).equals("events") && parts.get(2).equals("between")) {
      handleCopyEventsBetween(parts);
    } else {
      throw new IllegalArgumentException("Invalid copy command");
    }
  }

  private void handleCopyEvent(List<String> parts) {
    if (parts.size() < 11 || !parts.get(3).equals("on") || !parts.get(6).equals("--target") || !parts.get(8).equals("to")) {
      throw new IllegalArgumentException("Invalid copy event syntax");
    }
    String eventName = parts.get(2);
    ZoneId currentTimezone = calendarManager.getCurrentCalendar().getTimezone();
    ZonedDateTime sourceStart = parseDateTime(parts.get(4) + " " + parts.get(5), currentTimezone);
    String targetCalendarName = parts.get(7);
    ICalendar targetCalendar = calendarManager.getCalendar(targetCalendarName);
    ZoneId targetTimezone = targetCalendar.getTimezone();
    ZonedDateTime targetStart = parseDateTime(parts.get(9) + " " + parts.get(10), targetTimezone);
    IEvent sourceEvent = calendarManager.getCurrentCalendar().getEventAt(sourceStart);
    if (sourceEvent == null || !sourceEvent.getSubject().equals(eventName)) {
      throw new IllegalArgumentException("Event not found");
    }
    Duration duration = (sourceEvent.getEnd() != null) ? Duration.between(sourceEvent.getStart(), sourceEvent.getEnd()) : null;
    ZonedDateTime newStart = targetStart;
    ZonedDateTime newEnd = duration != null ? newStart.plus(duration) : null;
    Event newEvent = new Event(sourceEvent.getSubject(), newStart, newEnd, sourceEvent.getLocation(),
        sourceEvent.getDescription(), sourceEvent.isPublic());
    if (!targetCalendar.addEvent(newEvent)) {
      view.display("Cannot copy event '" + eventName + "' due to conflict");
    } else {
      view.display("Event '" + eventName + "' copied to '" + targetCalendarName + "'.");
    }
  }

  private void handleCopyEventsOn(List<String> parts) {
    if (parts.size() < 8 || !parts.get(4).equals("--target") || !parts.get(6).equals("to")) {
      throw new IllegalArgumentException("Invalid copy events on syntax");
    }
    ZoneId currentTimezone = calendarManager.getCurrentCalendar().getTimezone();
    LocalDate sourceDate = LocalDate.parse(parts.get(3));
    ZonedDateTime sourceStart = sourceDate.atStartOfDay(currentTimezone);
    ZonedDateTime sourceEnd = sourceDate.plusDays(1).atStartOfDay(currentTimezone).minusSeconds(1);
    String targetCalendarName = parts.get(5);
    ICalendar targetCalendar = calendarManager.getCalendar(targetCalendarName);
    ZoneId targetTimezone = targetCalendar.getTimezone();
    LocalDate targetDate = LocalDate.parse(parts.get(7));
    List<IEvent> eventsToCopy = calendarManager.getCurrentCalendar().getEventsInRange(sourceStart, sourceEnd);
    for (IEvent e : eventsToCopy) {
      LocalTime startTime = e.getStart().toLocalTime();
      LocalTime endTime = e.getEnd() != null ? e.getEnd().toLocalTime() : null;
      ZonedDateTime newStart = targetDate.atTime(startTime).atZone(targetTimezone);
      ZonedDateTime newEnd = endTime != null ? targetDate.atTime(endTime).atZone(targetTimezone) : null;
      Event newEvent = new Event(e.getSubject(), newStart, newEnd, e.getLocation(),
          e.getDescription(), e.isPublic());
      if (!targetCalendar.addEvent(newEvent)) {
        view.display("Cannot copy event '" + e.getSubject() + "' due to conflict");
      }
    }
    if (!eventsToCopy.isEmpty()) {
      view.display("Events copied to '" + targetCalendarName + "' where applicable.");
    }
  }

  private void handleCopyEventsBetween(List<String> parts) {
    if (parts.size() < 10 || !parts.get(4).equals("and") || !parts.get(6).equals("--target") || !parts.get(8).equals("to")) {
      throw new IllegalArgumentException("Invalid copy events between syntax");
    }
    ZoneId currentTimezone = calendarManager.getCurrentCalendar().getTimezone();
    LocalDate sourceStartDate = LocalDate.parse(parts.get(3));
    LocalDate sourceEndDate = LocalDate.parse(parts.get(5));
    ZonedDateTime sourceStart = sourceStartDate.atStartOfDay(currentTimezone);
    ZonedDateTime sourceEnd = sourceEndDate.plusDays(1).atStartOfDay(currentTimezone).minusSeconds(1);
    String targetCalendarName = parts.get(7);
    ICalendar targetCalendar = calendarManager.getCalendar(targetCalendarName);
    ZoneId targetTimezone = targetCalendar.getTimezone();
    LocalDate targetStartDate = LocalDate.parse(parts.get(9));
    List<IEvent> eventsToCopy = calendarManager.getCurrentCalendar().getEventsInRange(sourceStart, sourceEnd);
    for (IEvent e : eventsToCopy) {
      long daysOffset = ChronoUnit.DAYS.between(sourceStartDate, e.getStart().toLocalDate());
      LocalDate newDate = targetStartDate.plusDays(daysOffset);
      LocalTime startTime = e.getStart().toLocalTime();
      LocalTime endTime = e.getEnd() != null ? e.getEnd().toLocalTime() : null;
      ZonedDateTime newStart = newDate.atTime(startTime).atZone(targetTimezone);
      ZonedDateTime newEnd = endTime != null ? newDate.atTime(endTime).atZone(targetTimezone) : null;
      Event newEvent = new Event(e.getSubject(), newStart, newEnd, e.getLocation(),
          e.getDescription(), e.isPublic());
      if (!targetCalendar.addEvent(newEvent)) {
        view.display("Cannot copy event '" + e.getSubject() + "' due to conflict");
      }
    }
    if (!eventsToCopy.isEmpty()) {
      view.display("Events copied to '" + targetCalendarName + "' where applicable.");
    }
  }

  private void handlePrint(List<String> parts) {
    ZoneId timezone = calendarManager.getCurrentCalendar().getTimezone();
    ZonedDateTime from = parseDateTime(parts.get(3) + " " + parts.get(4), timezone);
    ZonedDateTime to = parts.size() > 6 ? parseDateTime(parts.get(6) + " " + parts.get(7), timezone) : from;
    List<IEvent> events = calendarManager.getCurrentCalendar().getEventsInRange(from, to);
    view.printEvents(events.stream().map(e -> (Event) e).collect(Collectors.toList()));
  }

  private void handleExport(List<String> parts) {
    if (parts.size() < 3) {
      throw new IllegalArgumentException("Missing filename");
    }
    try {
      String path = new CSVExporter().export((Calendar) calendarManager.getCurrentCalendar(), parts.get(2));
      view.display("Exported to: " + path);
    } catch (IOException e) {
      throw new IllegalArgumentException("Export failed: " + e.getMessage());
    }
  }

  private void handleShowBusy(List<String> parts) {
    ZoneId timezone = calendarManager.getCurrentCalendar().getTimezone();
    ZonedDateTime time = parseDateTime(parts.get(2) + " " + parts.get(3), timezone);
    view.display(calendarManager.getCurrentCalendar().isBusy(time) ? "Busy" : "Available");
  }

  private ZonedDateTime parseDateTime(String dateTimeStr, ZoneId timezone) {
    try {
      LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, DT_FORMAT);
      return localDateTime.atZone(timezone);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date-time format: " + dateTimeStr);
    }
  }
}