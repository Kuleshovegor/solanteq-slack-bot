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

    override fun apply(req: SlashCommandRequest, context: SlashCommandContext): Response {
        if (!userService.isAdmin(req.payload.userId)) {
            return context.ack("You must be admin to see a list of support chat.")
        }

        val channelsInfo = "Channels:" + System.lineSeparator() +
                supportChannelService.getAllChannels(req.payload.teamId)
                    .joinToString(System.lineSeparator()) {
                    """
                Name: ${it.name}
                Support users: ${
                        it.supportUserIds
                            .joinToString(" ") { userId -> userService.getName(userId) }
                    }
            """.trimIndent()
                }

        return context.ack(channelsInfo)
    }


}