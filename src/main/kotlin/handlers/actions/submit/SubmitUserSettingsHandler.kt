package handlers.actions.submit

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import models.TaskPriority
import models.TaskType
import models.UserSettings
import org.kodein.di.DI
import org.kodein.di.instance
import service.UserSettingsService

class SubmitUserSettingsHandler(di: DI) : ViewSubmissionHandler {
    private val userSettingsService: UserSettingsService by di.instance()

    private fun isMuted(str: String): Boolean {
        return "mute" == str
    }

    override fun apply(req: ViewSubmissionRequest, context: ViewSubmissionContext): Response {
        val errors = mutableMapOf<String, String>()
        val isSlackMutedStr =
            req.payload.view.state.values["selectSlackMuteBlock"]!!["selectSlackMute"]?.selectedOption?.value!!
        val isYouTrackMutedStr =
            req.payload.view.state.values["selectYouTrackMuteBlock"]!!["selectYouTrackMute"]?.selectedOption?.value!!
        val prioritiesStr =
            req.payload.view.state.values["selectYouTrackPriorityBlock"]!!["selectYouTrackPriority"]?.selectedOptions?.map { it.value!! }!!
        val typesStr =
            req.payload.view.state.values["selectYouTrackTypesBlock"]!!["selectYouTrackTypes"]?.selectedOptions?.map { it.value!! }!!
        val projectsStr = req.payload.view.state.values["inputYouTrackProjectBlock"]!!["inputYouTrackProject"]?.value
            ?: ""

        val isSlackMuted = isMuted(isSlackMutedStr)
        val isYouTrackMuted = isMuted(isYouTrackMutedStr)
        val priorities = prioritiesStr.map { TaskPriority.valueOf(it) }.toSet()
        val types = typesStr.map { TaskType.valueOf(it) }.toSet()
        val projects = projectsStr.split(",").map { it.trim() }.toSet()

        userSettingsService.setUserSettings(
            UserSettings(
                req.payload.user.id,
                isSlackMuted,
                isYouTrackMuted,
                projects,
                priorities,
                types
            )
        )

        return context.ack()
    }
}