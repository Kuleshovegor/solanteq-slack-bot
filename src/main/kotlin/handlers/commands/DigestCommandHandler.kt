package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import dsl.BotConfig
import org.kodein.di.DI
import org.kodein.di.instance
import service.MessageService

class DigestCommandHandler(di: DI) : SlashCommandHandler {
    private val botConfig: BotConfig by di.instance()
    private val messageService: MessageService by di.instance()

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

       if (!botConfig.userNameToId.containsValue(req.payload.userId)) {
           return context.ack("You are not admin")
       }

        val userDigest = messageService.creatUserDigest(req.payload.userId)

        return DirectMessageHandler.sendMessageInDirect(userDigest, req.payload.userId, context, botConfig)
    }
}