package com.cribl.interview.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LogServiceTest {

    private static final String testDirectory = "/test/path/";
    private static final String testFilename = "test.log";
    private static final int testLimit = 3;
    List<String> mockLines = List.of(
        "first line",
        "second line",
        "third line",
        "fourth line"
    );

    private final LogService logService = new LogService(testDirectory);

    @Test
    void testGetLogsCatchException() {
        // arrange
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            Path mockPath = Mockito.mock(Path.class);
            BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);

            mockPaths.when(() -> Paths.get(testDirectory + testFilename)).thenReturn(mockPath);
            mockFiles.when(() -> Files.newBufferedReader(mockPath)).thenReturn(mockBufferedReader);
            when(mockBufferedReader.readLine()).thenThrow(new IOException("File not found"));

            // act & assert
            assertDoesNotThrow(() -> logService.getLogs(testFilename, testLimit, null));
        } catch (IOException e) {
            fail("Failure: IOException thrown");
        }
    }

    @Test
    void testGetLogsWithEmptyFile() {
        // arrange
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            Path mockPath = Mockito.mock(Path.class);
            BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);

            mockPaths.when(() -> Paths.get(testDirectory + testFilename)).thenReturn(mockPath);
            mockFiles.when(() -> Files.newBufferedReader(mockPath)).thenReturn(mockBufferedReader);
            when(mockBufferedReader.readLine()).thenReturn(null);

            // act
            List<String> result = logService.getLogs(testFilename, testLimit, null);

            // assert
            assertEquals(0, result.size());
        } catch (IOException e) {
            fail("Failure: IOException thrown");
        }
    }

    @Test
    void testGetLogsWithLimitOfZero() {
        // arrange
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            Path mockPath = Mockito.mock(Path.class);
            BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);

            mockPaths.when(() -> Paths.get(testDirectory + testFilename)).thenReturn(mockPath);
            mockFiles.when(() -> Files.newBufferedReader(mockPath)).thenReturn(mockBufferedReader);
            when(mockBufferedReader.readLine())
                    .thenReturn(mockLines.get(0))
                    .thenReturn(mockLines.get(1))
                    .thenReturn(mockLines.get(2))
                    .thenReturn(mockLines.get(3))
                    .thenReturn(null);  // simulate end of file

            // act
            List<String> result = logService.getLogs(testFilename, 0, null);

            // assert
            assertEquals(0, result.size());
        } catch (IOException e) {
            fail("Failure: IOException thrown");
        }
    }

    @Test
    void testGetLogsWithoutKeyword() {
        // arrange
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            Path mockPath = Mockito.mock(Path.class);
            BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);

            mockPaths.when(() -> Paths.get(testDirectory + testFilename)).thenReturn(mockPath);
            mockFiles.when(() -> Files.newBufferedReader(mockPath)).thenReturn(mockBufferedReader);
            when(mockBufferedReader.readLine())
                .thenReturn(mockLines.get(0))
                .thenReturn(mockLines.get(1))
                .thenReturn(mockLines.get(2))
                .thenReturn(mockLines.get(3))
                .thenReturn(null);  // simulate end of file

            // act
            List<String> result = logService.getLogs(testFilename, testLimit, null);

            // assert
            assertEquals(3, result.size());
            assertEquals("fourth line", result.get(0)); // latest line returned first
            assertEquals("third line", result.get(1));
            assertEquals("second line", result.get(2));
        } catch (IOException e) {
            fail("Failure: IOException thrown");
        }
    }

    @Test
    void testGetLogsWithoutKeywordMatch() {
        // arrange
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            Path mockPath = Mockito.mock(Path.class);
            BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);

            mockPaths.when(() -> Paths.get(testDirectory + testFilename)).thenReturn(mockPath);
            mockFiles.when(() -> Files.newBufferedReader(mockPath)).thenReturn(mockBufferedReader);
            when(mockBufferedReader.readLine())
                    .thenReturn(mockLines.get(0))
                    .thenReturn(mockLines.get(1))
                    .thenReturn(mockLines.get(2))
                    .thenReturn(mockLines.get(3))
                    .thenReturn(null);  // simulate end of file

            // act
            List<String> result = logService.getLogs(testFilename, testLimit, "not-found");

            // assert
            assertEquals(0, result.size());
        } catch (IOException e) {
            fail("Failure: IOException thrown");
        }
    }

    @Test
    void testGetLogsWithKeywordMatch() {
        // arrange
        List<String> mockLinesWithKeywordMatch = List.of(
            "first found line",
            "second found line",
            "third line",
            "fourth found line"
        );

        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            Path mockPath = Mockito.mock(Path.class);
            BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);

            mockPaths.when(() -> Paths.get(testDirectory + testFilename)).thenReturn(mockPath);
            mockFiles.when(() -> Files.newBufferedReader(mockPath)).thenReturn(mockBufferedReader);
            when(mockBufferedReader.readLine())
                    .thenReturn(mockLinesWithKeywordMatch.get(0))
                    .thenReturn(mockLinesWithKeywordMatch.get(1))
                    .thenReturn(mockLinesWithKeywordMatch.get(2))
                    .thenReturn(mockLinesWithKeywordMatch.get(3))
                    .thenReturn(null);  // simulate end of file

            // act
            List<String> result = logService.getLogs(testFilename, testLimit, "found");

            // assert
            assertEquals(3, result.size());
            assertEquals("fourth found line", result.get(0)); // latest line returned first
            assertEquals("second found line", result.get(1));
            assertEquals("first found line", result.get(2));
        } catch (IOException e) {
            fail("Failure: IOException thrown");
        }
    }
}