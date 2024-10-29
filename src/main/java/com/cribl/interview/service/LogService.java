package com.cribl.interview.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private final String logDirectory;

    public LogService(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    public List<String> getLogs(String filename, int limit, String keyword) {
        return readFile(filename, limit, keyword);
    }

    private List<String> readFile(String filename, int limit, String keyword) {
        Stack<String> stack = new Stack<>();
        List<String> result = new ArrayList<>();

        try {
            Path filePath = Paths.get(logDirectory + filename);
            BufferedReader bufferedReader = Files.newBufferedReader(filePath);
            String line;

            // read file line by line
            while ((line = bufferedReader.readLine()) != null) {
                // if requested filter applies, push line to stack
                if (keyword == null || line.toLowerCase().contains(keyword.toLowerCase())) {
                    stack.push(line);
                }
            }

            bufferedReader.close();

            int counter = 0;
            // apply requested limit
            while (!stack.empty() && counter < limit) {
                result.add(stack.pop());    // fill result list with reversed file content
                counter++;
            }
        } catch (Exception e) {
            logger.error("Failed to read log file: {}", filename);
        }

        return result;
    }
}
