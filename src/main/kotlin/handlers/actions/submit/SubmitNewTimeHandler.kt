package handlers.actions.submit

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import models.ScheduleTime
import org.kodein.di.DI
import org.kodein.di.instance
import service.EveryWeekTaskService

class SubmitNewTimeHandler(di: DI, private val everyWeekTaskService: EveryWeekTaskService): ViewSubmissionHandler {
    private val teamId: String by di.instance("TEAM_ID")

    override fun apply(req: ViewSubmissionRequest, context: ViewSubmissionContext): Response {
        val errors = mutableMapOf<String, String>()
        val day = req.payload.view.state.values["selectDayBlock"]!!["selectDay"]?.selectedOption?.value

        if (day == null) {
            errors["selectDayBlock"] = "Day is not chosen."
        }

        val time = req.payload.view.state.values["selectTimeBlock"]!!["selectTime"]?.selectedTime
        var pairTime = 0 to 0

        if (time== null) {
            errors["selectTimeBlock"] = "Time is not chosen."
        } else {
            pairTime = time.split(":")[0].toInt() to time.split(":")[1].toInt()
        }

        val scheduleTime = ScheduleTime(teamId, day!!.toInt(), pairTime.first, pairTime.second)

        if (everyWeekTaskService.contains(scheduleTime)) {
            errors["selectTimeBlock"] = "This date is not free."
            errors["selectDayBlock"] = "This date is not free."
        }

        return if (errors.isNotEmpty()) {
            context.ack { it.responseAction("errors").errors(errors) }
        } else {
            everyWeekTaskService.addAndSaveNewTime(scheduleTime)
            context.ack()
        }
    }
}