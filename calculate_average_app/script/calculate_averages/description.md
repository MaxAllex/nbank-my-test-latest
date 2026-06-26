# Скрипт: `calculate_averages.sh`

## Назначение

Скрипт обрабатывает CSV-файл с транзакциями пользователей и вычисляет **средние расходы в месяц**.  
Результат сохраняется в файл `averages.csv`.

---

## Входной формат: `transactions.csv`

```csv
user,date,category,amount
alice,2025-01-15,food,1200
alice,2025-01-20,transport,500
bob,2025-02-03,food,800
````

* `user` — логин пользователя
* `date` — дата транзакции в формате `YYYY-MM-DD`
* `category` — категория расходов
* `amount` — сумма в рублях

---

## Выходной формат: `averages.csv`

```csv
user,month,average_spending
alice,ALL,850.0
bob,ALL,800.0
```

## ⏱ Нефункциональные требования

| Объем данных      | Время выполнения |
| ----------------- | ---------------- |
| ≤ 10 000 строк    | ≤ 1 секунда      |
| ≤ 100 000 строк   | ≤ 5 секунд       |
| ≤ 1 000 000 строк | ≤ 20 секунд      |

---

## Пример запуска

```bash
chmod +x calculate_averages.sh
./calculate_averages.sh data/transactions_100000.csv
```

---

## Структура проекта (пример)

```
expenses-analyzer/
├── calculate_averages.sh
├── data/
│   ├── transactions_1k.csv
│   ├── transactions_100k.csv
│   └── transactions_1M.csv
├── averages.csv                  # Результат выполнения
├── README.md                     # Этот файл
```