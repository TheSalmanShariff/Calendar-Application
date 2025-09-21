import controller.CalendarController;
import model.CalendarManager;  // Import CalendarManager
import org.junit.jupiter.api.Test;
import view.TextView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the CalendarController class.
 */
public class CalendarControllerTest {

  /**
   * Sets up the CalendarController with a TextView that captures output.
   * This method returns a new instance of CalendarController with a TextView that captures
   * the output to be used in testing.
   * @return a new instance of CalendarController
   */
  private CalendarController setupControllerWithCapture() {
    CalendarManager calendarManager = new CalendarManager(); // Use CalendarManager instead of Calendar
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    TextView view = new TextView() {
      @Override
      public void display(String message) {
        System.setOut(new PrintStream(outContent));
        super.display(message);
      }
    };
    return new CalendarController(calendarManager, view); // Pass CalendarManager here
  }

  /**
   * Tests the creation of a single event.
   */
  @Test
  void testCreateSingleEvent() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand(
        "create event Meeting from 2025-03-01 10:00 to 2025-03-01 11:00"));
  }

  /**
   * Tests editing an event's subject.
   */
  @Test
  void testEditEvent() {
    CalendarController controller = setupControllerWithCapture();
    controller.processCommand("create event Meeting from 2025-03-01 10:00 to 2025-03-01 11:00");
    assertTrue(controller.processCommand(
        "edit event subject Meeting from 2025-03-01 10:00 with NewMeeting"));
  }

  /**
   * Tests an invalid command and expects an exception to be thrown.
   */
  @Test
  void testInvalidCommand() {
    CalendarController controller = setupControllerWithCapture();
    assertThrows(IllegalArgumentException.class, () ->
        controller.processCommand("invalid command"));
  }

  /**
   * Tests the exit command to ensure the program ends correctly.
   */
  @Test
  void testExitCommand() {
    CalendarController controller = setupControllerWithCapture();
    assertFalse(controller.processCommand("exit"));
  }

  /**
   * Tests creating an event with the same start and end time.
   */
  @Test
  void testZeroDurationEvent() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand(
        "create event InstantMeeting from 2025-03-01 10:00 to 2025-03-01 10:00"));
  }

  /**
   * Tests creating a recurring event with a specified number of occurrences.
   */
  @Test
  void testCreateRecurringEventWithOccurrences() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand(
        "create recurring event WeeklySync from 2025-03-01 14:00 to "
            + "2025-03-01 15:00 --weekdays W --occurrences 5"));
  }

  /**
   * Tests creating a recurring event with an end date.
   */
  @Test
  void testCreateRecurringEventWithEndDate() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand(
        "create recurring event MonthlyMeeting from 2025-03-01 09:00 to 2025-03-01 10:00 "
            + "--weekdays M --end-date 2025-05-01 00:00"));
  }

  /**
   * Tests conflict detection for recurring events.
   */
  @Test
  void testRecurringEventConflict() {
    CalendarController controller = setupControllerWithCapture();
    // Create initial event
    controller.processCommand("create event Meeting from 2025-03-01 14:00 to 2025-03-01 15:00");
    // Attempt conflicting recurring event
    assertTrue(controller.processCommand(
        "create recurring event ConflictEvent from 2025-03-01 14:00 to 2025-03-01 15:00 "
            + "--weekdays W --occurrences 3"));
    // Should display conflict message
  }

  /**
   * Tests exporting events to CSV.
   */
  @Test
  void testExportEvents() {
    CalendarController controller = setupControllerWithCapture();
    controller.processCommand("create event ExportTest from 2025-03-01 10:00 to 2025-03-01 11:00");
    assertTrue(controller.processCommand("export csv test_export.csv"));
  }

  /**
   * Tests checking availability at a busy time.
   */
  @Test
  void testShowBusy() {
    CalendarController controller = setupControllerWithCapture();
    controller.processCommand("create event BusyTime from 2025-03-01 14:00 to 2025-03-01 15:00");
    assertTrue(controller.processCommand("show busy 2025-03-01 14:30"));
  }

  /**
   * Tests checking availability at a free time.
   */
  @Test
  void testShowAvailable() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand("show busy 2025-03-01 09:00"));
  }

  /**
   * Tests editing a non-existent event (should fail).
   */
  @Test
  void testEditNonExistentEvent() {
    CalendarController controller = setupControllerWithCapture();
    assertThrows(IllegalArgumentException.class, () ->
        controller.processCommand("edit event subject GhostMeeting "
            + "from 2025-03-01 10:00 with NewName"));
  }

  /**
   * Tests auto-decline functionality for conflicting events.
   */
  @Test
  void testAutoDeclineConflict() {
    CalendarController controller = setupControllerWithCapture();
    controller.processCommand("create event FirstMeeting "
        + "from 2025-03-01 10:00 to 2025-03-01 11:00");
    // Attempt to add conflicting event with auto-decline
    assertTrue(controller.processCommand(
        "create event --autoDecline ConflictMeeting from 2025-03-01 10:30 to 2025-03-01 11:30"));
    // Should display "Event declined due to conflict"
  }

  /**
   * Tests invalid date format handling.
   */
  @Test
  void testInvalidDateFormat() {
    CalendarController controller = setupControllerWithCapture();
    assertThrows(IllegalArgumentException.class, () ->
        controller.processCommand("create event BadDate "
            + "from 2025/03/01 10:00 to 2025/03/01 11:00"));
  }

  /**
   * Tests all-day event creation (no end time specified).
   */
  @Test
  void testAllDayEvent() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand(
        "create event AllDay on 2025-03-01"));
  }

  /**
   * Tests printing events within a date range.
   */
  @Test
  void testPrintEventsInRange() {
    CalendarController controller = setupControllerWithCapture();
    controller.processCommand("create event RangeTest from 2025-03-01 10:00 to 2025-03-01 11:00");
    assertTrue(controller.processCommand("print events from 2025-03-01 00:00 to 2025-03-01 23:59"));
  }

  /**
   * Tests events with quoted names/locations.
   */
  @Test
  void testQuotedEventNames() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand(
        "create event \"Meeting with Quotes\" "
            + "from 2025-03-01 10:00 to 2025-03-01 11:00 --location \"Room 101\""));
  }

  /**
   * Tests missing required parameters in command.
   */
  @Test
  void testMissingParameters() {
    CalendarController controller = setupControllerWithCapture();
    // Ensure missing parameters are caught
    assertThrows(IllegalArgumentException.class, () ->
        controller.processCommand("create event IncompleteEvent from 2025-03-01 10:00 to 2025-03-01 11:00"));
  }

  /**
   * Tests creating an event without a location.
   */
  @Test
  void testEventWithoutLocation() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand(
        "create event NoLocationEvent from 2025-03-01 12:00 to 2025-03-01 13:00"));
  }

  /**
   * Tests creating a multi-day event.
   */
  @Test
  void testMultiDayEvent() {
    CalendarController controller = setupControllerWithCapture();
    assertTrue(controller.processCommand(
        "create event MultiDayEvent from 2025-03-01 10:00 to 2025-03-03 10:00"));
  }
}
