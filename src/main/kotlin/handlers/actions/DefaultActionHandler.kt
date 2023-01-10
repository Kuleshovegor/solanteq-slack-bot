package handlers.actions

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.handler.builtin.BlockActionHandler
import com.slack.api.bolt.request.builtin.BlockActionRequest
import com.slack.api.bolt.response.Response

class DefaultActionHandler: BlockActionHandler {
    override fun apply(req: BlockActionRequest, context: ActionContext): Response {
        return context.ack()
    }
}