package handlers.events

import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.handler.BoltEventHandler
import com.slack.api.bolt.response.Response
import com.slack.api.model.Conversation
import com.slack.api.model.event.MessageEvent
import dsl.BotConfig
import service.MessageService


class MessageEventHandler(
    private val botConfig: BotConfig,
    private val messageService: MessageService,
    private val channelsNameToConversation: Map<String, Conversation>,
    private val userNameToId: Map<String, String>
) : BoltEventHandler<MessageEvent> {
    override fun apply(req: EventsApiPayload<MessageEvent>?, context: EventContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        if (channelsNameToConversation.values.map { it.id }.contains(req.event.channel)) {
            val channelDescription =
                botConfig.channels.values.find { channelsNameToConversation[it.name]!!.id == req.event.channel }
            if (req.event.threadTs != null) {
                if (userNameToId.containsValue(req.event.user) &&
                    channelDescription!!.users.any { userNameToId[it.name] == req.event.user }
                ) {
                    messageService.deleteMessage(req.event.threadTs)
                }
                return context.ack()
            }

            val linkResp = context.client().chatGetPermalink { r ->
                r.token(botConfig.slackBotToken)
                    .channel(req.event.channel)
                    .messageTs(req.event.ts)

            }

            messageService.addMessage(models.Message(req.event.ts, linkResp.permalink, req.event.channel))
        }
        return context.ack()
    }
}