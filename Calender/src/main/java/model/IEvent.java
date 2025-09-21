package model;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface IEvent {
  /**
   * Gets the subject of the event.
   * @return The subject of the event.
   */
  String getSubject();

  /**
   * Gets the start time of the event.
   * @return The start time of the event.
   */
  ZonedDateTime getStart();

  /**
   * Gets the end time of the event, or null if the event is all-day.
   * @return The end time of the event, or null for all-day events.
   */
  ZonedDateTime getEnd();

  /**
   * Gets the location of the event.
   * @return The location of the event.
   */
  String getLocation();

  /**
   * Gets the description of the event.
   * @return The description of the event.
   */
  String getDescription();

  /**
   * Checks whether the event is public or private.
   * @return true if the event is public, false if it is private.
   */
  boolean isPublic();

  /**
   * Gets the zoneID of the event.
   * @return The zoneID of the event.
   */
  ZoneId getZoneId();

  // Setters

  /**
   * Sets the subject of the event.
   * @param subject The new subject of the event.
   */
  void setSubject(String subject);

  /**
   * Sets the start time of the event.
   * @param start The new start time for the event.
   */
  void setStart(ZonedDateTime start);

  /**
   * Sets the end time of the event.
   * @param end The new end time for the event, or null if the event is all-day.
   */
  void setEnd(ZonedDateTime end);

  /**
   * Sets the location of the event.
   * @param location The new location for the event.
   */
  void setLocation(String location);

  /**
   * Sets the description of the event.
   * @param description The new description for the event.
   */
  void setDescription(String description);

  /**
   * Sets the publicity of the event.
   * @param isPublic True to make event public, false to make event private.
   */
  void setPublic(boolean isPublic);

  /**
   * Sets the zoneID of the event.
   * Changing the zoneID of the event will automatically change the start and end date to the corresponding new zone.
   * @param zoneId The new zoneId for the event.
   * @throws IllegalArgumentException If zoneId is null
   */
  void setZoneId(ZoneId zoneId);


  /**
   * Checks if this event conflicts with another event.
   * A conflict occurs if the events overlap in time.
   * @param other The other event to check against.
   * @return true if the events overlap in time, false otherwise.
   */
  boolean conflictsWith(Event other);

  /**
   * Returns a string representation of the event
   * @return A pretty string format of the event
   */
  @Override
  String toString();

  boolean conflictsWith(IEvent other);

  /**
   * Compares this Event object with another object to determine if they are equal.
   * Two events are considered equal if they have the same subject, start time,
   * end time (if not null), location (if not null), description (if not null), and visibility (isPublic).
   * This method handles null values for fields like subject, start, and end,
   *
   * @param obj other object to compare to
   * @return true if the two events are equal, false otherwise
   */
  @Override
  boolean equals(Object obj);

  @Override
  int hashCode();
}
