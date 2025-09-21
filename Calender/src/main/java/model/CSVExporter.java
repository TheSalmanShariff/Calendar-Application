package model;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class is responsible for exporting calendar events to a CSV file format,
 * which is compatible with tools like Google Calendar or any CSV-compatible calendar app.
 */
public class CSVExporter {

  // Formatter for dates (MM/dd/yyyy) and times (HH:mm:ss)
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");


  /**
   * Exports all the events from a calendar to a CSV file.
   * The CSV file contains fields such as subject, start time, end time, location, etc.
   * @param calendar The calendar object that holds all events to export.
   * @param fileName The name of the output CSV file.
   * @return The absolute path of the generated CSV file.
   * @throws IOException If there is an issue writing to the file (e.g., file system issues).
   */
  public String export(Calendar calendar, String fileName) throws IOException {
    // Generate the absolute path of the CSV file
    String path = Paths.get(fileName).toAbsolutePath().toString();

    // Try-with-resources to automatically close the FileWriter when done
    try (FileWriter writer = new FileWriter(fileName)) {

      // Write the CSV header row (columns)
      writer.write("Subject,Start Date,Start Time,End Date,"
          + "End Time,Location,Description,Private,ZoneID\n");

      // Define a far range for events: from 100 years ago to 100 years into the future
      ZonedDateTime farPast = ZonedDateTime.now().minusYears(100);
      ZonedDateTime farFuture = ZonedDateTime.now().plusYears(100);

      ZoneId zone = calendar.getTimezone();

      // Loop through all events in the calendar within the far past and future range
      for (IEvent e : calendar.getEventsInRange(farPast, farFuture)) {

        // Process the subject and escape any special characters for CSV format
        String subject = quoteField(e.getSubject());

        // Convert event start time to EST and format it
        ZonedDateTime startEST = e.getStart().withZoneSameInstant(zone);
        String startDate = startEST.format(DATE_FORMAT);
        String startTime = startEST.format(TIME_FORMAT);

        // Handle the end time (if any) similarly to the start time
        String endDate;
        String endTime;
        if (e.getEnd() != null) {
          // Regular event with end time
          ZonedDateTime endEST = e.getEnd().withZoneSameInstant(zone);
          endDate = endEST.format(DATE_FORMAT);
          endTime = endEST.format(TIME_FORMAT);
        } else {
          // If the event is all-day, we handle it differently
          endDate = startDate;   // All-day event ends the same day
          endTime = "";          // No specific end time for all-day events
          startTime = "00:00";   // All-day events typically start at midnight
        }

        // Process location and description, escaping special characters if needed
        String location = quoteField(e.getLocation() != null ? e.getLocation() : "");
        String description = quoteField(e.getDescription() != null ? e.getDescription() : "");

        // Determine the event's privacy setting (public or private)
        String privacy = e.isPublic() ? "No" : "Yes";

        // Write the event data into the CSV file in the appropriate format
        writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s.%s\n",
            subject, startDate, startTime, endDate, endTime, location, description, privacy, zone));
      }
    }
    // Return the absolute path of the generated file
    return path;
  }

  /**
   * Helper method to process a field and quote it if it contains special characters.
   * CSV fields with commas, quotes, or newlines need to be properly escaped.
   * @param field The field to process, which may contain commas, quotes, or newlines.
   * @return The processed field, quoted and escaped if necessary.
   */
  private String quoteField(String field) {
    if (field == null || field.isEmpty()) {
      return ""; // Return an empty string if the field is null or empty
    }
    // If the field contains any special CSV characters (comma, quote, newline)
    if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
      // Escape the embedded quotes by replacing them with double quotes
      field = field.replace("\"", "\"\"");
      // Quote the field to make it valid in CSV format
      return "\"" + field + "\"";
    }
    return field; // Return the field as-is if no special characters are present
  }
}
