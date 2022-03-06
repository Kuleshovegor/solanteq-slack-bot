package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import dsl.BotConfig

class HelloCommandHandler(private val botConfig: BotConfig) : SlashCommandHandler {
    companion object {
        const val HELLO_TEXT = "hello :wave:"
    }

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        return DirectMessageHandler.sendResponseInDirect(HELLO_TEXT, req, context, botConfig)
    }
}