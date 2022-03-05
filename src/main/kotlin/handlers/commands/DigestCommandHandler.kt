package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import dsl.BotConfig
import service.MessageService

class DigestCommandHandler(private val botConfig: BotConfig, private val messageService: MessageService) : SlashCommandHandler {
    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        val userDigest = messageService.creatUserDigest(req.payload.userId)

        return DirectMessageHandler.sendResponseInDirect(userDigest, req, context, botConfig)
    }
}