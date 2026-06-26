import org.junit.jupiter.api.Test;

public class CheckActiveUsersTest {

    private static String[] login(String login, String date) {
        return new String[]{login, date};
    }

    // TC-CAU-01: активный пользователь попадает в выходной файл
    @Test
    void activeUserWithRecentLoginAppearsInOutput() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users("alice")
                    .logins(login("alice", "2026-06-25"))
                    .banned()
                .when().run()
                .then()
                    .hasExitCode(0)
                    .containsUser("alice");
    }

    // TC-CAU-13 + TC-CAU-20: забаненный пользователь с активной датой исключается
    @Test
    void bannedUserIsExcludedEvenWithRecentLogin() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users("alice", "bob")
                    .logins(login("alice", "2026-06-25"), login("bob", "2026-06-20"))
                    .banned("bob")
                .when().run()
                .then()
                    .containsUser("alice")
                    .doesNotContainUser("bob");
    }

    // TC-CAU-17: вход ровно 30 дней назад — граница включительно
    @Test
    void userWithLoginExactly30DaysAgoIsConsideredActive() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users("alice")
                    .logins(login("alice", "2026-05-27"))
                    .banned()
                .when().run()
                .then()
                    .containsUser("alice");
    }

    // TC-CAU-18: вход 31 день назад — уже не активен
    @Test
    void userWithLoginMoreThan30DaysAgoIsExcluded() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users("alice")
                    .logins(login("alice", "2026-05-26"))
                    .banned()
                .when().run()
                .then()
                    .doesNotContainUser("alice");
    }

    // TC-CAU-10: пользователь без записи в logins исключается
    @Test
    void userWithNoLoginRecordIsExcluded() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users("dave")
                    .logins()
                    .banned()
                .when().run()
                .then()
                    .hasOnlyHeader();
    }

    // TC-CAU-08: пустой список пользователей — только заголовок
    @Test
    void emptyUserListProducesHeaderOnlyOutput() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users()
                    .logins(login("alice", "2026-06-25"))
                    .banned()
                .when().run()
                .then()
                    .hasExitCode(0)
                    .hasOnlyHeader();
    }

    // TC-CAU-22: все пользователи забанены — только заголовок
    @Test
    void allBannedUsersProducesHeaderOnlyOutput() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users("alice", "bob")
                    .logins(login("alice", "2026-06-25"), login("bob", "2026-06-20"))
                    .banned("alice", "bob")
                .when().run()
                .then()
                    .hasOnlyHeader();
    }

    // TC-CAU-14: пустой banned — никого не исключает
    @Test
    void emptyBannedListIncludesAllActiveUsers() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users("alice", "charlie")
                    .logins(login("alice", "2026-06-25"), login("charlie", "2026-06-20"))
                    .banned()
                .when().run()
                .then()
                    .hasNUsers(2);
    }

    // TC-CAU-03: отсутствие USERS_FILE — код выхода 1
    @Test
    void missingUsersFileReturnsExitCode1() {
        new CheckActiveUsersTestScenario()
                .given()
                    .noUsersFile()
                    .logins(login("alice", "2026-06-25"))
                    .banned()
                .when().run()
                .then()
                    .hasExitCode(1)
                    .outputContains("❌ Один из входных файлов не найден.");
    }

    // TC-CAU-26: сообщение об успешном завершении
    @Test
    void scriptPrintsSuccessMessageOnCompletion() {
        new CheckActiveUsersTestScenario()
                .given()
                    .users("alice")
                    .logins(login("alice", "2026-06-25"))
                    .banned()
                .when().run()
                .then()
                    .outputContains("✅ Готово: active_users.csv");
    }
}