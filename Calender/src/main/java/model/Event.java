package model;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Event implements IEvent {
  private String subject;
  private ZonedDateTime start;
  private ZonedDateTime end;
  private String location;
  private String description;
  private boolean isPublic;
  private ZoneId zoneId;

  public Event(String subject, ZonedDateTime start, ZonedDateTime end,
      String location, String description, boolean isPublic) {
    if (start == null) throw new IllegalArgumentException("Start time cannot be null");
    this.subject = subject != null ? subject : "";
    this.start = start;
    this.end = end;
    this.location = location;
    this.description = description;
    this.isPublic = isPublic;
    this.zoneId = start.getZone();
  }

  // Interface implementation
  @Override
  public String getSubject() { return subject; }

  @Override
  public ZonedDateTime getStart() { return start; }

  @Override
  public ZonedDateTime getEnd() { return end; }

  @Override
  public String getLocation() { return location; }

  @Override
  public String getDescription() { return description; }

  @Override
  public boolean isPublic() { return isPublic; }

  @Override
  public ZoneId getZoneId() { return zoneId; }

  @Override
  public void setSubject(String subject) {
    this.subject = subject != null ? subject : "";
  }

  @Override
  public void setStart(ZonedDateTime start) {
    if (start == null) throw new IllegalArgumentException("Start time cannot be null");
    this.start = start;
    maintainTimeZoneConsistency();
  }

  @Override
  public void setEnd(ZonedDateTime end) {
    this.end = end;
    maintainTimeZoneConsistency();
  }

  @Override
  public void setLocation(String location) {
    this.location = location;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  @Override
  public void setZoneId(ZoneId zoneId) {
    if (zoneId == null) throw new IllegalArgumentException("Zone ID cannot be null");
    this.zoneId = zoneId;
    maintainTimeZoneConsistency();
  }

  @Override
  public boolean conflictsWith(Event other) {
    return false;
  }

  private void maintainTimeZoneConsistency() {
    this.start = start.withZoneSameInstant(zoneId);
    if (end != null) {
      this.end = end.withZoneSameInstant(zoneId);
    }
  }

  @Override
  public boolean conflictsWith(IEvent other) {
    if (other == null) return false;

    ZonedDateTime thisStart = this.start;
    ZonedDateTime thisEnd = this.end != null ? this.end : thisStart.plusDays(1);
    ZonedDateTime otherStart = other.getStart();
    ZonedDateTime otherEnd = other.getEnd() != null ? other.getEnd() : otherStart.plusDays(1);

    return thisStart.isBefore(otherEnd) && thisEnd.isAfter(otherStart);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Event)) return false;
    Event event = (Event) o;
    return isPublic() == event.isPublic() &&
        Objects.equals(getSubject(), event.getSubject()) &&
        Objects.equals(getStart(), event.getStart()) &&
        Objects.equals(getEnd(), event.getEnd()) &&
        Objects.equals(getLocation(), event.getLocation()) &&
        Objects.equals(getDescription(), event.getDescription()) &&
        Objects.equals(getZoneId(), event.getZoneId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSubject(), getStart(), getEnd(),
        getLocation(), getDescription(),
        isPublic(), getZoneId());
  }

  @Override
  public String toString() {
    return String.format("Event[%s, %s to %s, %s]",
        subject,
        start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        end != null ? end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "ALL DAY",
        zoneId);
  }

  // Additional helper methods
  public boolean isAllDay() {
    return end == null;
  }

  public Duration getDuration() {
    return end != null ? Duration.between(start, end) : Duration.ofDays(1);
  }
}