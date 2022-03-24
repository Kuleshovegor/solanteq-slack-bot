package service


import com.slack.api.methods.MethodsClient
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import dsl.BotConfig
import org.kodein.di.DI
import org.kodein.di.instance
import repository.SupportChannelRepository
import repository.UnansweredMessageRepository

class DigestService(di: DI) {
    private val supportChannelRepository by di.instance<SupportChannelRepository>()
    private val unansweredMessageRepository by di.instance<UnansweredMessageRepository>()
    private val slackClient: MethodsClient by di.instance("slackClient")
    private val botConfig by di.instance<BotConfig>()

    fun sendAllDigest(teamId: String) {
        val usersListResponse = slackClient.usersList { r ->
            r.token(botConfig.slackBotToken)
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

        slackClient.conversationsOpen { r ->
            r.token(botConfig.slackBotToken)
                .users(listOf(userId))
        }

        return slackClient.chatPostMessage {r ->
            r.token(botConfig.slackBotToken)
                .channel(userId)
                .text(digest.toString())
        }

    }

}