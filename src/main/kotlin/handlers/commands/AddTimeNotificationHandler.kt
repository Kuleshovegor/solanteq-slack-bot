package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import service.EveryWeekTaskService
import java.util.Calendar

class AddTimeNotificationHandler(di: DI, private val everyWeekTaskService: EveryWeekTaskService) : SlashCommandHandler {
    companion object {
        val DAYS_OF_WEEK = mapOf(
            "понедельник" to Calendar.MONDAY,
            "вторник" to Calendar.TUESDAY,
            "среда" to Calendar.WEDNESDAY,
            "четверг" to Calendar.THURSDAY,
            "пятница" to Calendar.FRIDAY,
            "суббота" to Calendar.SATURDAY,
            "воскресенье" to Calendar.SUNDAY
        )
    }

    fun get(string: String): Pair<Int, Pair<Int, Int>>? {
        val dayOfWeek: Int
        val list = string.split("\\s".toRegex())
        if (list.size != 2) {
            return null
        }
        dayOfWeek = DAYS_OF_WEEK[list[0]] ?: return null
        val strTime = list[1]
        if (!strTime.matches("^\\d\\d:\\d\\d$".toRegex())) {
            return null
        }
        val hours = strTime.slice(0..1).toInt()
        val minute = strTime.slice(3..4).toInt()

        return dayOfWeek to (hours to minute)
    }

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        val (dayOfWeek, time) = get(req.payload.text) ?: return context.ack("неверный запрос")

        everyWeekTaskService.addAndSaveNewTime(req.payload.teamId, dayOfWeek, time.first, time.second)

        return context.ack("время добавлено")
    }
}