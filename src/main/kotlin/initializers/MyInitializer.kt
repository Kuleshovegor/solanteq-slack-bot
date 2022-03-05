package initializers

import com.slack.api.bolt.App
import com.slack.api.bolt.Initializer
import dsl.BotConfig
import models.UserChannels
import org.kodein.di.instance
import repository.UserRepository

/*class MyInitializer(private val botConfig: BotConfig, private val userRepository: UserRepository) : Initializer {
    override fun accept(initApp: App?) {
        check(initApp != null)

        val usersListResponse =
            initApp.client().usersList { r -> r.token(botConfig.slackBotToken).teamId(botConfig.teamId) }

        check(usersListResponse.isOk) { "Init error: bad response: ${usersListResponse.error}" }

        val usersList = usersListResponse.members
        val userNames = userDescriptionToChannels.keys.map { it.name }.toSet()

        usersList.forEach {
            if (userNames.contains(it.name)) {
                userNameToId[it.name] = it.id
            }
        }

        val channelListResponse = initApp.client().conversationsList { r ->
            r.token(botConfig.slackBotToken)
                .teamId(botConfig.teamId)
        }

        check(channelListResponse.isOk) { "Init error: bad response: ${channelListResponse.error}" }

        val channelList = channelListResponse.channels
        val channelNames = botConfig.channels.map { it.key }.toSet()

        channelList.forEach {
            if (channelNames.contains(it.name)) {
                channelsNameToConversation[it.name] = it
            }
        }

        userDescriptionToChannels.entries.forEach { (key, value) ->
            val channelsId = value.map { channelsNameToConversation[it]!! }
            userRepository.addUser(UserChannels(userNameToId[key.name]!!, channelsId))
        }
    }

}*/