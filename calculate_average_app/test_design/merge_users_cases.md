# Тест-кейсы: merge_users.sh

Скрипт принимает 3 аргумента: `TXT_FILE`, `JSON_FILE`, `CSV_FILE`.  
Объединяет логин + имя + email и записывает `full_users.csv`.  
Пользователь пропускается с предупреждением `⚠️`, если нет имени (JSON) или email (CSV).

---

## Входные данные (примеры)

**users.txt**
```
alice
bob
carol
```

**users.json**
```json
{
  "alice": "Alice Smith",
  "bob": "Bob Johnson",
  "carol": "Carol Lee"
}
```

**users.csv**
```csv
login,email
alice,alice@example.com
bob,bob@example.com
carol,carol@example.com
```

---

## 1. Проверка аргументов и наличия файлов

### TC-MU-01: Все три файла переданы и существуют

**Предусловие:** `users.txt`, `users.json`, `users.csv` существуют и корректны.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** Скрипт завершается с кодом `0`, создан файл `full_users.csv`.

---

### TC-MU-02: Запуск без аргументов

**Предусловие:** —  
**Шаги:** `./merge_users.sh`  
**Ожидаемый результат:** Сообщение об ошибке, код выхода `1`, `full_users.csv` не создан.

---

### TC-MU-03: TXT_FILE не существует

**Предусловие:** `users.json` и `users.csv` есть, `users.txt` отсутствует.  
**Шаги:** `./merge_users.sh no_users.txt users.json users.csv`  
**Ожидаемый результат:** Сообщение `Один из входных файлов не найден`, код выхода `1`.

---

### TC-MU-04: JSON_FILE не существует

**Предусловие:** `users.txt` и `users.csv` есть, `users.json` отсутствует.  
**Шаги:** `./merge_users.sh users.txt no_users.json users.csv`  
**Ожидаемый результат:** Сообщение `Один из входных файлов не найден`, код выхода `1`.

---

### TC-MU-05: CSV_FILE не существует

**Предусловие:** `users.txt` и `users.json` есть, `users.csv` отсутствует.  
**Шаги:** `./merge_users.sh users.txt users.json no_users.csv`  
**Ожидаемый результат:** Сообщение `Один из входных файлов не найден`, код выхода `1`.

---

## 2. Проверка формата TXT_FILE

### TC-MU-06: Один пользователь

**Предусловие:** `users.txt` содержит только `alice`. Имя и email для `alice` есть.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `full_users.csv` содержит заголовок и одну строку `alice,Alice Smith,alice@example.com`.

---

### TC-MU-07: Несколько пользователей

**Предусловие:** `users.txt` содержит `alice`, `bob`, `carol`. Все данные есть.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `full_users.csv` содержит 3 строки с данными (порядок — как в `users.txt`).

---

### TC-MU-08: Пустой TXT_FILE

**Предусловие:** `users.txt` пустой.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `full_users.csv` содержит только заголовок `login,name,email`, код выхода `0`.

---

### TC-MU-09: Дубликат логина в TXT_FILE

**Предусловие:** `users.txt` содержит `alice` дважды.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `alice` попадает в `full_users.csv` дважды (скрипт не дедуплицирует).

---

## 3. Проверка формата JSON_FILE

### TC-MU-10: Корректный JSON-словарь

**Предусловие:** `users.json` содержит корректный JSON с именами для всех пользователей.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** Имена корректно подставлены в `full_users.csv`.

---

### TC-MU-11: Логин есть в TXT, но отсутствует в JSON

**Предусловие:** `users.txt` содержит `dave`, в `users.json` ключа `dave` нет.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** В stdout — `⚠️  Пропущен пользователь dave: нет имени или email`. `dave` не попадает в `full_users.csv`.

---

### TC-MU-12: JSON со значением `null` для логина

**Предусловие:** `users.json` содержит `"alice": null`.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `jq` вернёт строку `null`; `alice` пропускается с предупреждением (имя воспринимается как непустое — поведение зависит от реализации; зафиксировать фактическое поведение).

---

