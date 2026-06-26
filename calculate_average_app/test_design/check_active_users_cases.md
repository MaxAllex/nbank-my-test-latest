# Тест-кейсы: check_active_users.sh

Скрипт принимает 3 аргумента: `USERS_FILE`, `LOGINS_FILE`, `BANNED_FILE`.  
Выводит `active_users.csv` — список пользователей, чей последний вход был не позднее 30 дней назад и которые не забанены.

---

## Входные данные (примеры)

**users.txt**
```
alice
bob
charlie
```

**logins.csv**
```csv
login,date
alice,2026-06-10
bob,2026-05-01
charlie,2026-06-25
```

**banned.json**
```json
["bob"]
```

---

## 1. Проверка аргументов и наличия файлов

### TC-CAU-01: Все три файла переданы и существуют

**Предусловие:** `users.txt`, `logins.csv`, `banned.json` существуют и корректны.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** Скрипт завершается с кодом `0`, создан файл `active_users.csv`.

---

### TC-CAU-02: USERS_FILE не передан (0 аргументов)

**Предусловие:** —  
**Шаги:** `./check_active_users.sh`  
**Ожидаемый результат:** Сообщение об ошибке (`❌`), код выхода `≠ 0`, файл `active_users.csv` не создан.

---

### TC-CAU-03: USERS_FILE не существует

**Предусловие:** Файл `no_users.txt` отсутствует.  
**Шаги:** `./check_active_users.sh no_users.txt logins.csv banned.json`  
**Ожидаемый результат:** Сообщение `❌ Один из входных файлов не найден.`, код выхода `1`.

---

### TC-CAU-04: LOGINS_FILE не существует

**Предусловие:** `users.txt` и `banned.json` есть, `logins.csv` отсутствует.  
**Шаги:** `./check_active_users.sh users.txt no_logins.csv banned.json`  
**Ожидаемый результат:** Сообщение `❌ Один из входных файлов не найден.`, код выхода `1`.

---

### TC-CAU-05: BANNED_FILE не существует

**Предусловие:** `users.txt` и `logins.csv` есть, `banned.json` отсутствует.  
**Шаги:** `./check_active_users.sh users.txt logins.csv no_banned.json`  
**Ожидаемый результат:** Сообщение `❌ Один из входных файлов не найден.`, код выхода `1`.

---

## 2. Проверка формата USERS_FILE

### TC-CAU-06: Файл содержит одного пользователя

**Предусловие:** `users.txt` — одна строка `alice`. `alice` есть в `logins.csv` с датой входа в пределах 30 дней.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `active_users.csv` содержит строку с `alice`.

---

### TC-CAU-07: Файл содержит несколько пользователей

**Предусловие:** `users.txt` содержит `alice`, `charlie`; оба активны и не забанены.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** В `active_users.csv` — строки для `alice` и `charlie`.

---

### TC-CAU-08: Файл пустой (нет пользователей)

**Предусловие:** `users.txt` пустой.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `active_users.csv` содержит только заголовок `login,last_login`, скрипт завершается с кодом `0`.

---

## 3. Проверка формата LOGINS_FILE

### TC-CAU-09: Корректный CSV с заголовком

**Предусловие:** `logins.csv` имеет строку заголовка `login,date` и записи.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** Заголовок пропускается, данные обрабатываются корректно.

---

### TC-CAU-10: Пользователь отсутствует в LOGINS_FILE

**Предусловие:** `users.txt` содержит `dave`, в `logins.csv` нет записи для `dave`.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `dave` не попадает в `active_users.csv`.

---

### TC-CAU-11: Некорректный формат даты в LOGINS_FILE

**Предусловие:** В `logins.csv` для `alice` дата `not-a-date`.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `alice` пропускается, не попадает в `active_users.csv`. Скрипт не падает.

---

### TC-CAU-12: LOGINS_FILE содержит только заголовок

**Предусловие:** `logins.csv` содержит только `login,date`.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `active_users.csv` содержит только заголовок, скрипт завершается с кодом `0`.

---

## 4. Проверка формата BANNED_FILE

### TC-CAU-13: Корректный JSON-массив с одним забаненным

**Предусловие:** `banned.json` = `["bob"]`. `bob` есть в `users.txt` и `logins.csv` с активной датой.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `bob` не попадает в `active_users.csv`.

---

### TC-CAU-14: Пустой JSON-массив (никто не забанен)

**Предусловие:** `banned.json` = `[]`. Все пользователи активны.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** Все активные пользователи попадают в `active_users.csv`.

---

### TC-CAU-15: Некорректный JSON в BANNED_FILE

**Предусловие:** `banned.json` содержит невалидный JSON, например `{not: valid}`.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** Скрипт завершается с ошибкой (jq падает), код выхода `≠ 0`.

---

## 5. Проверка логики фильтрации по активности (30 дней)

### TC-CAU-16: Последний вход — сегодня (0 дней)

**Предусловие:** Дата входа `alice` = сегодняшняя дата.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `alice` попадает в `active_users.csv`.

---

### TC-CAU-17: Последний вход — ровно 30 дней назад (граничное значение)

**Предусловие:** Дата входа `alice` = сегодня − 30 дней.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `alice` попадает в `active_users.csv` (условие `≤ 30` включительно).

---

### TC-CAU-18: Последний вход — 31 день назад (граничное значение)

**Предусловие:** Дата входа `alice` = сегодня − 31 день.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `alice` **не** попадает в `active_users.csv`.

---

### TC-CAU-19: Последний вход — давно (> 30 дней)

**Предусловие:** Дата входа `bob` = `2024-01-01`.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `bob` не попадает в `active_users.csv`.

---

## 6. Проверка фильтрации забаненных пользователей

### TC-CAU-20: Забаненный пользователь с активной датой входа

**Предусловие:** `charlie` забанен в `banned.json`, его дата входа в пределах 30 дней.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `charlie` **не** попадает в `active_users.csv`.

---

### TC-CAU-21: Незабаненный пользователь с активной датой входа

**Предусловие:** `alice` не забанена, дата входа в пределах 30 дней.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `alice` попадает в `active_users.csv`.

---

### TC-CAU-22: Все пользователи забанены

**Предусловие:** `banned.json` содержит всех пользователей из `users.txt`.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** `active_users.csv` содержит только заголовок.

---

## 7. Проверка выходного файла active_users.csv

### TC-CAU-23: Заголовок выходного файла

**Предусловие:** Корректные входные данные.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** Первая строка `active_users.csv` — `login,last_login`.

---

### TC-CAU-24: Формат строки — `login,date`

**Предусловие:** `alice` активна и не забанена, дата входа `2026-06-10`.  
**Шаги:** Проверить содержимое `active_users.csv`.  
**Ожидаемый результат:** Строка вида `alice,2026-06-10` (разделитель `,`, без пробелов).

---

### TC-CAU-25: Повторный запуск перезаписывает файл

**Предусловие:** `active_users.csv` уже существует с данными прошлого запуска.  
**Шаги:** Запустить скрипт повторно.  
**Ожидаемый результат:** `active_users.csv` перезаписан без дублирования строк.

---

### TC-CAU-26: Сообщение об успешном завершении

**Предусловие:** Корректные входные данные.  
**Шаги:** `./check_active_users.sh users.txt logins.csv banned.json`  
**Ожидаемый результат:** В stdout выводится `✅ Готово: active_users.csv`, код выхода `0`.
