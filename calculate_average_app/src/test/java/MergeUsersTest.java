import org.junit.jupiter.api.Test;

public class MergeUsersTest {

    // TC-MU-19: пользователь с полными данными объединяется корректно
    @Test
    void userWithAllDataMergedCorrectly() {
        new MergeUsersTestScenario()
                .given()
                    .users("alice")
                    .name("alice", "Alice Smith")
                    .email("alice", "alice@example.com")
                .when().run()
                .then()
                    .containsRecord("alice", "Alice Smith", "alice@example.com");
    }

    // TC-MU-07: несколько пользователей — все объединяются
    @Test
    void multipleUsersAllMergedCorrectly() {
        new MergeUsersTestScenario()
                .given()
                    .users("alice", "bob", "carol")
                    .name("alice", "Alice Smith").email("alice", "alice@example.com")
                    .name("bob", "Bob Johnson").email("bob", "bob@example.com")
                    .name("carol", "Carol Lee").email("carol", "carol@example.com")
                .when().run()
                .then()
                    .hasNRecords(3)
                    .containsRecord("alice", "Alice Smith", "alice@example.com")
                    .containsRecord("bob", "Bob Johnson", "bob@example.com")
                    .containsRecord("carol", "Carol Lee", "carol@example.com");
    }

    // TC-MU-11: нет имени в JSON — пользователь пропускается с предупреждением
    @Test
    void userMissingInJsonIsSkippedWithWarning() {
        new MergeUsersTestScenario()
                .given()
                    .users("alice", "dave")
                    .name("alice", "Alice Smith").email("alice", "alice@example.com")
                    .email("dave", "dave@example.com")
                .when().run()
                .then()
                    .containsRecord("alice", "Alice Smith", "alice@example.com")
                    .doesNotContainRecord("dave")
                    .outputContains("⚠️")
                    .outputContains("dave");
    }

    // TC-MU-16: нет email в CSV — пользователь пропускается с предупреждением
    @Test
    void userMissingInCsvIsSkippedWithWarning() {
        new MergeUsersTestScenario()
                .given()
                    .users("alice", "dave")
                    .name("alice", "Alice Smith").email("alice", "alice@example.com")
                    .name("dave", "Dave Brown")
                .when().run()
                .then()
                    .containsRecord("alice", "Alice Smith", "alice@example.com")
                    .doesNotContainRecord("dave")
                    .outputContains("⚠️")
                    .outputContains("dave");
    }

    // TC-MU-21: нет ни имени, ни email — пользователь пропускается
    @Test
    void userMissingBothNameAndEmailIsSkippedWithWarning() {
        new MergeUsersTestScenario()
                .given()
                    .users("dave")
                .when().run()
                .then()
                    .hasOnlyHeader()
                    .outputContains("⚠️")
                    .outputContains("dave");
    }

    // TC-MU-08: пустой список пользователей — только заголовок
    @Test
    void emptyUserListProducesHeaderOnlyOutput() {
        new MergeUsersTestScenario()
                .given()
                    .users()
                    .name("alice", "Alice Smith").email("alice", "alice@example.com")
                .when().run()
                .then()
                    .hasExitCode(0)
                    .hasOnlyHeader();
    }

    // TC-MU-20: пользователь есть в JSON/CSV, но отсутствует в TXT — не попадает в результат
    @Test
    void userNotInTxtIsNotMergedEvenIfDataExists() {
        new MergeUsersTestScenario()
                .given()
                    .users("alice")
                    .name("alice", "Alice Smith").email("alice", "alice@example.com")
                    .name("dave", "Dave Brown").email("dave", "dave@example.com")
                .when().run()
                .then()
                    .hasNRecords(1)
                    .doesNotContainRecord("dave");
    }

    // TC-MU-03: отсутствие TXT_FILE — код выхода 1
    @Test
    void missingUsersFileReturnsExitCode1() {
        new MergeUsersTestScenario()
                .given()
                    .noUsersFile()
                    .name("alice", "Alice Smith").email("alice", "alice@example.com")
                .when().run()
                .then()
                    .hasExitCode(1)
                    .outputContains("Один из входных файлов не найден");
    }

    // TC-MU-27: сообщение об успешном завершении
    @Test
    void scriptPrintsSuccessMessageOnCompletion() {
        new MergeUsersTestScenario()
                .given()
                    .users("alice")
                    .name("alice", "Alice Smith")
                    .email("alice", "alice@example.com")
                .when().run()
                .then()
                    .outputContains("✅ Готово: full_users.csv");
    }
}