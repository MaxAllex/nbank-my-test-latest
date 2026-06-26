package helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CsvOutputReader {
    private final Path path;
    private List<String[]> records;

    public CsvOutputReader(Path path) {
        this.path = path;
    }

    public CsvOutputReader hasOnlyHeader() {
        assertTrue(records().isEmpty(),
                "Expected only header but found " + records().size() + " data rows in " + path);
        return this;
    }

    public CsvOutputReader hasNRecords(int expected) {
        assertEquals(expected, records().size(),
                "Expected " + expected + " records but got " + records().size() + " in " + path);
        return this;
    }

    public CsvOutputReader containsRecord(String... values) {
        boolean found = records().stream().anyMatch(row -> rowMatches(row, values));
        assertTrue(found, "Record [" + String.join(",", values) + "] not found in " + path);
        return this;
    }

    public CsvOutputReader doesNotContainRecord(String firstColumnValue) {
        boolean found = records().stream().anyMatch(row -> row.length > 0 && row[0].equals(firstColumnValue));
        assertFalse(found, "Expected no record with '" + firstColumnValue + "' but it was found in " + path);
        return this;
    }

    public CsvOutputReader noDuplicatesInColumn(int columnIndex) {
        long unique = records().stream()
                .filter(row -> row.length > columnIndex)
                .map(row -> row[columnIndex])
                .distinct().count();
        assertEquals(records().size(), unique,
                "Found duplicates in column " + columnIndex + " of " + path);
        return this;
    }

    private List<String[]> records() {
        if (records == null) {
            try {
                records = Files.readAllLines(path).stream()
                        .skip(1)
                        .filter(l -> !l.isBlank())
                        .map(l -> l.split(","))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read output file: " + path, e);
            }
        }
        return records;
    }

    private boolean rowMatches(String[] row, String[] values) {
        if (row.length < values.length) return false;
        for (int i = 0; i < values.length; i++) {
            if (!row[i].equals(values[i])) return false;
        }
        return true;
    }
}