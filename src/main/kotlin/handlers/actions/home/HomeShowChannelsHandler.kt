package handlers.actions.home

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.handler.builtin.BlockActionHandler
import com.slack.api.bolt.request.builtin.BlockActionRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import service.ModalService
import service.SupportChannelService

class HomeShowChannelsHandler(di: DI): BlockActionHandler {
    private val token: String by di.instance("SLACK_BOT_TOKEN")
    private val teamId: String by di.instance("TEAM_ID")
    private val supportChannelService: SupportChannelService by di.instance()
    private val modalService: ModalService by di.instance()

    override fun apply(req: BlockActionRequest, context: ActionContext): Response {
        val response = context.client().viewsOpen { vo ->
            vo.token(token)
                .triggerId(req.payload.triggerId)
                .view(modalService.getAllChannels(supportChannelService.getAllChannels(teamId)))
        }

        if (!response.isOk) {
            context.logger.error(response.error)
        }

        return context.ack()
    }
}