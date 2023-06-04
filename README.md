<h1>OneCoffeeBot</h1>

<h2>Как запустить бота</h2>
- Пишем PrimeBot и создаем бота через него https://tt.me/primebot, также регистрируемся на сайте https://robbi.ai/
- Заходим в файл application.properties и выполняем настройку
- api.key - токен для доступа
- robbi.login - логин для АПИ robbi.ai (определение 18+ контента)
- robbi.password - пароль для АПИ robbi.ai
- sql.reinit - стереть старую базу данных и создать новую с нуля
- Готово, можно наслаждаться)

<h2>Как работать с БД</h2>
- Создаем нашу БД (sqlite на macOS предустановлен):
```
sqlite3 OneCoffee
```

- Поскольку все таблицы при первом запуске еще не созданы,
потребуется немного ручной работы.
- - Для деплоя на тачке:
```
java -jar <jar name> [isRecreatingTablesNeeded=true]
```
- - Для запуска из идеи нужно проставить значение `true` в StaticContext::isRecreatingTablesNeeded. 
После пересоздания нужно вернуть все, как было.
