package service

import com.slack.api.methods.MethodsClient
import org.kodein.di.DI
import org.kodein.di.instance

class MessageService(di: DI) {
    private val slackClient: MethodsClient by di.instance("slackClient")
    private val token: String by di.instance("SLACK_BOT_TOKEN")

    fun sendMessage(userId: String, text: String) {
        slackClient.conversationsOpen { r ->
            r.token(token)
                .users(listOf(userId))
        }

        slackClient.chatPostMessage { r ->
            r.token(token)
                .channel(userId)
                .text(text)
        }
    }
}
