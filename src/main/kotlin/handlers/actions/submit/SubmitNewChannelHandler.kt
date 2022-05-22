package handlers.actions.submit

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import models.SupportChannel
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService
import service.UserService

class SubmitNewChannelHandler(di: DI) : ViewSubmissionHandler {
    private val supportChannelService: SupportChannelService by di.instance()
    private val userService: UserService by di.instance()
    private val teamId: String by di.instance("TEAM_ID")
    private val token: String by di.instance("SLACK_BOT_TOKEN")

    override fun apply(req: ViewSubmissionRequest, context: ViewSubmissionContext): Response {
        val errors = mutableMapOf<String, String>()
        val channelId = req.payload.view.state.values["selectChannelBlock"]!!["selectChannel"]?.selectedChannel
        var channelName = ""

        if (channelId == null) {
            errors["selectChannelBlock"] = "Канал не выбран."
        } else if (supportChannelService.isSupportChannel(channelId)) {
            errors["selectChannelBlock"] = "Канал уже добавлен."
        } else {
            channelName = context.client().conversationsInfo { r ->
                r
                    .token(token)
                    .channel(channelId)
            }.channel.name
        }

        val userIds = req.payload.view.state.values["selectUsersBlock"]!!["selectUsers"]?.selectedUsers

        if (userIds == null || userIds.isEmpty()) {
            errors["selectUsersBlock"] = "Пользователи не выбраны."
        } else {
            val botUsers = userIds.map { userService.getUserInfoById(it) }.filter { it.isBot || it.isWorkflowBot }
            if (botUsers.isNotEmpty()) {
                errors["selectUsersBlock"] = "Пользователи ${botUsers.joinToString(", ") { it.name }}  - боты."
            }
        }

        return if (errors.isEmpty()) {
            supportChannelService.addSupportChannel(SupportChannel(teamId, channelId!!, channelName, userIds!!.toSet()))
            context.ack()
        } else {
            context.ack { it.responseAction("errors").errors(errors) }
        }
    }
}