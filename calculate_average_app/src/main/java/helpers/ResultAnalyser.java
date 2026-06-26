package helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultAnalyser {
    private static final String RESULT_FILE = Paths.get(System.getProperty("user.dir"), "script", "averages.csv").toString();
    private List<String[]> records = new LinkedList<>();

    public void reset() {
        records.clear();
    }

    public void containsRecord(String user, String month, double averageAmount) {
        loadIfNeeded();

        String[] userRecord = records.stream().filter(record -> record[0].equals(user))
                .findFirst().get();

        assertEquals(month, userRecord[1]);
        assertEquals(averageAmount, Double.parseDouble(userRecord[2]));
    }

    public void hasOnlyHeader() {
        loadIfNeeded();
        assertTrue(records.isEmpty(), "Expected no data records, but found: " + records.size());
    }

    public void hasNRecords(int expected) {
        loadIfNeeded();
        assertEquals(expected, records.size(),
                "Expected " + expected + " records but got " + records.size());
    }

    public void doesNotContainDuplicateUsers() {
        loadIfNeeded();
        long uniqueCount = records.stream().map(r -> r[0]).distinct().count();
        assertEquals(records.size(), uniqueCount,
                "Found duplicate users in output: " + records.size() + " rows, " + uniqueCount + " unique users");
    }

    private void loadIfNeeded() {
        if (records.isEmpty()) {
            readRecords();
        }
    }

    private void readRecords() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(RESULT_FILE));
            if (lines.size() <= 1) {
                return;
            }
            for (String part : lines.get(1).split("\\\\n")) {
                if (!part.isEmpty()) {
                    records.add(part.split(","));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
