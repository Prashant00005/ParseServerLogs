# ParseServerLogs
A custom-build server logs different events to a file named logfile.txt. Every event has 2 entries in the file - one entry when the event was started and another when the event was finished. The entries in the file have no specific order (a finish event could occur before a start event for a given id).

The program takes the path to logfile.txt as an input argument, parse the contents of logfile.txt and flag any long events that take longer than 4ms.

Instructions to run the application:
  * Download the source code
  * Navigate to /ServerLogsProject/src/main/java/com/cs/ServerLogsProject folder
  * Execute the following command: "java CreateServerLogs.java path_to_logfile.txt"
