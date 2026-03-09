package api.generators;

import com.github.javafaker.Faker;
import api.dto.Role;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

public class DTOGenerator {

    private static final Faker faker = new Faker();

    /**
     * Генерирует экземпляр указанного класса с случайными тестовыми данными.
     * Работает на основе типов полей и их имен (для более точной генерации).
     * Для массивов генерируются пустые массивы, чтобы избежать циклов.
     *
     * @param clazz Класс для генерации (record или обычный класс).
     * @param <T>   Тип объекта.
     * @return Сгенерированный экземпляр.
     */
    public static <T> T generate(Class<T> clazz) {
        return generate(clazz, Map.of());
    }

    /**
     * Генерирует экземпляр указанного класса с возможностью переопределения полей.
     *
     * @param clazz     Класс для генерации.
     * @param overrides Карта с переопределениями полей (ключ - имя поля, значение - значение).
     * @param <T>       Тип объекта.
     * @return Сгенерированный экземпляр с переопределениями.
     */
    public static <T> T generate(Class<T> clazz, Map<String, Object> overrides) {
        try {
            if (clazz.isRecord()) {
                Constructor<?>[] constructors = clazz.getConstructors();
                if (constructors.length != 1) {
                    throw new IllegalArgumentException("Record должен иметь ровно один конструктор: " + clazz.getName());
                }
                Constructor<?> constructor = constructors[0];
                Parameter[] params = constructor.getParameters();
                Object[] args = new Object[params.length];

                for (int i = 0; i < params.length; i++) {
                    Parameter param = params[i];
                    String name = param.getName();
                    Class<?> type = param.getType();

                    if (overrides.containsKey(name)) {
                        args[i] = overrides.get(name);
                    } else {
                        args[i] = generateValue(type, name);
                    }
                }

                @SuppressWarnings("unchecked")
                T instance = (T) constructor.newInstance(args);
                return instance;
            }

            Constructor<T> noArgsConstructor = getNoArgsConstructor(clazz);
            if (noArgsConstructor != null) {
                noArgsConstructor.setAccessible(true);
                T instance = noArgsConstructor.newInstance();
                populateFields(instance, overrides);
                return instance;
            }

            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors.length == 0) {
                throw new IllegalArgumentException("Класс должен иметь публичный конструктор: " + clazz.getName());
            }
            Constructor<?> constructor = pickBestConstructor(clazz, constructors);
            Object[] args = buildArgsFromFields(constructor, clazz, overrides);
            @SuppressWarnings("unchecked")
            T instance = (T) constructor.newInstance(args);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сгенерировать экземпляр для " + clazz.getName(), e);
        }
    }

    //Хардкод для стабильности.
    private static Object generateValue(Class<?> type, String name) {
        if (type == long.class || type == Long.class) {
            return faker.number().randomNumber(5, false);
        } else if (type == int.class || type == Integer.class) {
            if (name.toLowerCase().contains("balance") || name.toLowerCase().contains("amount")) {
                return faker.number().numberBetween(0, 10000);
            }
            return faker.number().numberBetween(1, 100);
        } else if (type == String.class) {
            if (name.toLowerCase().contains("username")) {
                String base = faker.name().firstName()
                        .replaceAll("[^A-Za-z0-9]", "")
                        .toLowerCase(Locale.ENGLISH);
                if (base.isBlank()) {
                    base = "user";
                }
                int maxBaseLength = 6;
                if (base.length() > maxBaseLength) {
                    base = base.substring(0, maxBaseLength);
                }
                String suffix = faker.number().digits(4);
                return base + suffix;
            } else if (name.toLowerCase().contains("password")) {
                return faker.internet().password(4, 12, true, false, true) + "aA1$";
            } else if (name.toLowerCase().contains("name")) {
                return faker.name().fullName() + "А";
            } else if (name.toLowerCase().contains("role")) {
                return faker.options().option(Role.USER.name(), Role.ADMIN.name());
            } else if (name.toLowerCase().contains("accountnumber") || name.toLowerCase().contains("accounts")) {
                return "ACC" + faker.number().randomNumber(4, false);
            } else if (name.toLowerCase().contains("type")) {
                return faker.options().option("DEPOSIT", "WITHDRAWAL", "TRANSFER");
            } else {
                return faker.lorem().sentence(3);
            }
        } else if (type == ZonedDateTime.class) {
            return ZonedDateTime.now().minusDays(faker.number().numberBetween(0, 365));
        } else if (type == java.time.LocalDateTime.class) {
            return java.time.LocalDateTime.now().minusDays(faker.number().numberBetween(0, 365));
        } else if (java.util.List.class.isAssignableFrom(type)) {
            return java.util.List.of();
        } else if (type.isArray()) {
            // Генерируем пустой массив, чтобы избежать циклов и зависимостей
            return Array.newInstance(type.getComponentType(), 0);
        } else if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants != null && constants.length > 0 ? constants[0] : null;
        } else {
            // Для неизвестных типов возвращаем null (можно расширить для других DTO)
            return null;
        }
    }

    private static <T> Constructor<T> getNoArgsConstructor(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static <T> void populateFields(T instance, Map<String, Object> overrides) throws IllegalAccessException {
        Class<?> current = instance.getClass();
        while (current != null && current != Object.class) {
            var fields = current.getDeclaredFields();
            for (var field : fields) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                String name = field.getName();
                Object value = overrides.containsKey(name)
                        ? overrides.get(name)
                        : generateValue(field.getType(), name);
                if (value == null && field.getType().isPrimitive()) {
                    value = defaultPrimitiveValue(field.getType());
                }
                field.set(instance, value);
            }
            current = current.getSuperclass();
        }
    }

    private static Object[] buildArgsFromFields(Constructor<?> constructor, Class<?> clazz, Map<String, Object> overrides) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        var fields = clazz.getDeclaredFields();
        for (int i = 0; i < paramTypes.length; i++) {
            String name = i < fields.length ? fields[i].getName() : "";
            Class<?> type = paramTypes[i];
            Object value = overrides.containsKey(name) ? overrides.get(name) : generateValue(type, name);
            if (value == null && type.isPrimitive()) {
                value = defaultPrimitiveValue(type);
            }
            args[i] = value;
        }
        return args;
    }

    private static Constructor<?> pickBestConstructor(Class<?> clazz, Constructor<?>[] constructors) {
        int fieldCount = clazz.getDeclaredFields().length;
        for (Constructor<?> ctor : constructors) {
            if (ctor.getParameterCount() == fieldCount) {
                return ctor;
            }
        }
        return constructors[0];
    }

    private static Object defaultPrimitiveValue(Class<?> type) {
        if (type == boolean.class) {
            return false;
        } else if (type == byte.class) {
            return (byte) 0;
        } else if (type == short.class) {
            return (short) 0;
        } else if (type == int.class) {
            return 0;
        } else if (type == long.class) {
            return 0L;
        } else if (type == float.class) {
            return 0.0f;
        } else if (type == double.class) {
            return 0.0d;
        } else if (type == char.class) {
            return '\0';
        }
        return null;
    }
}
