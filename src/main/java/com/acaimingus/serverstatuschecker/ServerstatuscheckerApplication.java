package com.acaimingus.serverstatuschecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ServerstatuscheckerApplication {
    /** String that holds the path to the file with the processes to monitor */
    private static String pidFilePath;

    /**
     * Main method, checks if the user specified a path and then runs the Spring server
     * @param args Pass the path as the first argument here, rest doesn't matter
     */
    public static void main(String[] args) {
        // Check if the user specified an argument
        if (args.length == 0) {
            System.err.println("You need to specify a file with the processes to monitor as an argument!");
            System.exit(1);
        }

        // Treat the first argument as a path
        pidFilePath = args[0];

        // Start Spring
        SpringApplication.run(ServerstatuscheckerApplication.class, args);
    }

    /**
     * Method to create a map wit PIDs and corresponding names to pass to the web server
     * @param filePath Path of the file with the PIDs you wish to be monitored
     * @return Map with PIDs as the key and names as the value
     */
    static Map<Integer, String> getPIDMap(String filePath) {
        try {
            // Get all lines from the pid file
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            // Trim whitespace
            lines.replaceAll(String::trim);

            // Create a map with the names of the processes and the PIDs and populate valid entries
            Map<Integer, String> output = new LinkedHashMap<>();
            for (int i = 0; i < lines.size() - 1; i += 2) {
                String processName = lines.get(i);
                try {
                    int pid = Integer.parseInt(lines.get(i+1));
                    output.put(pid, processName);
                } catch (NumberFormatException e) {
                    System.err.println("Process " + processName + ": Failed to parse PID: " + e.getMessage());
                }
            }
            // Return the map
            return output;
        } catch (IOException e) {
            // File was not found, inform user and return an empty map
            System.err.println("Couldn't read the pids: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Small helper method that checks if the specified process ID is running on the system
     * @param processID the given process ID
     * @return string for the HTML specifying the process state
     */
    static String checkProcess(int processID) {
        // Check if the process is runnning using the ProcessHandle and return the status based on that
        boolean isRunning = ProcessHandle.allProcesses().map(ProcessHandle::pid).anyMatch(pid -> pid == processID);
        return isRunning ? "UP" : "DOWN";
    }

    /**
     * Method called by the websites JavaScript to check it's PID list for the corresponding statuses
     * @return List with the data to be displayed on the website
     */
    @GetMapping("/status")
    static Map<String, String> getProcessStatuses() {
        // Map that will be used to output the given data
        Map<String, String> output = new LinkedHashMap<>();
        // Map of processes to monitor given in the configuration file
        Map<Integer, String> pidMap = getPIDMap(pidFilePath);

        // Convert the hashmap with the PID into a a Hashmap with the data for the html display
        for (Map.Entry<Integer, String> entry : pidMap.entrySet()) {
            int pid = entry.getKey();
            String processName = entry.getValue();
            // Put the Name + PID as key and put the status as the value
            output.put(processName + "(" + pid + ")", checkProcess(pid)); 
        }

        return output;
    }
}
