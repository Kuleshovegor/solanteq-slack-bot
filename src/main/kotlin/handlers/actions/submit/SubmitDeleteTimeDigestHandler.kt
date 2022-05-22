package handlers.actions.submit

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import models.ScheduleTime
import org.kodein.di.DI
import org.kodein.di.instance
import service.EveryWeekTaskService

class SubmitDeleteTimeDigestHandler(di: DI, private val everyWeekTaskService: EveryWeekTaskService): ViewSubmissionHandler {
    private val teamId: String by di.instance("TEAM_ID")

    override fun apply(req: ViewSubmissionRequest, context: ViewSubmissionContext): Response {
        val errors = mutableMapOf<String, String>()
        val schStr = req.payload.view.state.values["selectTimeDigestBlock"]!!["selectTimeDigest"]?.selectedOption?.value
        lateinit var time: ScheduleTime
        if (schStr == null) {
            errors["selectTimeDigestBlock"] = "Время не выбрано."
        } else {
            val list = schStr.split(" ").map { it.toInt() }
            time = ScheduleTime(teamId, list[0], list[1], list[2])
        }

        return if (errors.isNotEmpty()) {
            context.ack { it.responseAction("errors").errors(errors) }
        } else {
            everyWeekTaskService.delete(time)
            context.ack()
        }
    }
}