package handlers.events

import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.handler.BoltEventHandler
import com.slack.api.bolt.response.Response
import com.slack.api.model.event.MessageDeletedEvent
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService
import service.UnansweredMessageService

class MessageDeleteEventHandler(di: DI) : BoltEventHandler<MessageDeletedEvent> {
    private val unansweredMessageService: UnansweredMessageService by di.instance()
    private val supportChannelService: SupportChannelService by di.instance()

    override fun apply(req: EventsApiPayload<MessageDeletedEvent>?, context: EventContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        if (supportChannelService.isSupportChannel(req.event.channel)) {
            unansweredMessageService.deleteMessage(req.event.deletedTs)
        }

        return context.ack()
    }
}