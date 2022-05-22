package handlers.actions.submit

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService

class SubmitDeleteChannelHandler(di: DI) : ViewSubmissionHandler {
    private val supportChannelService: SupportChannelService by di.instance()

    override fun apply(req: ViewSubmissionRequest, context: ViewSubmissionContext): Response {
        val errors = mutableMapOf<String, String>()
        val channelId = req.payload.view.state.values["selectChannelBlock"]!!["selectChannel"]?.selectedOption?.value

        if (channelId == null) {
            errors["selectChannelBlock"] = "Канал не выбран."
        }

        return if (errors.isNotEmpty()) {
            context.ack { it.responseAction("errors").errors(errors) }
        } else {
            supportChannelService.deleteSupportChannel(channelId!!)
            context.ack()
        }
    }
}