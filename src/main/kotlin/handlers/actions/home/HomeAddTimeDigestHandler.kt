package handlers.actions.home

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.handler.builtin.BlockActionHandler
import com.slack.api.bolt.request.builtin.BlockActionRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import service.ModalService

class HomeAddTimeDigestHandler(di: DI) : BlockActionHandler {
    private val token: String by di.instance("SLACK_BOT_TOKEN")
    private val logger = LoggerFactory.getLogger("HomeAddTimeDigestHandler")

    override fun apply(req: BlockActionRequest, context: ActionContext): Response {
        val response = context.client().viewsOpen { vo ->
            vo.token(token)
                .triggerId(req.payload.triggerId)
                .view(ModalService.addNewTime)
        }

        if (!response.isOk) {
            logger.error(response.error)
        }

        return context.ack()
    }
}