### TC-MU-13: Невалидный JSON в JSON_FILE

**Предусловие:** `users.json` содержит невалидный JSON, например `{not: valid}`.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `jq` завершается с ошибкой; все пользователи пропускаются или скрипт падает. Код выхода `≠ 0`.

---

### TC-MU-14: Пустой JSON-объект

**Предусловие:** `users.json` = `{}`.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** Для каждого пользователя из `users.txt` выводится предупреждение, `full_users.csv` содержит только заголовок.

---

## 4. Проверка формата CSV_FILE

### TC-MU-15: Корректный CSV с заголовком

**Предусловие:** `users.csv` имеет строку `login,email` и данные для всех пользователей.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** Заголовок пропускается (`tail -n +2`), email подставлен корректно.

---

### TC-MU-16: Логин есть в TXT, но отсутствует в CSV

**Предусловие:** `users.txt` содержит `dave`, в `users.csv` строки для `dave` нет.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** В stdout — `⚠️  Пропущен пользователь dave: нет имени или email`. `dave` не попадает в `full_users.csv`.

---

### TC-MU-17: CSV содержит только заголовок

**Предусловие:** `users.csv` содержит только строку `login,email`.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** Для каждого пользователя из `users.txt` выводится предупреждение, `full_users.csv` содержит только заголовок.

---

### TC-MU-18: CSV с лишними пробелами в email

**Предусловие:** `users.csv` содержит `alice, alice@example.com` (пробел после запятой).  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** Email попадает в `full_users.csv` с пробелом (`IFS=','` не trim). Зафиксировать фактическое поведение.

---

## 5. Проверка логики объединения

### TC-MU-19: Пользователь есть в TXT, JSON и CSV — полное совпадение

**Предусловие:** `alice` присутствует во всех трёх файлах с корректными данными.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** Строка `alice,Alice Smith,alice@example.com` в `full_users.csv`.

---

### TC-MU-20: Пользователь есть в JSON и CSV, но отсутствует в TXT

**Предусловие:** `dave` есть в `users.json` и `users.csv`, но не в `users.txt`.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `dave` не попадает в `full_users.csv` — скрипт итерирует только по TXT.

---

### TC-MU-21: Нет имени И нет email одновременно

**Предусловие:** `dave` есть в `users.txt`, но отсутствует и в `users.json`, и в `users.csv`.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** Одно предупреждение `⚠️  Пропущен пользователь dave: нет имени или email`. `dave` не в `full_users.csv`.

---

### TC-MU-22: Нет имени, но email есть

**Предусловие:** `dave` есть в `users.csv` (email есть), но отсутствует в `users.json`.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `dave` пропускается с предупреждением (условие `[[ -n "$name" && -n "$email" ]]` не выполнено).

---

### TC-MU-23: Имя есть, но нет email

**Предусловие:** `dave` есть в `users.json` (имя есть), но отсутствует в `users.csv`.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** `dave` пропускается с предупреждением.

---

## 6. Проверка выходного файла full_users.csv

### TC-MU-24: Заголовок выходного файла

**Предусловие:** Корректные входные данные.  
**Шаги:** Проверить первую строку `full_users.csv`.  
**Ожидаемый результат:** Первая строка — `login,name,email`.

---

### TC-MU-25: Порядок строк соответствует порядку в TXT_FILE

**Предусловие:** `users.txt` содержит `carol`, `alice`, `bob` в таком порядке.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** В `full_users.csv` строки идут в порядке `carol`, `alice`, `bob`.

---

### TC-MU-26: Повторный запуск перезаписывает файл

**Предусловие:** `full_users.csv` уже существует с данными прошлого запуска.  
**Шаги:** Запустить скрипт повторно.  
**Ожидаемый результат:** `full_users.csv` перезаписан без дублирования строк из предыдущего запуска.

---

### TC-MU-27: Сообщение об успешном завершении

**Предусловие:** Корректные входные данные.  
**Шаги:** `./merge_users.sh users.txt users.json users.csv`  
**Ожидаемый результат:** В stdout выводится `✅ Готово: full_users.csv`, код выхода `0`.