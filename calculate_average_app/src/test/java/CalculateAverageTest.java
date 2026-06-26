import org.junit.jupiter.api.Test;

public class CalculateAverageTest {
    @Test
    public void calculatingAverageWithTwoUsersAndOneRecordWorksCorrectly() {
        new TestScenario()
                .given()
                .generateRecords(0)
                .withRecord("user1", "2025-01-01", "transport", 800.0)
                .withRecord("user2", "2025-01-02", "food", 100.0)
                .when().executeScript() // в 1 метод
                .then()
                .containsRecord("user1", "ALL",  800.0)
                .containsRecord("user2", "ALL",  100.0);
    }

    @Test
    public void calculatingAverageWithInvalidDataLeadsToInvalidDataError() {
        new TestScenario()
                .given()
                .generateRecords(0)
                .withRecord("user1", "invalid-date", "transport", 800.0)
                .when().executeScript() // в 1 метод
                .then()
                .containsError("❌ Обнаружена неверная дата. Завершаем.");
    }

    @Test
    public void calculatingAverageWithNonExistingInputFileLeadsToFileNotFoundError() {
        String nonExistingFile = "file.txt";
        new TestScenario()
                .given()
                .generateRecords(1)
                .when().executeScript(nonExistingFile)
                .containsError("Файл не найден: " + nonExistingFile);
    }
}
