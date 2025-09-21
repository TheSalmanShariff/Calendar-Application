import view.TextView;
import model.Event;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the TextView class.
 * This test class verifies the behavior of the printEvents and display methods in TextView class,
 * ensuring they correctly print event details and messages to the console.
 */
public class TextViewTest {

  private static final ZoneId EST = ZoneId.of("America/New_York");

  @Test
  public void testPrintEvents() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);
    System.setOut(printStream);

    TextView textView = new TextView();
    ZonedDateTime start = LocalDateTime.of(2025, 3, 8, 10, 0)
        .atZone(EST);
    ZonedDateTime end = LocalDateTime.of(2025, 3, 8, 12, 0)
        .atZone(EST);
    Event event = new Event("Test Event", start, end, "Location",
        "Description", true);
    textView.printEvents(List.of(event));

    System.setOut(originalOut);
    String output = outputStream.toString().trim();
    String expectedOutput = "Test Event: 03/08/2025 10:00 to 03/08/2025 12:00 at Location";
    assertTrue(output.contains(expectedOutput),
        "The output should contain the event details.");
  }

  @Test
  public void testPrintEventsWithNoEvents() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);
    System.setOut(printStream);

    TextView textView = new TextView();
    textView.printEvents(new ArrayList<>());

    System.setOut(originalOut);
    String output = outputStream.toString().trim();
    String expectedOutput = "No events to display.";
    assertTrue(output.contains(expectedOutput),
        "The output should indicate no events to display.");
  }

  @Test
  public void testPrintEventsWithMultipleEvents() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);
    System.setOut(printStream);

    TextView textView = new TextView();
    ZonedDateTime start1 = LocalDateTime.of(2025, 3, 8, 10,
        0).atZone(EST);
    ZonedDateTime end1 = LocalDateTime.of(2025, 3, 8, 12,
        0).atZone(EST);
    Event event1 = new Event("Event 1", start1, end1, "Location 1",
        "Description 1", true);

    ZonedDateTime start2 = LocalDateTime.of(2025, 3, 9, 14,
        0).atZone(EST);
    ZonedDateTime end2 = LocalDateTime.of(2025, 3, 9, 15,
        0).atZone(EST);
    Event event2 = new Event("Event 2", start2, end2, "Location 2",
        "Description 2", false);

    textView.printEvents(List.of(event1, event2));

    System.setOut(originalOut);
    String output = outputStream.toString().trim();
    String expectedOutput1 = "Event 1: 03/08/2025 10:00 to 03/08/2025 12:00 at Location 1";
    String expectedOutput2 = "Event 2: 03/09/2025 14:00 to 03/09/2025 15:00 at Location 2";
    assertTrue(output.contains(expectedOutput1),
        "The output should contain the first event details.");
    assertTrue(output.contains(expectedOutput2),
        "The output should contain the second event details.");
  }

  @Test
  public void testPrintEventsWithAllDayEvent() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);
    System.setOut(printStream);

    TextView textView = new TextView();
    ZonedDateTime start = LocalDateTime.of(2025, 3, 8, 0,
        0).atZone(EST);
    Event event = new Event("All-Day Event", start, null, "Location",
        "Description", true);
    textView.printEvents(List.of(event));

    System.setOut(originalOut);
    String output = outputStream.toString().trim();
    String expectedOutput = "All-Day Event: 03/08/2025 00:00 to No end time at Location";
    assertTrue(output.contains(expectedOutput),
        "The output should contain the all-day event details with 'No end time'.");
  }

  @Test
  public void testPrintEventsWithNoLocation() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);
    System.setOut(printStream);

    TextView textView = new TextView();
    ZonedDateTime start = LocalDateTime.of(2025, 3, 8,
        10, 0).atZone(EST);
    ZonedDateTime end = LocalDateTime.of(2025, 3, 8,
        12, 0).atZone(EST);
    Event event = new Event("No Location Event", start, end, null,
        "Description", true);
    textView.printEvents(List.of(event));

    System.setOut(originalOut);
    String output = outputStream.toString().trim();
    String expectedOutput = "No Location Event: 03/08/2025 10:00 to "
        + "03/08/2025 12:00 at No location";
    assertTrue(output.contains(expectedOutput),
        "The output should contain the event details with 'No location'.");
  }

  @Test
  public void testDisplayMessage() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);
    System.setOut(printStream);

    TextView textView = new TextView();
    String message = "Test message";
    textView.display(message);

    System.setOut(originalOut);
    String output = outputStream.toString().trim();
    assertTrue(output.contains(message),
        "The output should contain the displayed message.");
  }
}