package handlers.commands

import DAYS_OF_WEEK
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import models.ScheduleTime
import org.kodein.di.DI
import org.kodein.di.instance
import service.EveryWeekTaskService
import service.UserService

class AddTimeNotificationHandler(di: DI, private val everyWeekTaskService: EveryWeekTaskService) : SlashCommandHandler {
    private val userService: UserService by di.instance()

    private fun getData(string: String): Pair<Int, Pair<Int, Int>>? {
        val dayOfWeek: Int
        val list = string.split("\\s+".toRegex())
        if (list.size != 2) {
            return null
        }
        dayOfWeek = DAYS_OF_WEEK[list[0].lowercase()] ?: return null
        val strTime = list[1]

        if (strTime.split(":").size != 2) {
            return null
        }

        val hoursStr = strTime.split(":")[0]
        val minuteStr = strTime.split(":")[1]

        val hours = hoursStr.toIntOrNull() ?: return null
        val minute = minuteStr.toIntOrNull() ?: return null

        if (!(hours in 0 until 24 && minute in  0 until 60)) {
            return null
        }

        return dayOfWeek to (hours to minute)
    }

    override fun apply(req: SlashCommandRequest, context: SlashCommandContext): Response {
        if (!userService.isAdmin(req.payload.userId)) {
            return context.ack("You must be admin to add a notification time.")
        }

        val (dayOfWeek, time) = getData(req.payload.text) ?: return context.ack("Invalid request.")

        everyWeekTaskService.addAndSaveNewTime(ScheduleTime(req.payload.teamId, dayOfWeek, time.first, time.second))

        return context.ack("The time added.")
    }
}