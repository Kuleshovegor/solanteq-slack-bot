package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.response.Response
import dsl.BotConfig

class DirectMessageHandler {
    companion object {
        private const val DEFAULT_ERROR_RESPONSE = "Произошла ошибка :pensive:"
        private const val DEFAULT_OK_RESPONSE = "Вам отправлено личное сообщение"

        fun sendMessageInDirect(
            text: String,
            userId: String,
            context: SlashCommandContext,
            botConfig: BotConfig
        ): Response {

            val conversationsOpenResponse = context.client().conversationsOpen { r ->
                r.token(botConfig.slackBotToken)
                    .users(listOf(userId))
            }

            if (!conversationsOpenResponse.isOk && !conversationsOpenResponse.isAlreadyOpen) {
                context.logger.error(conversationsOpenResponse.error)

                return context.ack(DEFAULT_ERROR_RESPONSE)
            }

            val chatPostMessageResponse = context.client()
                .chatPostMessage { r ->
                    r.token(botConfig.slackBotToken)
                        .channel(userId)
                        .text(text)
                }

            if (!chatPostMessageResponse.isOk) {
                context.logger.error(conversationsOpenResponse.error)

                return context.ack(DEFAULT_ERROR_RESPONSE)
            }

            return context.ack(DEFAULT_OK_RESPONSE)
        }
    }
}