import model.RecurringEvent;
import model.Event;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for validating RecurringEvent functionality.
 * This includes tests for event creation, recurrence logic,
 * exceptions, and invalid scenarios.
 */
public class RecurringEventTest {

  private static final ZoneId EST = ZoneId.of("America/New_York");

  @Test
  void testRecurringEventCreationByOccurrences() {
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 3, 10, 0,
        0, 0, EST); // Monday, March 3, 2025
    RecurringEvent re = new RecurringEvent("Yoga", start, start.plusHours(1), null,
        null, true, "MW", 3, null);
    List<Event> instances = re.getInstances(start, start.plusWeeks(2));
    assertEquals(3, instances.size());
    assertEquals(start.plusDays(2), instances.get(1).getStart()); // Wednesday, March 5
    assertEquals(start.plusDays(7), instances.get(2).getStart()); // Next Monday, March 10
  }

  @Test
  void testInvalidMultiDayRecurringEvent() {
    ZonedDateTime start = ZonedDateTime.now(EST);
    ZonedDateTime end = start.plusDays(1);
    assertThrows(IllegalArgumentException.class, () ->
        new RecurringEvent("Invalid", start, end, null, null,
            true, "M", 1, null));
  }

  @Test
  void testAllDayRecurringEvent() {
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 3, 0, 0,
        0, 0, EST); // Monday, March 3, 2025
    RecurringEvent re = new RecurringEvent("All-Day Yoga", start, null, null,
        null,
        true, "MW", 3, null);
    List<Event> instances = re.getInstances(start, start.plusWeeks(2));
    assertEquals(3, instances.size());
    assertEquals(start, instances.get(0).getStart());
    assertEquals(start.plusDays(2), instances.get(1).getStart()); // Wednesday, March 5
    assertEquals(start.plusDays(7), instances.get(2).getStart()); // Monday, March 10
    assertNull(instances.get(0).getEnd());
  }

  @Test
  void testRecurringEventWithZeroOccurrences() {
    ZonedDateTime start = ZonedDateTime.now(EST);
    assertThrows(IllegalArgumentException.class, () ->
        new RecurringEvent("Invalid", start, start.plusHours(1), null,
            null,
            true, "M", 0, null));
  }

  @Test
  void testRecurringEventWithEndBeforeStart() {
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 3, 10, 0,
        0, 0, EST); // Monday, March 3, 2025
    ZonedDateTime endDate = start.minusDays(1);
    assertThrows(IllegalArgumentException.class, () ->
        new RecurringEvent("Invalid", start, start.plusHours(1), null,
            null, true, "M", null, endDate));
  }

  @Test
  void testInvalidWeekdays() {
    ZonedDateTime start = ZonedDateTime.now(EST);
    assertThrows(IllegalArgumentException.class, () ->
        new RecurringEvent("Invalid", start, start.plusHours(1), null,
            null, true, "ABC", 1, null));
  }

  @Test
  void testMultipleOccurrences() {
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 3, 10, 0,
        0, 0, EST); // Monday, March 3, 2025
    RecurringEvent re = new RecurringEvent("Yoga", start, start.plusHours(1), null,
        null, true, "MWF", 10, null);
    List<Event> instances = re.getInstances(start, start.plusWeeks(4));
    assertEquals(10, instances.size());
  }

  @Test
  void testAddException() {
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 3, 10, 0,
        0, 0, EST); // Monday, March 3, 2025
    RecurringEvent re = new RecurringEvent("Yoga", start, start.plusHours(1), null,
        null, true, "M", 2, null);
    Event exception = new Event("Canceled", start.plusDays(7),
        start.plusDays(7).plusHours(1), null, null, true);
    re.addException(start.plusDays(7), exception);
    List<Event> instances = re.getInstances(start, start.plusWeeks(1).plusHours(1));
    assertEquals(2, instances.size());
    assertEquals("Canceled", instances.get(1).getSubject());
  }

  @Test
  void testRecurringEventCreationByEndDate() {
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 10, 10, 0,
        0, 0, EST); // Monday, March 10, 2025
    ZonedDateTime endDate = ZonedDateTime.of(2025, 3, 20, 10,
        0, 0, 0, EST); // Thursday, March 20, 2025
    RecurringEvent re = new RecurringEvent("Yoga", start, start.plusHours(1), null,
        null, true, "MW", null, endDate);
    List<Event> instances = re.getInstances(start, endDate.plusHours(1));
    assertEquals(4, instances.size());
  }

  @Test
  void testInstancesOutsideRange() {
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 3, 10, 0,
        0, 0, EST); // Monday, March 3, 2025
    RecurringEvent re = new RecurringEvent("Yoga", start, start.plusHours(1), null,
        null, true, "MW", 3, null);
    ZonedDateTime from = start.plusDays(3);
    ZonedDateTime to = start.plusDays(6);
    List<Event> instances = re.getInstances(from, to);
    assertEquals(0, instances.size());
  }
}
