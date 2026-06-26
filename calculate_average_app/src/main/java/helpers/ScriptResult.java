package helpers;

public class ScriptResult {
    private final int exitCode;
    private final String stdout;
    private final long executionTimeMs;

    public ScriptResult(int exitCode, String stdout, long executionTimeMs) {
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.executionTimeMs = executionTimeMs;
    }

    public int exitCode() { return exitCode; }
    public String stdout() { return stdout; }
    public long executionTimeMs() { return executionTimeMs; }
}