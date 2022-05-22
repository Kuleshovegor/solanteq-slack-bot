package handlers.actions.home

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.handler.builtin.BlockActionHandler
import com.slack.api.bolt.request.builtin.BlockActionRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.DigestService

class HomeDigestActionHandler(di: DI) : BlockActionHandler {
    private val digestService: DigestService by di.instance()

    override fun apply(req: BlockActionRequest, context: ActionContext): Response {
        val response = digestService.sendUserDigest(req.payload.user.id)

        if (!response.isOk) {
            context.logger.error(response.error)
            return context.ack()
        }

        return context.ack()
    }
}