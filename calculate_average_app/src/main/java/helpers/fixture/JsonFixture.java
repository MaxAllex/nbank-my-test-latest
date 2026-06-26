package helpers.fixture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonFixture {
    public enum Mode { ARRAY, OBJECT }

    private final Path path;
    private final Mode mode;
    private final List<String> arrayItems = new ArrayList<>();
    private final Map<String, String> objectEntries = new LinkedHashMap<>();

    private JsonFixture(Path path, Mode mode) {
        this.path = path;
        this.mode = mode;
    }

    public static JsonFixture array(Path path) {
        return new JsonFixture(path, Mode.ARRAY);
    }

    public static JsonFixture object(Path path) {
        return new JsonFixture(path, Mode.OBJECT);
    }

    public JsonFixture withItem(String item) {
        arrayItems.add(item);
        return this;
    }

    public JsonFixture withItems(String... items) {
        for (String item : items) arrayItems.add(item);
        return this;
    }

    public JsonFixture withEntry(String key, String value) {
        objectEntries.put(key, value);
        return this;
    }

    public Path write() {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, mode == Mode.ARRAY ? buildArray() : buildObject());
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JsonFixture: " + path, e);
        }
    }

    private String buildArray() {
        if (arrayItems.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arrayItems.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(arrayItems.get(i)).append("\"");
        }
        return sb.append("]").toString();
    }

    private String buildObject() {
        if (objectEntries.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> e : objectEntries.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
            first = false;
        }
        return sb.append("}").toString();
    }
}