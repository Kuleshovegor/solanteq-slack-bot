package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService

class DeleteSupportChannel(di: DI) : SlashCommandHandler {
    private val supportChannelService: SupportChannelService by di.instance()
    private val token: String by di.instance("SLACK_BOT_TOKEN")

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        val usrResp = context.client().usersInfo { r -> r.token(token).user(req.payload.userId) }

        if (!usrResp.isOk) {
            context.logger.error(usrResp.error)
            return context.ack("Could not get information about the user who requested the deletion of the support chat.")
        }

        if (usrResp.user.isBot) {
            return context.ack("You are a bot. The bot cannot delete support chats.")
        }

        if (!usrResp.user.isAdmin) {
            return context.ack("You must be admin to delete a support chat.")
        }

        val tag = req.payload.text.trim()
        val name = tag.slice(1 until tag.length)

        if (!supportChannelService.deleteSupportChannelByName(name)) {
            return context.ack("Invalid channel tag.")
        }

        return context.ack("Support chat deleted from bot.")
    }
}