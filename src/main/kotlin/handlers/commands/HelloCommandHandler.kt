package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance

class HelloCommandHandler(di: DI) : SlashCommandHandler {
    private val token: String by di.instance("SLACK_BOT_TOKEN")

    companion object {
        const val HELLO_TEXT = "hello :wave:"
    }

    override fun apply(req: SlashCommandRequest, context: SlashCommandContext): Response {
        return DirectMessageHandler.sendMessageInDirect(HELLO_TEXT, req.payload.userId, context, token)
    }
}