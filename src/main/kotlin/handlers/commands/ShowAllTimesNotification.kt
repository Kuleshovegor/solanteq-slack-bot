package handlers.commands

import DAYS_OF_WEEK_TO_STRING
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.EveryWeekTaskService
import service.UserService

class ShowAllTimesNotification(di: DI, private val everyWeekTaskService: EveryWeekTaskService) : SlashCommandHandler {
    private val userService: UserService by di.instance()

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        if (!userService.isAdmin(req.payload.userId)) {
            return context.ack("очень жаль, вы не админ")
        }

        val timesNotificationInfo = "Расписание:" + System.lineSeparator() +
            everyWeekTaskService.getAllTimes(req.payload.teamId).joinToString(System.lineSeparator()) {
                "${DAYS_OF_WEEK_TO_STRING[it.dayOfWeek]} ${it.hours}:${if (it.minutes < 10) "0" else ""}${it.minutes}"
            }

        return context.ack(timesNotificationInfo)
    }
}