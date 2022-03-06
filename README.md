# Slack-bot
## Здесь много плохого кода. Репозиторий используется для теста технологий.
### Что сделано на данный момент
1. Полуподдержка  сообщений из чатов поддержки.
2. Описание конфига бота с помощью дсл в файле BotConfig.kt

### Как запустить бота локально
1. Скачайте [ngrok](https://ngrok.com/download).
2. Запустите тунель по команде <code>ngrok http 3000</code>. Запомните Forwarding,
это адрес, на который будут пересылаться запросы из слака, который в своем случае будет пересылать
на ваш *localhost:3000*.
3. Создайте своего бота в слаке в [своих приложениях](https://api.slack.com/apps/new).
4. Открыв настройки своего бота перейдите в пункт *OAuth & Permissions* там записан ваш 
*Bot User OAuth Token*, который и необходимо будет ввести в [конфиг бота](src/main/kotlin/BotConfig.kt).
5. В этом же разделе в Scopes выдайте боту права.
6. В разделе *Slash command* добавьте комманды <code>/hello</code> и <code>/digest</code>.
В качестве *Request URL* укажите *<Forwarding из пункта 2.>/slack/events*
7. В *Event Subscriptions* В качестве *Request URL* укажите *<Forwarding из пункта 2.>/slack/events*.
В этом же разделе в *Subscribe to bot events* добавьте *message.channels*
8. Введите id вашего workspace. [как узнать идентификатор](https://stackoverflow.com/questions/40940327/what-is-the-simplest-way-to-find-a-slack-team-id-and-a-channel-id)
9. Для задания группы для юзеров которым будет приходить уведовления о неотвеченных
сообщениях в чатах поддержки используйте шаблон в [конфиге](src/main/kotlin/BotConfig.kt)
(на данный момент поддерживаются только каналы в том же workspace).
10. Поднимите локально базу данных mongoDB. [ссылка на установку](https://www.mongodb.com/try/download/community).
При установке выбирайте графический интерфейс *Compass* и локальное расположение. 
Автоматически сервер должен расположиться на *localhost:27017*. 
Создайте базу данных и впишите название в [Main](src/main/kotlin/Main.kt).
(В будущем возможно переедем в облако).
11. Запустите Main.