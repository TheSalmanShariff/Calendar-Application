package view;

import model.Event;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Handles text-based output for the calendar application.
 */
public class TextView {
  private static final DateTimeFormatter DATE_TIME_FORMAT =
      DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  public void printEvents(List<Event> events) {
    if (events == null || events.isEmpty()) {
      System.out.println("No events to display.");
      return;
    }
    for (Event event : events) {
      String startStr = event.getStart().format(DATE_TIME_FORMAT);
      String endStr = event.getEnd() != null ? event.getEnd().format(DATE_TIME_FORMAT)
          : "No end time";
      String output = String.format("%s: %s to %s at %s",
          event.getSubject(), startStr, endStr,
          event.getLocation() != null ? event.getLocation() : "No location");
      System.out.println(output);
    }
  }

  public void display(String message) {
    System.out.println(message);
  }
}