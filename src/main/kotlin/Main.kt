import handlers.commands.DigestCommandHandler
import handlers.commands.HelloCommandHandler
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.AppConfig.AppConfigBuilder
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.model.Conversation
import com.slack.api.model.event.MessageEvent
import dsl.UserDescription
import handlers.events.MessageEventHandler
import models.UserChannels
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.litote.kmongo.KMongo
import repository.SlackMessageRepository
import repository.UserRepository
import service.MessageService


fun main() {

    val app = App()

    val di = DI {
        bindSingleton("database") {
            KMongo.createClient().getDatabase("CompassTest")
        }
        bindSingleton { UserRepository(instance("database")) }
        bindSingleton { SlackMessageRepository(instance("database")) }
        bindSingleton { MessageService(instance(), instance()) }

    }

    val userDescriptionToChannels = mutableMapOf<UserDescription, MutableList<String>>()

    BOT_CONFIG.channels.forEach { (_, channelDescription) ->
        channelDescription.users.forEach { userDescription ->
            userDescriptionToChannels.putIfAbsent(userDescription, mutableListOf())
            userDescriptionToChannels[userDescription]?.add(channelDescription.name)
        }
    }

    val userNameToId = mutableMapOf<String, String>()
    val channelsNameToConversation = mutableMapOf<String, Conversation>()

    val messageService: MessageService by di.instance()

    app.initializer("nameInitializer") { initApp ->
        val userRepository: UserRepository by di.instance()
        val usersListResponse =
            initApp.client().usersList { r -> r.token(BOT_CONFIG.slackBotToken).teamId(BOT_CONFIG.teamId) }

        check(usersListResponse.isOk) { "Init error: bad response: ${usersListResponse.error}" }

        val usersList = usersListResponse.members
        val userNames = userDescriptionToChannels.keys.map { it.name }.toSet()

        usersList.forEach {
            if (userNames.contains(it.name)) {
                userNameToId[it.name] = it.id
            }
        }

        val channelListResponse = initApp.client().conversationsList { r ->
            r.token(BOT_CONFIG.slackBotToken)
                .teamId(BOT_CONFIG.teamId)
        }

        check(channelListResponse.isOk) { "Init error: bad response: ${channelListResponse.error}" }

        val channelList = channelListResponse.channels
        val channelNames = BOT_CONFIG.channels.keys

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

    app.command("/hello", HelloCommandHandler(BOT_CONFIG))

    app.command("/digest", DigestCommandHandler(BOT_CONFIG, messageService))

    app.event(MessageEvent::class.java, MessageEventHandler(BOT_CONFIG, messageService, channelsNameToConversation, userNameToId))

    val server = SlackAppServer(app)
    server.start()
}