package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import dsl.BotConfig
import org.kodein.di.DI
import org.kodein.di.instance

class HelloCommandHandler(di: DI) : SlashCommandHandler {
    private val botConfig: BotConfig by di.instance()

    companion object {
        const val HELLO_TEXT = "hello :wave:"
    }

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        return DirectMessageHandler.sendMessageInDirect(HELLO_TEXT, req.payload.userId, context, botConfig)
    }
}