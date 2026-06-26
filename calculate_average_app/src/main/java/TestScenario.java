import helpers.DataGenerator;
import helpers.ResultAnalyser;
import helpers.ScriptRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestScenario {
    private DataGenerator dataGenerator;
    private ScriptRunner scriptRunner;
    private ResultAnalyser resultAnalyser;

    public TestScenario() {
        this.dataGenerator = new DataGenerator();
        this.scriptRunner = new ScriptRunner();
        this.resultAnalyser = new ResultAnalyser();
    }

    public TestScenario given() {
        System.out.println("Initializing data..");
        return this;
    }

    public TestScenario generateRecords(int num) {
        dataGenerator.generateRecords(num);
        return this;
    }

    public TestScenario generateRecords(int num, int numUsers) {
        dataGenerator.generateRecords(num, numUsers);
        return this;
    }

    public TestScenario withRecord(String user, String date, String category, double amount) {
        dataGenerator.withRecord( user,  date,  category,  amount);
        return this;
    }

    public TestScenario when() {
        System.out.println("Executing actions..");
        return this;
    }

    public TestScenario executeScript() {
        resultAnalyser.reset();
        scriptRunner.executeScript();
        return this;
    }

    public TestScenario executeScript(String filePath) {
        resultAnalyser.reset();
        scriptRunner.executeScript(filePath);
        return this;
    }

    public TestScenario then() {
        System.out.println("Check results..");
        return this;
    }

    public TestScenario containsRecord(String user, String month, double averageAmount) {
        resultAnalyser.containsRecord(user, month, averageAmount);
        return this;
    }

    public TestScenario containsError(String error) {
        assertEquals(error, scriptRunner.getErrorMessage().replace("\n", ""));
        return this;
    }

    public TestScenario containsOutput(String message) {
        assertTrue(scriptRunner.getErrorMessage().contains(message),
                "Expected stdout to contain: \"" + message + "\" but was: \"" + scriptRunner.getErrorMessage() + "\"");
        return this;
    }

    public TestScenario hasExitCode(int expected) {
        assertEquals(expected, scriptRunner.getExitCode(),
                "Expected exit code " + expected + " but was " + scriptRunner.getExitCode());
        return this;
    }

    public TestScenario executionTimeLessThan(long ms) {
        long actual = scriptRunner.getExecutionTimeMs();
        assertTrue(actual < ms,
                "Expected execution time < " + ms + "ms but was " + actual + "ms");
        return this;
    }

    public TestScenario outputHasOnlyHeader() {
        resultAnalyser.hasOnlyHeader();
        return this;
    }

    public TestScenario outputHasNRecords(int n) {
        resultAnalyser.hasNRecords(n);
        return this;
    }

    public TestScenario doesNotContainDuplicateUsers() {
        resultAnalyser.doesNotContainDuplicateUsers();
        return this;
    }
}
