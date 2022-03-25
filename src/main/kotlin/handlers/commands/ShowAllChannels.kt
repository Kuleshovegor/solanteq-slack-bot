package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService

class ShowAllChannels(di: DI): SlashCommandHandler {
    private val supportChannelService: SupportChannelService by di.instance()

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        return context.ack(supportChannelService.getAllChannels(req.payload.teamId).toString())
    }


}