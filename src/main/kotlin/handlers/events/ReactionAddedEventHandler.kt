package handlers.events

import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.handler.BoltEventHandler
import com.slack.api.bolt.response.Response
import com.slack.api.model.event.ReactionAddedEvent
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService
import service.UnansweredMessageService

class ReactionAddedEventHandler(di: DI) : BoltEventHandler<ReactionAddedEvent> {
    private val supportChannelService: SupportChannelService by di.instance()
    private val unansweredMessageService: UnansweredMessageService by di.instance()

    override fun apply(req: EventsApiPayload<ReactionAddedEvent>, context: EventContext): Response {
        if (supportChannelService.isSupportChannel(req.event.item.channel)
            && supportChannelService.isSupportUser(req.event.user)) {
            unansweredMessageService.deleteMessage(req.event.item.ts)
        }

        return context.ack()
    }

}