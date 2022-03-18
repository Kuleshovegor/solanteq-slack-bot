package dsl

import com.slack.api.model.Conversation

data class UserDescription(val name: String, val email: String)

data class ChannelDescription(val name: String, val users: Set<UserDescription>)

data class BotConfig(val slackBotToken: String,
                     val teamId: String,
                     val channels: Map<String, ChannelDescription>,
                     val admins: Set<UserDescription>) {

    val userDescriptionToChannels = mutableMapOf<UserDescription, MutableList<String>>()

    init {
        channels.forEach { (_, channelDescription) ->
            channelDescription.users.forEach { userDescription ->
                userDescriptionToChannels.putIfAbsent(userDescription, mutableListOf())
                userDescriptionToChannels[userDescription]?.add(channelDescription.name)
            }
        }
    }

    val userNameToId = mutableMapOf<String, String>()
    val channelsNameToConversation = mutableMapOf<String, Conversation>()
}