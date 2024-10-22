package com.cribl.interview.service;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private static final String LOG_DIRECTORY = "/var/log/";

    public List<String> getLogs(String filename, int limit, String keyword) {
        Path logFilePath = Paths.get(LOG_DIRECTORY + filename);

        try (Stream<String> lines = Files.lines(logFilePath)) {

            List<String> logList = lines.toList();
            List<String> reverseLogList = new ArrayList<>();
            ListIterator<String> listIterator = logList.listIterator(logList.size());

            // reverse list of logs
            while (listIterator.hasPrevious()){
                String logElement = listIterator.previous();
                reverseLogList.add(logElement);
            }

            return reverseLogList.stream()
                    // apply requested filter
                    .filter(line -> keyword == null || line.toLowerCase().contains(keyword.toLowerCase()))
                    // apply requested limit
                    .limit(limit)
                    .toList();

        } catch (IOException e) {
            logger.error("Failed to read log file: {}", filename);

            return List.of();
        }
    }
}
