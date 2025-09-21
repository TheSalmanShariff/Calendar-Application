import model.CSVExporter;
import model.Calendar;
import model.Event;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the CSVExporter class, which handles exporting calendar events to CSV files.
 */
public class CSVExporterTest {

  private static final ZoneId EST = ZoneId.of("America/New_York");

  /**
   * Tests exporting a single event to a CSV file.
   * Verifies that the event is correctly written to the CSV file with the expected details.
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportSingleEvent() throws Exception {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 1, 10,
        0, 0, 0, EST);
    Event e = new Event("Meeting", start, start.plusHours(1), "Office",
        null, true);
    cal.addEvent(e, false);

    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test.csv");

    assertTrue(Files.exists(Paths.get("test.csv")));
    String content = Files.readString(Paths.get("test.csv"));
    assertTrue(content.contains("Meeting,03/01/2025,10:00:00,03/01/2025,11:00:00,Office,,No"));
  }

  /**
   * Tests exporting an all-day event to a CSV file.
   * Verifies that the all-day event is exported correctly without an end time.
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportAllDayEvent() throws Exception {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 1, 0,
        0, 0, 0, EST);
    Event e = new Event("Holiday", start, null, null,
        "Day off", false);
    cal.addEvent(e, false);

    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test_allday.csv");
    String content = Files.readString(Paths.get("test_allday.csv"));

    // Verify that the file contains the all-day event data
    assertTrue(content.contains("Holiday,03/01/2025,00:00,03/01/2025,,,Day off,Yes"));
  }

  /**
   * Tests exporting multiple events to a CSV file.
   * Verifies that all events are included in the CSV file with correct details.
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportMultipleEvents() throws Exception {
    Calendar cal = new Calendar();
    ZonedDateTime start1 = ZonedDateTime.of(2025, 3, 1, 9,
        0, 0, 0, EST);
    Event e1 = new Event("Morning Meeting", start1, start1.plusHours(1),
        "Office", "Team sync", true);
    cal.addEvent(e1, false);

    ZonedDateTime start2 = ZonedDateTime.of(2025, 3, 1, 14,
        0, 0, 0, EST);
    Event e2 = new Event("Afternoon Review", start2, start2.plusHours(2),
        "Conference Room", "Project update", false);
    cal.addEvent(e2, false);

    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test_multiple.csv");

    String content = Files.readString(Paths.get("test_multiple.csv"));
    assertTrue(content.contains("Morning Meeting,03/01/2025,09:00:00,"
        + "03/01/2025,10:00:00,Office,Team sync,No"));
    assertTrue(content.contains("Afternoon Review,03/01/2025,14:00:00,03/01/2025,"
        + "16:00:00,Conference Room,Project update,Yes"));
  }

  /**
   * Tests exporting an event that spans multiple days.
   * Verifies that start and end dates are correctly represented.
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportEventSpanningDays() throws Exception {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 1, 23,
        0, 0, 0, EST);
    ZonedDateTime end = ZonedDateTime.of(2025, 3, 2, 1, 0,
        0, 0, EST);
    Event e = new Event("Late Night Shift", start, end, "Workstation",
        "Overnight task", true);
    cal.addEvent(e, false);

    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test_spanning.csv");

    String content = Files.readString(Paths.get("test_spanning.csv"));
    assertTrue(content.contains("Late Night Shift,03/01/2025,23:00:00,03/02/2025,"
        + "01:00:00,Workstation,Overnight task,No"));
  }

  /**
   * Tests exporting an event with special characters in subject and description.
   * Verifies that commas and quotes are handled correctly (quoted or escaped).
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportEventWithSpecialCharacters() throws Exception {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 1, 10,
        0, 0, 0, EST);
    Event e = new Event("Meeting, with comma", start, start.plusHours(1), "Office",
        "Discuss \"important\" topics", true);
    cal.addEvent(e, false);

    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test_special.csv");

    String content = Files.readString(Paths.get("test_special.csv"));
    assertTrue(content.contains("\"Meeting, with comma\",03/01/2025,10:00:00,03/01/2025,"
        + "11:00:00,Office,\"Discuss \"\"important\"\" topics\",No"));
  }

  /**
   * Tests exporting an event with no description.
   * Verifies that the description field is empty in the CSV.
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportEventNoDescription() throws Exception {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 1, 10, 0,
        0, 0, EST);
    Event e = new Event("Silent Meeting", start, start.plusHours(1), "Office",
        null, true);
    cal.addEvent(e, false);

    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test_nodesc.csv");

    String content = Files.readString(Paths.get("test_nodesc.csv"));
    assertTrue(content.contains("Silent Meeting,03/01/2025,10:00:00,"
        + "03/01/2025,11:00:00,Office,,No"));
  }

  /**
   * Tests exporting an event with no location.
   * Verifies that the location field is empty in the CSV.
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportEventNoLocation() throws Exception {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 1, 10,
        0, 0, 0, EST);
    Event e = new Event("Virtual Call", start, start.plusHours(1), null,
        "Phone meeting", true);
    cal.addEvent(e, false);

    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test_noloc.csv");

    String content = Files.readString(Paths.get("test_noloc.csv"));
    assertTrue(content.contains("Virtual Call,03/01/2025,10:00:00,03/01/2025,"
        + "11:00:00,,Phone meeting,No"));
  }

  /**
   * Tests exporting an empty calendar.
   * Verifies that the CSV is either empty or contains only a header.
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportEmptyCalendar() throws Exception {
    Calendar cal = new Calendar();
    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test_empty.csv");

    String content = Files.readString(Paths.get("test_empty.csv"));
    // Allow for either an empty file or a header-only file (implementation-dependent)
    assertTrue(content.trim().isEmpty() || content.trim().equals("Subject,Start Date,"
        + "Start Time,End Date,End Time,Location,Description,Private"));
  }

  /**
   * Tests exporting an event with non-zero seconds.
   * Verifies that seconds are included in the time if supported.
   * @throws Exception if there is an error reading/writing the file
   */
  @Test
  void testExportEventWithSeconds() throws Exception {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 1, 10,
        0, 30, 0, EST);
    ZonedDateTime end = ZonedDateTime.of(2025, 3, 1, 11,
        0, 0, 0, EST);
    Event e = new Event("Precise Meeting", start, end, "Office",
        "Time-sensitive", true);
    cal.addEvent(e, false);
    CSVExporter exporter = new CSVExporter();
    String path = exporter.export(cal, "test_withseconds.csv");
    String content = Files.readString(Paths.get("test_withseconds.csv"));
    assertTrue(content.contains("Precise Meeting,03/01/2025,10:00:30,03/01/2025,"
        + "11:00:00,Office,Time-sensitive,No"));
  }
}
