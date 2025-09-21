package model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single calendar with events.
 */
public class Calendar implements ICalendar {
  private String name;
  private ZoneId timezone;
  private List<IEvent> events;

  public Calendar(String name, ZoneId timezone) {
    this.name = name;
    this.timezone = timezone;
    this.events = new ArrayList<>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public ZoneId getTimezone() {
    return timezone;
  }

  @Override
  public void setTimezone(ZoneId timezone) {
    this.timezone = timezone;
  }

  @Override
  public boolean addEvent(IEvent event) {
    events.add(event);
    return true;
  }

  @Override
  public boolean addRecurringEvent(RecurringEvent recurringEvent) {
    events.add(recurringEvent);
    return true;
  }

  @Override
  public List<IEvent> getEventsInRange(ZonedDateTime from, ZonedDateTime to) {
    List<IEvent> result = new ArrayList<>();
    for (IEvent event : events) {
      if (event.getStart().isAfter(from) && (event.getEnd() == null || event.getEnd().isBefore(to))) {
        result.add(event);
      }
    }
    return result;
  }

  @Override
  public boolean isBusy(ZonedDateTime time) {
    for (IEvent event : events) {
      if (event.getStart().isBefore(time) && (event.getEnd() == null || event.getEnd().isAfter(time))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void editEventInstance(ZonedDateTime start, String property, String value) {
    for (IEvent event : events) {
      if (event.getStart().equals(start)) {
        switch (property.toLowerCase()) {
          case "name":
            event.setSubject(value);
            break;
          default:
            throw new IllegalArgumentException("Unknown property: " + property);
        }
      }
    }
  }

  @Override
  public Event getEventAt(ZonedDateTime start) {
    for (IEvent event : events) {
      if (event.getStart().equals(start)) {
        return (Event) event;
      }
    }
    return null;
  }
}