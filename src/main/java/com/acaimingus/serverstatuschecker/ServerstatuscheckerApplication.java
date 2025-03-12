package com.acaimingus.serverstatuschecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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

	    static List<Integer> getPIDList() {
        try {
            // Get all lines from the pid file
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/pidlist.txt"));

            // Trim whitespace
            lines.replaceAll(String::trim);

            // Create a list of pids and convert the lines-list into the pid list
            List<Integer> pids = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                try {
                    pids.add(Integer.parseInt(lines.get(i)));
                } catch (NumberFormatException e) {
                    System.err.println("Line " + i + " failed to parse to int: " + e.getMessage());
                }
            }

            return pids;
        } catch (IOException e) {
            System.err.println("Couldn't read the pids: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    static String checkProcess(int processID) {
        boolean isRunning = ProcessHandle.allProcesses().map(ProcessHandle::pid).anyMatch(pid -> pid == processID);
        return isRunning ? "UP" : "DOWN";
    }

	@GetMapping("/status")
	static List<String> getProcessStatuses() {
		List<String> output = new ArrayList<>();
		List<Integer> pids = getPIDList();

		for (int pid : pids) {
			output.add(checkProcess(pid));
		}

		return output;
	}
}
