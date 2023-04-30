<h1>OneCoffeeBot</h1>

<h2>Как запустить бота</h2>
- Пишем PrimeBot и создаем бота через него https://tt.me/primebot
- Берем токен, который он нам дал и запускаем мою прогу через main.
Токен принимается как аргумент.
- Готово, можно наслаждаться)

<h2>Как работать с БД</h2>
- Создаем нашу БД (sqlite на macOS предустановлен):
```
sqlite3 OneCoffee.db
```

- Поскольку все таблицы при первом запуске еще не созданы,
потребуется немного ручной работы.
- - Для деплоя на тачке:
```
java -jar <jar name> isRecreatingTablesNeeded=true
```
- - Для запуска из идеи нужно раскомментировать последнюю строчку
в StaticContext::initialize.
