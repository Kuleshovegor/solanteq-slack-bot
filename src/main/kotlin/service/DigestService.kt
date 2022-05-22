package service


import com.slack.api.methods.MethodsClient
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import org.kodein.di.DI
import org.kodein.di.instance
import repository.SupportChannelRepository
import repository.UnansweredMessageRepository
import repository.YouTrackCommentRepository

class DigestService(di: DI) {
    private val supportChannelRepository: SupportChannelRepository by di.instance()
    private val unansweredMessageRepository: UnansweredMessageRepository by di.instance()
    private val youTrackCommentRepository: YouTrackCommentRepository by di.instance()
    private val userSettingsService: UserSettingsService by di.instance()
    private val userService: UserService by di.instance()
    private val slackClient: MethodsClient by di.instance("slackClient")
    private val token: String by di.instance("SLACK_BOT_TOKEN")

    fun sendAllDigest(teamId: String) {
        val usersListResponse = slackClient.usersList { r ->
            r.token(token)
                .teamId(teamId)
        }
        val usersList = usersListResponse.members

        usersList.filter { !it.isBot && !it.isStranger && !userSettingsService.getUserSettingsById(it.id).isSlackDigestMuted }
            .forEach {
                    sendUserDigest(it.id)
            }
    }

    fun sendUserDigest(userId: String): ChatPostMessageResponse {
        val userChannels = supportChannelRepository.getSupportChannelsByUsedId(userId)
        val digest = StringBuilder()
        if (userChannels.isEmpty()) {
            digest.append("У вас нет неотвеченных сообщений в чатах поддержки.")
        } else {
            digest.append("У вас неотвеченные сообщения в чатах поддержки:")
        }
        digest.append(System.lineSeparator()).append(System.lineSeparator())
        userChannels.forEach { channel ->
            val messages = unansweredMessageRepository.getMessagesByChannelId(channel.id)
            if (messages.isNotEmpty()) {
                val links = messages.joinToString(System.lineSeparator()) { it.link }

                digest.append("В канале ${channel.name}:").append(System.lineSeparator())
                digest.append(links).append(System.lineSeparator())
            }
        }

        val user = userService.getUserInfoById(userId)
        val youTrackComments =
            if (user.profile.email != null) {
                youTrackCommentRepository.getByEmail(user.profile.email.lowercase())
            } else {
                listOf()
            }
        if (youTrackComments.isEmpty()) {
            digest.append("У вас нет неотвеченных сообщений в чатах YouTrack.")
        } else {
            digest.append("У вас неотвеченные сообщения в чатах YouTrack:")
                .append(System.lineSeparator())
                .append(System.lineSeparator())

        }
        youTrackComments.forEach {
            digest.append(it.link).append(System.lineSeparator())
        }

        slackClient.conversationsOpen { r ->
            r.token(token)
                .users(listOf(userId))
        }

        return slackClient.chatPostMessage { r ->
            r.token(token)
                .channel(userId)
                .text(digest.toString())
        }

    }

}