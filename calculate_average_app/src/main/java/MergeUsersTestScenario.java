import helpers.CsvOutputReader;
import helpers.GenericScriptRunner;
import helpers.ScriptResult;
import helpers.fixture.CsvFixture;
import helpers.fixture.JsonFixture;
import helpers.fixture.TxtFixture;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MergeUsersTestScenario {

    private static final Path SCRIPT_DIR =
            Paths.get(System.getProperty("user.dir"), "script", "merge_users");
    private static final Path TEST_DATA =
            SCRIPT_DIR.resolve("test_data");
    private static final Path OUTPUT =
            SCRIPT_DIR.resolve("full_users.csv");
    private static final Path MISSING =
            TEST_DATA.resolve("_not_here_.txt");

    private List<String> usersList = new ArrayList<>();
    private JsonFixture jsonFixture = JsonFixture.object(TEST_DATA.resolve("users.json"));
    private CsvFixture csvFixture = new CsvFixture(TEST_DATA.resolve("users.csv"))
            .withHeader("login", "email");

    private boolean skipTxtFile = false;

    private ScriptResult result;

    // ── given ──────────────────────────────────────────────────────────────

    public MergeUsersTestScenario given() {
        System.out.println("Initializing data..");
        return this;
    }

    public MergeUsersTestScenario users(String... logins) {
        usersList.addAll(Arrays.asList(logins));
        return this;
    }

    public MergeUsersTestScenario name(String login, String fullName) {
        jsonFixture.withEntry(login, fullName);
        return this;
    }

    public MergeUsersTestScenario email(String login, String email) {
        csvFixture.withRow(login, email);
        return this;
    }

    /** Имитирует отсутствующий TXT_FILE при запуске. */
    public MergeUsersTestScenario noUsersFile() {
        skipTxtFile = true;
        return this;
    }

    // ── when ───────────────────────────────────────────────────────────────

    public MergeUsersTestScenario when() {
        System.out.println("Executing actions..");
        return this;
    }

    public MergeUsersTestScenario run() {
        Path txtPath = skipTxtFile ? MISSING : TEST_DATA.resolve("users.txt");
        Path jsonPath = TEST_DATA.resolve("users.json");
        Path csvPath = TEST_DATA.resolve("users.csv");

        if (!skipTxtFile) {
            new TxtFixture(txtPath).withLines(usersList.toArray(new String[0])).write();
        }
        jsonFixture.write();
        csvFixture.write();

        result = new GenericScriptRunner()
                .script(SCRIPT_DIR.resolve("merge_users.sh"))
                .workingDir(SCRIPT_DIR)
                .args(txtPath.toAbsolutePath().toString(),
                      jsonPath.toAbsolutePath().toString(),
                      csvPath.toAbsolutePath().toString())
                .run();
        return this;
    }

    // ── then ───────────────────────────────────────────────────────────────

    public MergeUsersTestScenario then() {
        System.out.println("Check results..");
        return this;
    }

    public MergeUsersTestScenario containsRecord(String login, String name, String email) {
        output().containsRecord(login, name, email);
        return this;
    }

    public MergeUsersTestScenario doesNotContainRecord(String login) {
        output().doesNotContainRecord(login);
        return this;
    }

    public MergeUsersTestScenario hasNRecords(int n) {
        output().hasNRecords(n);
        return this;
    }

    public MergeUsersTestScenario hasOnlyHeader() {
        output().hasOnlyHeader();
        return this;
    }

    public MergeUsersTestScenario hasExitCode(int expected) {
        assertEquals(expected, result.exitCode(),
                "Expected exit code " + expected + " but was " + result.exitCode());
        return this;
    }

    public MergeUsersTestScenario outputContains(String message) {
        assertTrue(result.stdout().contains(message),
                "Expected stdout to contain: \"" + message + "\" but was: \"" + result.stdout() + "\"");
        return this;
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private CsvOutputReader output() {
        return new CsvOutputReader(OUTPUT);
    }
}