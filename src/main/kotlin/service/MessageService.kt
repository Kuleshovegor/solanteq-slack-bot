package service;

import com.slack.api.methods.MethodsClient
import dsl.BotConfig
import org.kodein.di.DI;
import org.kodein.di.instance

class MessageService(di: DI) {
    private val slackClient: MethodsClient by di.instance("slackClient")
    private val botConfig: BotConfig by di.instance()

    fun sendMessage(userId: String, text: String) {
        slackClient.conversationsOpen { r ->
            r.token(botConfig.slackBotToken)
                .users(listOf(userId))
        }

        slackClient.chatPostMessage {r ->
            r.token(botConfig.slackBotToken)
                .channel(userId)
                .text(text)
        }
    }
}
