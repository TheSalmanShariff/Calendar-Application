import model.Event;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for validating Event class functionality.
 * This includes tests for event creation, time validation,
 * conflict checks, and edge cases.
 */
public class EventTest {

  private static final ZoneId EST = ZoneId.of("America/New_York");

  @Test
  void testEventCreationBasic() {
    ZonedDateTime start = ZonedDateTime.now(EST);
    Event event = new Event("Meeting", start, null, null,
        null, true);
    assertEquals("Meeting", event.getSubject());
    assertEquals(start, event.getStart());
    assertNull(event.getEnd());
    assertTrue(event.isPublic());
  }

  @Test
  void testEventCreationWithEndTime() {
    ZonedDateTime start = ZonedDateTime.now(EST);
    ZonedDateTime end = start.plusHours(1);
    Event event = new Event("Meeting", start, end, "Office",
        "Team sync", false);
    assertEquals(end, event.getEnd());
    assertEquals("Office", event.getLocation());
    assertEquals("Team sync", event.getDescription());
    assertFalse(event.isPublic());
  }

  @Test
  void testInvalidTimeOrderThrowsException() {
    ZonedDateTime start = ZonedDateTime.now(EST);
    ZonedDateTime end = start.minusHours(1);
    assertThrows(IllegalArgumentException.class, () ->
        new Event("Meeting", start, end, null, null, true));
  }

  @Test
  void testConflictWithOverlappingEvent() {
    ZonedDateTime now = ZonedDateTime.now(EST);
    Event e1 = new Event("E1", now, now.plusHours(2), null, null,
        true);
    Event e2 = new Event("E2", now.plusHours(1), now.plusHours(3), null,
        null, true);
    assertTrue(e1.conflictsWith(e2));
    assertTrue(e2.conflictsWith(e1));
  }

  @Test
  void testNoConflictWithNonOverlappingEvent() {
    ZonedDateTime now = ZonedDateTime.now(EST);
    Event e1 = new Event("E1", now, now.plusHours(1), null, null,
        true);
    Event e2 = new Event("E2", now.plusHours(2), now.plusHours(3), null,
        null, true);
    assertFalse(e1.conflictsWith(e2));
    assertFalse(e2.conflictsWith(e1));
  }

  @Test
  void testNoConflictWhenAdjacent() {
    ZonedDateTime now = ZonedDateTime.now(EST);
    Event e1 = new Event("E1", now, now.plusHours(1), null,
        null, true);
    Event e2 = new Event("E2", now.plusHours(1), now.plusHours(2), null,
        null, true);
    assertFalse(e1.conflictsWith(e2));
    assertFalse(e2.conflictsWith(e1));
  }

  @Test
  void testZeroDurationEventNoConflict() {
    ZonedDateTime time = ZonedDateTime.now(EST);
    Event e1 = new Event("E1", time, time, null, null, true);
    Event e2 = new Event("E2", time, time.plusHours(1), null,
        null, true);
    assertFalse(e1.conflictsWith(e2));
    assertFalse(e2.conflictsWith(e1));
  }

  @Test
  void testConflictAcrossTimeZones() {
    ZoneId utc = ZoneId.of("UTC");
    ZonedDateTime startEst = ZonedDateTime.of(2023, 10, 5, 10,
        0, 0, 0, EST);
    ZonedDateTime endEst = startEst.plusHours(1);
    ZonedDateTime startUtc = ZonedDateTime.of(2023, 10, 5, 14,
        0, 0, 0, utc);
    ZonedDateTime endUtc = startUtc.plusHours(2);

    Event e1 = new Event("E1", startEst, endEst, null,
        null, true);
    Event e2 = new Event("E2", startUtc, endUtc, null,
        null, true);
    assertTrue(e1.conflictsWith(e2));
    assertTrue(e2.conflictsWith(e1));
  }

  @Test
  void testEventWithinAnotherTime() {
    ZonedDateTime now = ZonedDateTime.now(EST);
    Event e1 = new Event("E1", now, now.plusHours(3), null, null,
        true);
    Event e2 = new Event("E2", now.plusHours(1), now.plusHours(2), null,
        null, true);
    assertTrue(e1.conflictsWith(e2));
    assertTrue(e2.conflictsWith(e1));
  }

  @Test
  void testAllDayEventConflictWithTimedEvent() {
    ZonedDateTime dayStart = ZonedDateTime.now(EST).withHour(0).withMinute(0);
    ZonedDateTime eventStart = dayStart.plusHours(10);
    Event allDay = new Event("All Day", dayStart, null, null, null,
        true);
    Event timed = new Event("Timed", eventStart, eventStart.plusHours(1), null,
        null, true);
    assertTrue(allDay.conflictsWith(timed));
    assertTrue(timed.conflictsWith(allDay));
  }

  @Test
  void testNullSubjectThrowsException() {
    ZonedDateTime start = ZonedDateTime.now(EST);
    assertThrows(NullPointerException.class, () ->
        new Event(null, start, null, null, null, true));
  }

  @Test
  void testEventsWithSameTimesConflict() {
    ZonedDateTime start = ZonedDateTime.now(EST);
    ZonedDateTime end = start.plusHours(1);
    Event e1 = new Event("E1", start, end, null, null, true);
    Event e2 = new Event("E2", start, end, null, null, true);
    assertTrue(e1.conflictsWith(e2));
    assertTrue(e2.conflictsWith(e1));
  }

  @Test
  void testAllDayEventConflictWithSpanningEvent() {
    ZonedDateTime day = ZonedDateTime.of(2023, 10, 5, 0, 0,
        0, 0, EST);
    Event allDay = new Event("All Day", day, null, null, null,
        true);
    ZonedDateTime spanningStart = day.plusHours(23);
    Event spanning = new Event("Spanning", spanningStart, spanningStart.plusHours(2),
        null, null, true);
    assertTrue(allDay.conflictsWith(spanning));
    assertTrue(spanning.conflictsWith(allDay));
  }

  @Test
  void testAllDayEventsDifferentDaysNoConflict() {
    ZonedDateTime day1 = ZonedDateTime.now(EST).withHour(0).withMinute(0);
    ZonedDateTime day2 = day1.plusDays(1);
    Event e1 = new Event("E1", day1, null, null, null, true);
    Event e2 = new Event("E2", day2, null, null, null, true);
    assertFalse(e1.conflictsWith(e2));
    assertFalse(e2.conflictsWith(e1));
  }

  @Test
  void testAllDayEventWithNonMidnightStart() {
    ZonedDateTime start = ZonedDateTime.now(EST).withHour(10).withMinute(0);
    Event event = new Event("Non-Midnight All Day", start, null, null,
        null, true);
    ZonedDateTime timedStart = start.withHour(14);
    Event timedEvent = new Event("Timed", timedStart, timedStart.plusHours(1),
        null, null, true);
    assertTrue(event.conflictsWith(timedEvent));
    assertTrue(timedEvent.conflictsWith(event));
  }

  @Test
  void testAllDayEventConflict() {
    ZonedDateTime day = ZonedDateTime.now(EST).withHour(0).withMinute(0);
    Event e1 = new Event("E1", day, null, null, null, true);
    Event e2 = new Event("E2", day, null, null, null, true);
    assertTrue(e1.conflictsWith(e2));
  }
}
