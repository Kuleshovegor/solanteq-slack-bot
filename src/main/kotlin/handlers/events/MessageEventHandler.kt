package handlers.events

import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.handler.BoltEventHandler
import com.slack.api.bolt.response.Response
import com.slack.api.model.event.MessageEvent
import models.UnansweredMessage
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService
import service.UnansweredMessageService


class MessageEventHandler(di: DI) : BoltEventHandler<MessageEvent> {
    private val token: String by di.instance("SLACK_BOT_TOKEN")
    private val unansweredMessageService: UnansweredMessageService by di.instance()
    private val supportChannelService: SupportChannelService by di.instance()

    override fun apply(req: EventsApiPayload<MessageEvent>?, context: EventContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        if (supportChannelService.isSupportChannel(req.event.channel)) {
            if (req.event.threadTs != null) {
                if (supportChannelService.isSupportUser(req.event.user, req.event.channel)) {
                    unansweredMessageService.deleteMessage(req.event.threadTs)
                }
            } else {
                if (!supportChannelService.isSupportUser(req.event.user, req.event.channel)) {
                    val linkResp = context.client().chatGetPermalink { r ->
                        r.token(token)
                            .channel(req.event.channel)
                            .messageTs(req.event.ts)

                    }

                    unansweredMessageService.addMessage(
                        UnansweredMessage(
                            req.event.ts,
                            linkResp.permalink,
                            req.event.channel
                        )
                    )
                }
            }
        }

        return context.ack()
    }
}