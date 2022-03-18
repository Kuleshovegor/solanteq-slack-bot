package handlers.events

import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.handler.BoltEventHandler
import com.slack.api.bolt.response.Response
import com.slack.api.model.event.MessageEvent
import dsl.BotConfig
import org.kodein.di.DI
import org.kodein.di.instance
import service.MessageService


class MessageEventHandler(di: DI) : BoltEventHandler<MessageEvent> {
    private val botConfig: BotConfig by di.instance()
    private val messageService: MessageService by di.instance()

    override fun apply(req: EventsApiPayload<MessageEvent>?, context: EventContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        if (botConfig.channelsNameToConversation.values.map { it.id }.contains(req.event.channel)) {
            val channelDescription =
                botConfig.channels.values.find { botConfig.channelsNameToConversation[it.name]!!.id == req.event.channel }
            if (req.event.threadTs != null) {
                if (botConfig.userNameToId.containsValue(req.event.user) &&
                    channelDescription!!.users.any { botConfig.userNameToId[it.name] == req.event.user }
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