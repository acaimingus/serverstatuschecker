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

    public static void main(String[] args) {
        SpringApplication.run(ServerstatuscheckerApplication.class, args);
    }

    static Map<Integer, String> getPIDMap() {
        try {
            // Get all lines from the pid file
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/pidlist.txt"));

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

    static String checkProcess(int processID) {
        // Check if the process is runnning using the ProcessHandle and return the status based on that
        boolean isRunning = ProcessHandle.allProcesses().map(ProcessHandle::pid).anyMatch(pid -> pid == processID);
        return isRunning ? "UP" : "DOWN";
    }

    @GetMapping("/status")
    static Map<String, String> getProcessStatuses() {
        Map<String, String> output = new LinkedHashMap<>();
        Map<Integer, String> pidMap = getPIDMap();

        // Convert the hashmap with the PID into a a Hashmap with the data for the html display
        for (Map.Entry<Integer, String> entry : pidMap.entrySet()) {
            int pid = entry.getKey();
            String processName = entry.getValue();
            output.put(processName + "(" + pid + ")", checkProcess(pid)); 
        }

        return output;
    }
}
