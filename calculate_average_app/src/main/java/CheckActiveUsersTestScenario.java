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

public class CheckActiveUsersTestScenario {

    private static final Path SCRIPT_DIR =
            Paths.get(System.getProperty("user.dir"), "script", "check_active_users");
    private static final Path TEST_DATA =
            SCRIPT_DIR.resolve("test_data");
    private static final Path OUTPUT =
            SCRIPT_DIR.resolve("active_users.csv");
    private static final Path MISSING =
            TEST_DATA.resolve("_not_here_.txt");

    private List<String> usersList = new ArrayList<>();
    private List<String[]> loginsList = new ArrayList<>();
    private List<String> bannedList = new ArrayList<>();

    private boolean skipUsersFile = false;

    private ScriptResult result;

    // ── given ──────────────────────────────────────────────────────────────

    public CheckActiveUsersTestScenario given() {
        System.out.println("Initializing data..");
        return this;
    }

    public CheckActiveUsersTestScenario users(String... logins) {
        usersList.addAll(Arrays.asList(logins));
        return this;
    }

    public CheckActiveUsersTestScenario logins(String[]... rows) {
        loginsList.addAll(Arrays.asList(rows));
        return this;
    }

    public CheckActiveUsersTestScenario banned(String... logins) {
        bannedList.addAll(Arrays.asList(logins));
        return this;
    }

    /** Имитирует отсутствующий USERS_FILE при запуске. */
    public CheckActiveUsersTestScenario noUsersFile() {
        skipUsersFile = true;
        return this;
    }

    // ── when ───────────────────────────────────────────────────────────────

    public CheckActiveUsersTestScenario when() {
        System.out.println("Executing actions..");
        return this;
    }

    public CheckActiveUsersTestScenario run() {
        Path usersPath = skipUsersFile ? MISSING : TEST_DATA.resolve("users.txt");

        if (!skipUsersFile) {
            new TxtFixture(usersPath).withLines(usersList.toArray(new String[0])).write();
        }

        Path loginsPath = TEST_DATA.resolve("logins.csv");
        CsvFixture logins = new CsvFixture(loginsPath).withHeader("login", "date");
        for (String[] row : loginsList) logins.withRow(row);
        logins.write();

        Path bannedPath = TEST_DATA.resolve("banned.json");
        JsonFixture.array(bannedPath).withItems(bannedList.toArray(new String[0])).write();

        result = new GenericScriptRunner()
                .script(SCRIPT_DIR.resolve("check_active_users.sh"))
                .workingDir(SCRIPT_DIR)
                .args(usersPath.toAbsolutePath().toString(),
                      loginsPath.toAbsolutePath().toString(),
                      bannedPath.toAbsolutePath().toString())
                .run();
        return this;
    }

    // ── then ───────────────────────────────────────────────────────────────

    public CheckActiveUsersTestScenario then() {
        System.out.println("Check results..");
        return this;
    }

    public CheckActiveUsersTestScenario containsUser(String login) {
        output().containsRecord(login);
        return this;
    }

    public CheckActiveUsersTestScenario doesNotContainUser(String login) {
        output().doesNotContainRecord(login);
        return this;
    }

    public CheckActiveUsersTestScenario hasNUsers(int n) {
        output().hasNRecords(n);
        return this;
    }

    public CheckActiveUsersTestScenario hasOnlyHeader() {
        output().hasOnlyHeader();
        return this;
    }

    public CheckActiveUsersTestScenario hasExitCode(int expected) {
        assertEquals(expected, result.exitCode(),
                "Expected exit code " + expected + " but was " + result.exitCode());
        return this;
    }

    public CheckActiveUsersTestScenario outputContains(String message) {
        assertTrue(result.stdout().contains(message),
                "Expected stdout to contain: \"" + message + "\" but was: \"" + result.stdout() + "\"");
        return this;
    }

    // ── helpers ────────────────────────────────────────────────────────────

    /** Фабричный метод для читаемого создания строки логина. */
    public static String[] login(String login, String date) {
        return new String[]{login, date};
    }

    private CsvOutputReader output() {
        return new CsvOutputReader(OUTPUT);
    }
}