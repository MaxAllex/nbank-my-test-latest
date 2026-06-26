# Скрипт: `merge_users.sh`

## 📌 Назначение

Скрипт объединяет информацию о пользователях из трёх источников:

1. `users.txt` — список логинов пользователей (один логин на строку)
2. `users.json` — JSON-словарь: логин → имя
3. `users.csv` — CSV-файл: логин, email

На выходе создается файл `full_users.csv` с объединёнными данными:

```csv
login,name,email
````

---

## Пример входных файлов

### `users.txt`

```
alice
bob
carol
```

### `users.json`

```json
{
  "alice": "Alice Smith",
  "bob": "Bob Johnson",
  "carol": "Carol Lee"
}
```

### `users.csv`

```csv
login,email
alice,alice@example.com
bob,bob@example.com
carol,carol@example.com
```

---

## Пример запуска

```bash
chmod +x merge_users.sh
./merge_users.sh users.txt users.json users.csv
```

---

## Выходной файл: `full_users.csv`

```csv
login,name,email
alice,Alice Smith,alice@example.com
bob,Bob Johnson,bob@example.com
carol,Carol Lee,carol@example.com
```

---

## ⚠️ Обработка ошибок

Если логин есть в `users.txt`, но отсутствует в `users.json` или `users.csv`, пользователь пропускается, и в консоль выводится предупреждение:

```
⚠️  Пропущен пользователь bob: нет имени или email
```

---

## Что можно протестировать

* Пользователи с полными данными объединяются корректно
* Пропуски в `users.json` или `users.csv` приводят к логированию предупреждения
* Скрипт не падает на пустых файлах
* Дубликаты логинов не дублируются в выходном файле
* Итоговый CSV начинается с корректного заголовка

---

## Зависимости

Скрипт использует:

* `jq` — для чтения JSON
* `bash` версии 4+ — для ассоциативных массивов

Убедитесь, что `jq` установлен:

```bash
sudo apt install jq  # для Ubuntu
```

---

## Структура проекта (пример)

```
repo/
├── merge_users.sh
├── users.txt
├── users.json
├── users.csv
├── full_users.csv
├── README.md  ← этот файл
```