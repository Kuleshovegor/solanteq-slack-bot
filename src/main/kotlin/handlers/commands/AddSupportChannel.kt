package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import com.slack.api.model.Conversation
import com.slack.api.model.User
import models.SupportChannel
import org.kodein.di.DI
import org.kodein.di.instance
import service.SupportChannelService
import java.nio.charset.Charset
import java.util.Scanner

class AddSupportChannel(di: DI) : SlashCommandHandler {
    private val supportChannelService: SupportChannelService by di.instance()
    private val token: String by di.instance("SLACK_BOT_TOKEN")
    private val teamId: String by di.instance("TEAM_ID")

    private fun getConversation(tag: String, context: SlashCommandContext): Conversation? {
        if (tag.isEmpty() || tag[0] != '#') {
            return null
        }

        val conversations = context.client().conversationsList { r ->
            r.teamId(teamId)
                .token(token)
                .excludeArchived(false)
        }

        if (!conversations.isOk) {
            context.logger.error(conversations.error)
            return null
        }

        return conversations.channels.find { it.name == tag.slice(1 until tag.length) }
    }

    private fun getUser(tag: String, context: SlashCommandContext): User? {
        if (tag.isEmpty() || tag[0] != '@') {
            return null
        }

        val users = context.client().usersList { r ->
            r.teamId(teamId)
                .token(token)
        }

        if (!users.isOk) {
            context.logger.error(users.error)
            return null
        }

        return users.members.find { it.name == tag.slice(1 until tag.length) }
    }

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }
        val usrResp = context.client().usersInfo { r -> r.token(token).user(req.payload.userId) }
        if (!usrResp.isOk) {
            context.logger.error(usrResp.error)
            return context.ack("что-то пошло не так")
        }

        if (usrResp.user.isBot) {
            return context.ack("no")
        }

        if (!usrResp.user.isAdmin) {
            return context.ack("Очень жаль, вы не админ")
        }

        val text = req.payload.text ?: return context.ack("некорректая комманда")
        val scanner = Scanner(text.byteInputStream(Charset.forName("UTF-8")))
        val channelTag = scanner.next()
        val conversation = getConversation(channelTag, context) ?: return context.ack("некорректый тег канала")
        if (supportChannelService.isSupportChannel(conversation.id)) {
            return context.ack("канал уже добавлен")
        }
        val users = mutableListOf<User>()

        while (scanner.hasNext()) {
            val userTag = scanner.next()
            users.add(getUser(userTag, context) ?: return context.ack("некорректый тег юзера"))
        }

        supportChannelService.addSupportChannel(
            SupportChannel(
                teamId,
                conversation.id,
                conversation.name,
                users.map { it.id }.toSet()
            )
        )

        return context.ack("чат поддержки добавлен в бота")
    }
}