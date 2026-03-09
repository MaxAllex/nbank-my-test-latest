package api.generators;

import com.github.javafaker.Faker;

import java.util.Locale;

public class NameGenerator {
    private static final Faker LATIN_FAKER = new Faker(Locale.ENGLISH);
    private static final Faker CYRILLIC_FAKER = new Faker(Locale.forLanguageTag("ru"));


    public static String generate(UserNameType type) {
        return switch (type) {
//            case VALID_CYRILLIC -> generateCyrillicFullName();
            case VALID_LATIN ->  LATIN_FAKER.name().fullName().replaceAll("^(\\S+\\s\\S+).*", "$1").replaceAll("\\p{P}", "");
            case SEMANTICALLY_INVALID -> generateGarbage();
        };
    }

    private static String generateCyrillicFullName() {
        String first = sanitizeCyrillic(CYRILLIC_FAKER.name().firstName());
        String last = sanitizeCyrillic(CYRILLIC_FAKER.name().lastName());

        for (int i = 0; i < 3 && (first.isBlank() || last.isBlank()); i++) {
            if (first.isBlank()) {
                first = sanitizeCyrillic(CYRILLIC_FAKER.name().firstName());
            }
            if (last.isBlank()) {
                last = sanitizeCyrillic(CYRILLIC_FAKER.name().lastName());
            }
        }

        if (first.isBlank()) {
            first = sanitizeCyrillic(CYRILLIC_FAKER.name().firstName());
        }
        if (last.isBlank()) {
            last = sanitizeCyrillic(CYRILLIC_FAKER.name().lastName());
        }

        return (first.isBlank() ? CYRILLIC_FAKER.name().firstName() : first)
                + " "
                + (last.isBlank() ? CYRILLIC_FAKER.name().lastName() : last);
    }

    private static String sanitizeCyrillic(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value
                .replace('\u0451', '\u0435')
                .replace('\u0401', '\u0415');
        return normalized.replaceAll("[^\\u0410-\\u042F\\u0430-\\u044F]", "");
    }

    private static String generateGarbage() {
        return LATIN_FAKER.options().option(
                "Asdfgh Asdfgh",
                "Qwerty Qwerty",
                "Test User",
                "Admin Admin",
                LATIN_FAKER.regexify("[A-Za-z]{10}") + " " + LATIN_FAKER.regexify("[A-Za-z]{10}")
        );
    }
}
