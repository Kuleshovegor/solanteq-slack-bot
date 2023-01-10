package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.UserSettingsService

class MuteYouTrackMessages(di: DI): SlashCommandHandler {
    private val userSettingsService: UserSettingsService by di.instance()

    override fun apply(req: SlashCommandRequest, context: SlashCommandContext): Response {
        val newMute = !userSettingsService.getUserSettingsById(req.payload.userId).isYouTrackMuted
        userSettingsService.setMuteYouTrack(req.payload.userId, newMute)

        return context.ack("Notification from YouTrack ${if (newMute) "mute" else "unmute"}.")
    }
}