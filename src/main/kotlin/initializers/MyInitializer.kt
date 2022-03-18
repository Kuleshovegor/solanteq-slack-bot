package initializers

import com.slack.api.bolt.App
import com.slack.api.bolt.Initializer
import dsl.BotConfig
import models.UserChannels
import org.kodein.di.DI
import org.kodein.di.instance
import repository.UserRepository

class MyInitializer(di: DI) : Initializer {
    private val botConfig: BotConfig by di.instance()
    private val userRepository: UserRepository by di.instance()

    override fun accept(initApp: App?) {
        check(initApp != null)

        val usersListResponse =
            initApp.client().usersList { r -> r.token(botConfig.slackBotToken).teamId(botConfig.teamId) }

        check(usersListResponse.isOk) { "Init error: bad response: ${usersListResponse.error}" }

        val usersList = usersListResponse.members
        val userNames = botConfig.userDescriptionToChannels.keys.map { it.name }.toSet()

        usersList.forEach {
            if (userNames.contains(it.name)) {
                botConfig.userNameToId[it.name] = it.id
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
                botConfig.channelsNameToConversation[it.name] = it
            }
        }

        botConfig.userDescriptionToChannels.entries.forEach { (key, value) ->
            val channelsId = value.map { botConfig.channelsNameToConversation[it]!! }
            userRepository.addUser(UserChannels(botConfig.userNameToId[key.name]!!, channelsId))
        }
    }

}