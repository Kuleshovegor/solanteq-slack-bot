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
            return context.ack("что-то пошло не так")
        }

        if (usrResp.user.isBot) {
            return context.ack("no")
        }

        if (!usrResp.user.isAdmin) {
            return context.ack("очень жаль, вы не админ")
        }

        val tag = req.payload.text.trim()
        val name = tag.slice(1 until tag.length)

        if (!supportChannelService.deleteSupportChannelByName(name)) {
            return context.ack("некорректый тег канала")
        }

        return context.ack("канал удален из бота")
    }
}