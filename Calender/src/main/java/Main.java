import controller.CalendarController;
import model.CalendarManager;
import view.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Main entry point for the calendar application.
 */
public class Main {
  public static void main(String[] args) {
    if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")) {
      System.out.println("Usage: --mode [interactive|headless file]");
      return;
    }
    CalendarManager calendarManager = new CalendarManager();
    TextView view = new TextView();
    CalendarController controller = new CalendarController(calendarManager, view);
    try {
      if (args[1].equalsIgnoreCase("interactive")) {
        runInteractive(controller);
      } else if (args[1].equalsIgnoreCase("headless") && args.length == 3) {
        runHeadless(controller, args[2]);
      } else {
        System.out.println("Invalid mode or arguments.");
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private static void runInteractive(CalendarController controller) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String command;
    while ((command = reader.readLine()) != null) {
      try {
        if (!controller.processCommand(command)) {
          break;
        }
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private static void runHeadless(CalendarController controller, String file) throws Exception {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String command;
      while ((command = reader.readLine()) != null) {
        try {
          if (!controller.processCommand(command)) {
            break;
          }
        } catch (IllegalArgumentException e) {
          System.out.println(e.getMessage());
          break;
        }
      }
    }
  }
}