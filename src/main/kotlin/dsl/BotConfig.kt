package dsl

data class UserDescription(val name: String, val email: String)

data class ChannelDescription(val name: String, val users: Set<UserDescription>)

data class BotConfig(val slackBotToken: String,
                     val teamId: String,
                     val channels: Map<String, ChannelDescription>,
                     val admins: Set<UserDescription>)