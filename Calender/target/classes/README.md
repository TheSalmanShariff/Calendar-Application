# Calendar Application README 

## Instructions
**To run in interactive mode**:

java -cp bin Main --mode interactive

**To run in headless mode with a command file**:

java -cp bin Main --mode headless commands.txt

## Features That Works/Didn't Work

| Features                                                                                                               | Status | Notes |
|------------------------------------------------------------------------------------------------------------------------|:------:|------:|
| create a single event                                                                                                  |  work  |       |
| create a single event with autoDecline                                                                                 |  work  |       |
| handle single event conflict                                                                                           |  work  |       |
| create recurring event that repeats N times                                                                            |  work  |       |
| create recurring event until a specific date (inclusive)                                                               |  work  |       |
| create a single all day event                                                                                          |  work  |       |
| create recurring all day event that repeats N times                                                                    |  work  |       |
| create recurring all day event until a specific date (inclusive)                                                       |  work  |       |
| Changes the property (e.g., name) of the given event                                                                   |  work  |       |
| Changes the property of all events starting at a specific date/time and have the same event name.                      |  work  |       |
| Change the property (e.g., name) of all events with the same event name.                                               |  work  |       |
| Prints bulleted list of all events on that day along with their start and end time and location (if any)               |  work  |       |
| Prints a bulleted list of all events in the given interval including their start and end times and location (if any).  |  work  |       |
| query calendar within a range of dates                                                                                 |  work  |       |
| query calendar for a specific date                                                                                     |  work  |       |
| query even if user is busy at a specific date/time                                                                     |  work  |       |
| export calendar to CSV                                                                                                 |  work  |       |
| program must halt with an error if a user enters an unexpected command                                                 |  work  |       |
| Prints busy status if the user has events scheduled on a given day and time, otherwise, available                      |  work  |       |
| interactive mode                                                                                                       |  work  |       |
| headless mode                                                                                                          |  work  |       |

## Team Contributions
We both did equal amount of work. We planned out how to approach the assignment together, and we planned out what data structures and file structure works best.

## Miscellaneous
N/A