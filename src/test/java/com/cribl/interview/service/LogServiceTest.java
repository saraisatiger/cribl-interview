package com.cribl.interview.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogServiceTest {

    private static final String DIRECTORY = "/var/log/";
    private static final String FILENAME = "system.log";

    @InjectMocks
    private LogService logService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLogsWithIOException() {
        // arrange
        int limit = 3;
        String keyword = null;

        // mock static file path
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath = Path.of(DIRECTORY + FILENAME);
            mockedFiles.when(() -> Files.lines(mockPath)).thenThrow(new IOException("File not found"));

            // act & assert
            assertDoesNotThrow(() -> logService.getLogs(FILENAME, limit, keyword));
        }
    }

    @Test
    void testGetLogsWithEmptyFile() {
        // arrange
        int limit = 3;
        String keyword = null;

        List<String> mockLogs = List.of();

        // mock static file path
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath = Path.of(DIRECTORY + FILENAME);
            Stream<String> mockStream = mockLogs.stream();
            mockedFiles.when(() -> Files.lines(mockPath)).thenReturn(mockStream);

            // act
            List<String> result = logService.getLogs(FILENAME, limit, keyword);

            // assert
            assertEquals(0, result.size());
        }
    }

    @Test
    void testGetLogsWithLimitOfZero() {
        // arrange
        int limit = 0;
        String keyword = null;

        List<String> mockLogs = Arrays.asList(
                "first line",
                "second line",
                "third line",
                "fourth line"
        );

        // mock static file path
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath = Path.of(DIRECTORY + FILENAME);
            Stream<String> mockStream = mockLogs.stream();
            mockedFiles.when(() -> Files.lines(mockPath)).thenReturn(mockStream);

            // act
            List<String> result = logService.getLogs(FILENAME, limit, keyword);

            // assert
            assertEquals(0, result.size());
        }
    }

    @Test
    void testGetLogsWithoutKeyword() {
        // arrange
        int limit = 3;
        String keyword = null;

        List<String> mockLogs = Arrays.asList(
                "first line",
                "second line",
                "third line",
                "fourth line"
        );

        // mock static file path
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath = Path.of(DIRECTORY + FILENAME);
            Stream<String> mockStream = mockLogs.stream();
            mockedFiles.when(() -> Files.lines(mockPath)).thenReturn(mockStream);

            // act
            List<String> result = logService.getLogs(FILENAME, limit, keyword);

            // assert
            assertEquals(3, result.size());
            assertEquals("fourth line", result.get(0)); // latest line returned first
            assertEquals("third line", result.get(1));
            assertEquals("second line", result.get(2));
        }
    }

    @Test
    void testGetLogsWithoutKeywordMatch() {
        // arrange
        int limit = 3;
        String keyword = "find me";

        List<String> mockLogs = Arrays.asList(
                "first not found",
                "second not found",
                "third not found",
                "fourth not found"
        );

        // mock static file path
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath = Path.of(DIRECTORY + FILENAME);
            Stream<String> mockStream = mockLogs.stream();
            mockedFiles.when(() -> Files.lines(mockPath)).thenReturn(mockStream);

            // act
            List<String> result = logService.getLogs(FILENAME, limit, keyword);

            // assert
            assertEquals(0, result.size());
        }
    }

    @Test
    void testGetLogsWithKeywordMatch() {
        // arrange
        int limit = 3;
        String keyword = "find me";

        List<String> mockLogs = Arrays.asList(
                "find me first",
                "second line",
                "find me last",
                "fourth line"
        );

        // mock static file path
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath = Path.of(DIRECTORY + FILENAME);
            Stream<String> mockStream = mockLogs.stream();
            mockedFiles.when(() -> Files.lines(mockPath)).thenReturn(mockStream);

            // act
            List<String> result = logService.getLogs(FILENAME, limit, keyword);

            // assert
            assertEquals(2, result.size());
            assertEquals("find me last", result.get(0));    // latest line returned first
            assertEquals("find me first", result.get(1));
        }
    }
}