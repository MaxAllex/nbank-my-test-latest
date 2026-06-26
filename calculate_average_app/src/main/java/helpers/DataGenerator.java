package helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

public class DataGenerator {
    private static final String DIR_PATH = Paths.get(System.getProperty("user.dir"), "script", "data").toString();
    private static final String FILE_PATH = Paths.get(System.getProperty("user.dir"), "script", "data", "transactions.csv").toString();

    private static final String[] USERS = {"alice", "bob", "charlie", "tom"};
    private static final String[] CATEGORIES = {"food", "transport", "beauty"};

    private void createDir() {
        File directory = new File(DIR_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private String generateRandomDate() {
        Random random = new Random();
        int month = random.nextInt(12) + 1; // [0,11] +1 -> [1, 12]
        int day = random.nextInt(28) + 1; // [1,29]
        return String.format("2025-%02d-%02d", month, day);
    }

    private void writeData(String data, boolean append) {
        try(FileWriter writer = new FileWriter(FILE_PATH, append)) {
            writer.write(data);
            System.out.println("Data was written to file " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("Error while writing data to file " + e.getMessage());
        }
    }

    private String generateRecordString(int num) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("user,date,category,amount\n");

        Random random = new Random();

        for (int i = 0; i < num; i++) {
            String user = USERS[random.nextInt(USERS.length)];
            String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
            String date = generateRandomDate();
            double amount = 1900 * random.nextDouble() + 100;
            stringBuilder.append(String.format("%s,%s,%s,%.2f\n", user, date, category, amount));
        }
        return stringBuilder.toString();
    }

    public void generateRecords(int num) {
        createDir();
        String generatedRecords = generateRecordString(num);
        writeData(generatedRecords, false);
    }

    public void generateRecords(int num, int numUsers) {
        createDir();
        String[] users = new String[numUsers];
        for (int i = 0; i < numUsers; i++) {
            users[i] = "user" + i;
        }
        writeDataBuffered(num, users);
    }

    private void writeDataBuffered(int num, String[] users) {
        Random random = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("user,date,category,amount\n");
            for (int i = 0; i < num; i++) {
                writer.write(String.format("%s,%s,%s,%.2f\n",
                        users[random.nextInt(users.length)],
                        generateRandomDate(),
                        CATEGORIES[random.nextInt(CATEGORIES.length)],
                        1900 * random.nextDouble() + 100));
            }
        } catch (IOException e) {
            System.out.println("Error while writing data to file: " + e.getMessage());
        }
    }

    public void withRecord(String user, String date, String category, double amount) {
        String record = String.format("%s,%s,%s,%.2f\n", user, date, category, amount);

        writeData(record, true);
    }
}
