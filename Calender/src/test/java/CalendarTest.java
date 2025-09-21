import model.Calendar;
import model.Event;
import model.IEvent;
import model.RecurringEvent;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Calendar class.
 */
public class CalendarTest {

  private static final ZoneId EST = ZoneId.of("America/New_York");

  /**
   * Tests adding a single event with no conflict.
   * Verifies that the event is added successfully and retrieved from the calendar.
   */
  @Test
  void testAddSingleEventNoConflict() {
    Calendar cal = new Calendar(); // Fix here: no need for e as it's not declared yet
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 13, 10, 0,
        0, 0, EST);
    Event e = new Event("Meeting", start, start.plusHours(1), null,
        null, true);
    assertTrue(cal.addEvent(e, false));
    List<Event> events = cal.getEventsInRange(start, start.plusHours(1));
    assertEquals(1, events.size());
  }

  /**
   * Tests adding a single event that conflicts with an existing event.
   * Verifies that the new event is not added if auto-decline is enabled.
   */
  @Test
  void testAddSingleEventWithConflictAutoDecline() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 13, 10, 0,
        0, 0, EST);
    Event e1 = new Event("E1", start, start.plusHours(2), null,
        null, true);
    Event e2 = new Event("E2", start.plusHours(1), start.plusHours(3), null,
        null, true);
    cal.addEvent(e1, false);
    assertFalse(cal.addEvent(e2, true));
  }

  /**
   * Tests adding a recurring event with no conflict.
   * Verifies that the recurring event is added successfully to the calendar.
   */
  @Test
  void testAddRecurringEventNoConflict() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 2, 10, 0,
        0, 0, EST); // March 2nd, 2025
    RecurringEvent re = new RecurringEvent("Yoga", start, start.plusHours(1), null,
        null, true, "M", 2, null);
    assertTrue(cal.addRecurringEvent(re));
  }

  /**
   * Tests adding a recurring event that conflicts with an existing event.
   * Verifies that the recurring event is not added if there is a conflict.
   */
  @Test
  void testAddRecurringEventWithConflict() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2023, 11, 6, 10, 0,
        0, 0, EST);
    Event e = new Event("Meeting", start, start.plusHours(2), null, null,
        true);
    RecurringEvent re = new RecurringEvent("Yoga", start, start.plusHours(1), null,
        null, true, "M", 2, null);
    cal.addEvent(e, false);
    assertFalse(cal.addRecurringEvent(re));
  }

  /**
   * Tests retrieving events within a specific time range.
   * Verifies that events are correctly retrieved when they fall within the specified range.
   */
  @Test
  void testGetEventsInRange() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 13, 10,
        0, 0, 0, EST);
    Event e = new Event("Meeting", start, start.plusHours(1), null,
        null, true);
    cal.addEvent(e, false);
    List<Event> events = cal.getEventsInRange(start, start.plusHours(2));
    assertEquals(1, events.size());
  }

  /**
   * Tests the isBusy method to check if the calendar is busy at a specific time.
   * Verifies that the method correctly detects conflicts with existing events.
   */
  @Test
  void testIsBusy() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 13, 10,
        0, 0, 0, EST);
    Event e = new Event("Meeting", start, start.plusHours(1), null,
        null, true);
    cal.addEvent(e, false);
    assertTrue(cal.isBusy(start.plusMinutes(30)));
    assertFalse(cal.isBusy(start.plusHours(2)));
  }

  /**
   * Tests adding multiple non-conflicting events.
   * Verifies that all events are added and retrieved correctly.
   */
  @Test
  void testAddMultipleNonConflictingEvents() {
    Calendar cal = new Calendar();
    ZonedDateTime start1 = ZonedDateTime.of(2025, 3, 13, 10,
        0, 0, 0, EST);
    ZonedDateTime start2 = start1.plusHours(2);
    Event e1 = new Event("Meeting 1", start1, start1.plusHours(1), null,
        null, true);
    Event e2 = new Event("Meeting 2", start2, start2.plusHours(1), null,
        null, true);
    assertTrue(cal.addEvent(e1, false));
    assertTrue(cal.addEvent(e2, false));
    List<Event> events = cal.getEventsInRange(start1, start2.plusHours(1));
    assertEquals(2, events.size());
  }

  /**
   * Tests adding events with partial overlap.
   * Verifies that conflicts are detected when auto-decline is enabled.
   */
  @Test
  void testAddPartiallyOverlappingEvents() {
    Calendar cal = new Calendar();
    ZonedDateTime start1 = ZonedDateTime.of(2025, 3, 13, 10,
        0, 0, 0, EST);
    ZonedDateTime start2 = start1.plusMinutes(30);
    Event e1 = new Event("Meeting 1", start1, start1.plusHours(1), null,
        null, true);
    Event e2 = new Event("Meeting 2", start2, start2.plusHours(1), null,
        null, true);
    assertTrue(cal.addEvent(e1, false));
    assertFalse(cal.addEvent(e2, true));
  }

  /**
   * Tests adding an all-day event.
   * Verifies conflict detection with other events on the same day.
   */
  @Test
  void testAddAllDayEvent() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 13, 0,
        0, 0, 0, EST);
    Event allDay = new Event("Holiday", start, null, null, null,
        true);
    Event timedEvent = new Event("Meeting", start.plusHours(8), start.plusHours(9),
        null, null, true);
    assertTrue(cal.addEvent(allDay, false));
    assertFalse(cal.addEvent(timedEvent, true));
  }

  /**
   * Tests adding a recurring event with an exception.
   * Verifies that the exception is correctly handled when retrieving events.
   */
  @Test
  void testRecurringEventWithException() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2023, 11, 6, 10, 0,
        0, 0, EST); // Monday
    RecurringEvent re = new RecurringEvent("Yoga", start, start.plusHours(1), null,
        null, true, "M", 3, null);
    cal.addRecurringEvent(re);
    ZonedDateTime exceptionDate = start.plusDays(7); // Next Monday
    Event exception = new Event("Yoga Canceled", exceptionDate, exceptionDate.plusHours(1),
        null, "Canceled", true);
    re.addException(exceptionDate, exception);
    List<Event> events = cal.getEventsInRange(start, start.plusDays(14));
    assertEquals(3, events.size());
    assertEquals("Yoga Canceled", events.get(1).getSubject());
  }

  /**
   * Tests editing an event's subject.
   * Verifies that the event is updated correctly.
   */
  @Test
  void testEditEventSubject() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 13, 10,
        0, 0, 0, EST);
    Event e = new Event("Old Subject", start, start.plusHours(1), null,
        null, true);
    cal.addEvent(e, false);
    cal.editEvent("Old Subject", start, "subject", "New Subject");
    List<Event> events = cal.getEventsInRange(start, start.plusHours(1));
    assertEquals("New Subject", events.get(0).getSubject());
  }

  /**
   * Tests retrieving events across time zones.
   * Verifies that events are correctly retrieved in a different time zone.
   */
  @Test
  void testGetEventsAcrossTimeZones() {
    Calendar cal = new Calendar();
    ZoneId pst = ZoneId.of("America/Los_Angeles");
    ZonedDateTime startEST = ZonedDateTime.of(2025, 3, 13, 10,
        0, 0, 0, EST);
    ZonedDateTime startPST = startEST.withZoneSameInstant(pst);
    Event e = new Event("Meeting", startEST, startEST.plusHours(1), null,
        null, true);
    cal.addEvent(e, false);
    List<Event> events = cal.getEventsInRange(startPST, startPST.plusHours(1));
    assertEquals(1, events.size());
  }

  /**
   * Tests adding a large number of events.
   * Verifies that the calendar can handle a large dataset.
   */
  @Test
  void testAddLargeNumberOfEvents() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 13, 10,
        0, 0, 0, EST);
    for (int i = 0; i < 100; i++) {
      Event e = new Event("Event " + i, start.plusDays(i), start.plusDays(i).plusHours(1),
          null, null, true);
      assertTrue(cal.addEvent(e, false));
    }
    List<IEvent> events = cal.getEventsInRange(start, start.plusDays(100));
    assertEquals(100, events.size());
  }

  /**
   * Tests adding an event at midnight (boundary condition).
   * Verifies that it is correctly included in the range.
   */
  @Test
  void testAddEventAtMidnight() {
    Calendar cal = new Calendar();
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 13, 0, 0,
        0, 0, EST);
    Event e = new Event("Midnight Meeting", start, start.plusHours(1), null,
        null, true);
    assertTrue(cal.addEvent(e, false));
    List<IEvent> events = cal.getEventsInRange(start.minusHours(1), start.plusHours(2));
    assertEquals(1, events.size());
  }
}
