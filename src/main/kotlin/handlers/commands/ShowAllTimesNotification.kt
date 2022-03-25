package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import service.EveryWeekTaskService

class ShowAllTimesNotification(di: DI, private val everyWeekTaskService: EveryWeekTaskService): SlashCommandHandler {
    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        return context.ack(everyWeekTaskService.getAllTimes(req.payload.teamId).toString())
    }
}