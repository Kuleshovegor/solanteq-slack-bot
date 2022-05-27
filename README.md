# Smart-reminder
Slack bot c интеграцией YouTrack, с поддержкой
уведомлений о событиях в YouTrack, регулярными digest-уведомлениями в
Slack, с напоминаниями о не отвеченных сообщениях.
## Установка
1. [Создать](https://api.slack.com/) приложение, скопировать [manifest](slack-manifest-template.yml), вставить ссылку на сервер и название бота. Установить в рабочее пространство.
2. Развернуть сервер через Docker. Скопировать [docker-compose.yaml](docker-compose-template.yaml) и прописать переменные. Запустить по команде docker-compose up.
3. Скачать [slack-reminder-bot-youtrack](slack-reminder-bot-youtrack-workflow.zip) и импортировать в рабочий процесс вашего YouTrack Workspace. И также пропишите ссылку на сервер.
