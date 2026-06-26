package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GenericScriptRunner {
    private Path scriptPath;
    private Path workingDir;
    private final List<String> args = new ArrayList<>();

    public GenericScriptRunner script(Path scriptPath) {
        this.scriptPath = scriptPath;
        return this;
    }

    public GenericScriptRunner workingDir(Path workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public GenericScriptRunner args(String... values) {
        for (String v : values) args.add(v);
        return this;
    }

    public ScriptResult run() {
        List<String> command = new ArrayList<>();
        command.add("bash");
        command.add(scriptPath.toAbsolutePath().toString());
        command.addAll(args);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        if (workingDir != null) pb.directory(workingDir.toFile());

        try {
            long start = System.currentTimeMillis();
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            return new ScriptResult(exitCode, output.toString(), System.currentTimeMillis() - start);
        } catch (IOException | InterruptedException e) {
            return new ScriptResult(-1, e.getMessage(), 0);
        }
    }
}