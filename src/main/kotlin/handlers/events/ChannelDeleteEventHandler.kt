package handlers.events

import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.handler.BoltEventHandler
import com.slack.api.bolt.response.Response
import com.slack.api.model.event.ChannelDeletedEvent
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService

class ChannelDeleteEventHandler(di: DI) : BoltEventHandler<ChannelDeletedEvent> {
    private val supportChannelService: SupportChannelService by di.instance()

    override fun apply(req: EventsApiPayload<ChannelDeletedEvent>, context: EventContext): Response {
        if (supportChannelService.isSupportChannel(req.event.channel)) {
            supportChannelService.deleteSupportChannel(req.event.channel)
        }

        return context.ack()
    }
}