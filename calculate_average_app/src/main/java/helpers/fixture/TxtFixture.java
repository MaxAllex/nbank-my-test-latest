package helpers.fixture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TxtFixture {
    private final Path path;
    private final List<String> lines = new ArrayList<>();

    public TxtFixture(Path path) {
        this.path = path;
    }

    public TxtFixture withLines(String... items) {
        lines.addAll(Arrays.asList(items));
        return this;
    }

    public Path write() {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, lines);
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write TxtFixture: " + path, e);
        }
    }
}