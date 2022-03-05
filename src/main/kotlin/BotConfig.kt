import dsl.description

val BOT_CONFIG = description {
    slackBotToken = "<your slack bot token>"
    teamId = "<workflow's id>"

    channels {
        channel("<channel name>") {
            users {
                user("<user's name in workflow>")
            }
        }
    }
}