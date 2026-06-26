package helpers.fixture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvFixture {
    private final Path path;
    private String header;
    private final List<String[]> rows = new ArrayList<>();

    public CsvFixture(Path path) {
        this.path = path;
    }

    public CsvFixture withHeader(String... columns) {
        this.header = String.join(",", columns);
        return this;
    }

    public CsvFixture withRow(String... values) {
        rows.add(values);
        return this;
    }

    public Path write() {
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            if (header != null) lines.add(header);
            for (String[] row : rows) lines.add(String.join(",", row));
            Files.write(path, lines);
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CsvFixture: " + path, e);
        }
    }
}