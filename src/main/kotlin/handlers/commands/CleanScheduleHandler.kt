package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.EveryWeekTaskService
import service.UserService

class CleanScheduleHandler(di: DI, private val everyWeekTaskService: EveryWeekTaskService) : SlashCommandHandler {
    private val userService: UserService by di.instance()

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        if (!userService.isAdmin(req.payload.userId)) {
            return context.ack("You must be admin to clean notification schedule.")
        }

        everyWeekTaskService.clean()

        return context.ack("The schedule cleaned.")
    }
}