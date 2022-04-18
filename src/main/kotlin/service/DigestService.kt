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
    private val userService: UserService by di.instance()
    private val slackClient: MethodsClient by di.instance("slackClient")
    private val token: String by di.instance("SLACK_BOT_TOKEN")

    fun sendAllDigest(teamId: String) {
        val usersListResponse = slackClient.usersList { r ->
            r.token(token)
                .teamId(teamId)
        }
        val usersList = usersListResponse.members

        usersList.forEach {
            sendUserDigest(it.id)
        }
    }

    fun sendUserDigest(userId: String): ChatPostMessageResponse {
        val userChannels = supportChannelRepository.getSupportChannelsByUsedId(userId)
        val digest = StringBuilder()
        digest.append("У вас неотвеченные сообщения в чатах поодержки.")
            .append(System.lineSeparator())
            .append(System.lineSeparator())
        userChannels.forEach { channel ->
            val messages = unansweredMessageRepository.getMessagesByChannelId(channel.id)
            val links = messages.joinToString(System.lineSeparator()) { it.link }

            digest.append("В канале ${channel.name}:").append(System.lineSeparator())
            digest.append(links).append(System.lineSeparator())
        }

        val user = userService.getUserInfoById(userId)
        val youTrackComments = youTrackCommentRepository.getByEmail(user.profile.email.lowercase())
        digest.append("У вас неотвеченные сообщения в чатах YouTrack.")
            .append(System.lineSeparator())
            .append(System.lineSeparator())

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