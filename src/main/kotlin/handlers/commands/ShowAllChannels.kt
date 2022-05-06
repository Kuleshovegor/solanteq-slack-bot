package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService
import service.UserService

class ShowAllChannels(di: DI) : SlashCommandHandler {
    private val supportChannelService: SupportChannelService by di.instance()
    private val userService: UserService by di.instance()

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        if (!userService.isAdmin(req.payload.userId)) {
            return context.ack("очень жаль, вы не админ")
        }

        val channelsInfo = "Каналы:" + System.lineSeparator() +
                supportChannelService.getAllChannels(req.payload.teamId)
                    .joinToString(System.lineSeparator()) {
                    """
                Название: ${it.name}
                Пользователи поддержки: ${
                        it.supportUserIds
                            .joinToString(" ") { userId -> userService.getName(userId) }
                    }
            """.trimIndent()
                }

        return context.ack(channelsInfo)
    }


}