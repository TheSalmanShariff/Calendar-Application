package model;

import java.time.ZonedDateTime;
import java.util.HashMap;

public interface ICalendarManager {
  /**
   * Returns the current active calendar the user is on.
   * @return a Calendar object.
   */
  Calendar getCurrentCalendar();

  /**
   * Returns all the calendars in CalendarManager.
   * @return A hashmap of all the calendars.
   */
  HashMap<String, Calendar> getCalendars();

  /**
   * Set current calendar to another existing calendar
   * @param calendarName the name of the calendar you want to switch to
   * @throws IllegalArgumentException if calendar does not exist
   */
  void setCurrentCalendar(String calendarName) throws IllegalArgumentException;

  /**
   * Retrieve an existing calendar by its name
   * @param name the name of the calendar
   * @return the calendar
   * @throws IllegalArgumentException if calendar does not exist
   */
  Calendar getCalendar(String name) throws IllegalArgumentException;

  /**
   * Rename an existing calendar's name
   * @param oldName the name of the calendar you wish to rename
   * @param newName the new name for the calendar (case-sensitive)
   * @throws IllegalArgumentException if calendar does not exist,
   *                                  or if newName is already taken,
   *                                  or if newName is empty or null
   */
  void renameCalendar(String oldName, String newName) throws IllegalArgumentException;

  /**
   * Delete an existing calendar
   * @param calendarName the name of the calendar to be deleted (case-sensitive)
   * @throws IllegalArgumentException if calendar does not exist,
   *                                  or if deleting will result in no more calendars in CalendarManager
   */
  void deleteCalendar(String calendarName) throws IllegalArgumentException;

  /**
   * Adds in a new calendar to CalendarManager
   * @param calendar New calendar to be added to CalendarManager
   * @throws IllegalArgumentException A calendar with the same name exists in CalendarManager
   */
  void addCalendar(Calendar calendar) throws IllegalArgumentException;

  /**
   * Copy a single event (or recurring event) from current calendar to a target
   * calendar with (different or same) date and time.
   * Source and target calendar can be the same calendar.
   *            @param target the target Calendar's name to copy the event to (case-sensitive)
   *            @param event the event or recurring event to be copied
   *            @param startingDate the desired starting date and time for the copied event.
   *            @throws IllegalArgumentException If event does not exist in current calendar,
   *                                              or if target calendar does not exist
   */
  void copyEvent(String target, IEvent event, ZonedDateTime startingDate) throws IllegalArgumentException;

  /**
   * Copy multiple events from current calendar with a date range with
   * (different or same) date and time to a target calendar.
   * Source and target calendar can be the same calendar.
   *            @param target the target Calendar's name to copy the event to
   *            @param from the starting date range of the events to be copied
   *            @param to the end date range of the events to be copied
   *            @throws IllegalArgumentException if target calendar does not exist
   */
  void copyEventsInRange(String target, ZonedDateTime from, ZonedDateTime to) throws IllegalArgumentException;
}
