import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CalculateAverageNonFunctionalTest {

    // TC-NFP-01
    @Tag("performance")
    @Test
    void scriptCompletesWithin1SecondFor10kRecords() {
        new TestScenario()
                .given().generateRecords(10_000, 10)
                .when().executeScript()
                .then().executionTimeLessThan(1_000);
    }

    // TC-NFP-02
    @Tag("performance")
    @Test
    void scriptCompletesWithin5SecondsFor100kRecords() {
        new TestScenario()
                .given().generateRecords(100_000, 100)
                .when().executeScript()
                .then().executionTimeLessThan(5_000);
    }

    // TC-NFP-03
    @Tag("performance")
    @Test
    void scriptCompletesWithin20SecondsFor1MillionRecords() {
        new TestScenario()
                .given().generateRecords(1_000_000, 1_000)
                .when().executeScript()
                .then().executionTimeLessThan(20_000);
    }

    // TC-NFR-03: количество строк в выходном файле = количество уникальных пользователей
    @Test
    void outputRecordCountMatchesUniqueUserCount() {
        new TestScenario()
                .given()
                .generateRecords(0)
                .withRecord("alice", "2025-01-01", "food", 100.0)
                .withRecord("alice", "2025-02-01", "transport", 200.0)
                .withRecord("bob", "2025-01-05", "food", 300.0)
                .when().executeScript()
                .then().outputHasNRecords(2);
    }

    // TC-NFR-04: повторный запуск перезаписывает, а не дублирует записи
    @Test
    void repeatedRunDoesNotDuplicateOutputRecords() {
        new TestScenario()
                .given()
                .generateRecords(0)
                .withRecord("alice", "2025-01-01", "food", 500.0)
                .withRecord("bob", "2025-02-01", "transport", 300.0)
                .when().executeScript()
                .executeScript()
                .then().doesNotContainDuplicateUsers();
    }

    // TC-NFR-05: входной файл содержит только заголовок — выходной тоже
    @Test
    void outputContainsOnlyHeaderWhenInputHasNoDataRows() {
        new TestScenario()
                .given().generateRecords(0)
                .when().executeScript()
                .then().outputHasOnlyHeader();
    }

    // TC-NFU-02: скрипт выводит сообщение об успехе
    @Test
    void scriptPrintsSuccessMessageOnCompletion() {
        new TestScenario()
                .given()
                .generateRecords(0)
                .withRecord("alice", "2025-01-01", "food", 100.0)
                .when().executeScript()
                .then().containsOutput("✅ Готово. Результат в:");
    }

    // TC-NFU-03: exit code 0 при успешном завершении
    @Test
    void scriptReturnsZeroExitCodeOnSuccess() {
        new TestScenario()
                .given()
                .generateRecords(0)
                .withRecord("alice", "2025-01-01", "food", 100.0)
                .when().executeScript()
                .then().hasExitCode(0);
    }

    // TC-NFU-03: ненулевой exit code при отсутствии входного файла
    @Test
    void scriptReturnsNonZeroExitCodeWhenFileIsMissing() {
        new TestScenario()
                .when().executeScript("nonexistent.csv")
                .then().hasExitCode(1);
    }
}