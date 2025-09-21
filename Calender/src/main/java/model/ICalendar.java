package model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Interface for calendar operations.
 */
public interface ICalendar {
  String getName();
  void setName(String name);
  ZoneId getTimezone();
  void setTimezone(ZoneId timezone);
  boolean addEvent(IEvent event);
  boolean addRecurringEvent(RecurringEvent recurringEvent);
  List<IEvent> getEventsInRange(ZonedDateTime from, ZonedDateTime to);
  boolean isBusy(ZonedDateTime time);
  void editEventInstance(ZonedDateTime start, String property, String value);
  Event getEventAt(ZonedDateTime start);
}