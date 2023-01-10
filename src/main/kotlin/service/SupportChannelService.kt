package service

import com.slack.api.methods.MethodsClient
import models.SupportChannel
import org.kodein.di.DI
import org.kodein.di.instance
import repository.SupportChannelRepository
import repository.UnansweredMessageRepository

class SupportChannelService(di: DI) {
    private val supportChannelRepository: SupportChannelRepository by di.instance()
    private val unansweredMessageRepository: UnansweredMessageRepository by di.instance()
    private val client: MethodsClient by di.instance("slackClient")
    private val token: String by di.instance("SLACK_BOT_TOKEN")

    fun isSupportChannel(channelId: String): Boolean {
        return supportChannelRepository.getSupportChannelById(channelId) != null
    }

    fun isSupportUser(userId: String): Boolean {
        return supportChannelRepository.getSupportChannelsByUsedId(userId).isNotEmpty()
    }

    fun addSupportChannel(supportChannel: SupportChannel) {
        client.conversationsJoin { r ->
            r.token(token)
                .channel(supportChannel.id)
        }
        supportChannelRepository.addSupportChannel(supportChannel)
    }

    fun getAllChannels(teamId: String): List<SupportChannel> {
        return supportChannelRepository.getSupportChannelByTeamId(teamId)
    }

    fun deleteSupportChannel(supportChannelId: String): Boolean {
        unansweredMessageRepository.deleteMessageByChannel(supportChannelId)
        supportChannelRepository.delete(supportChannelId)
        return true
    }

    fun deleteSupportChannelByName(name: String): Boolean {
        val supportChannel = supportChannelRepository.getSupportChannelByName(name) ?: return false
        unansweredMessageRepository.deleteMessageByChannel(supportChannel.id)
        supportChannelRepository.delete(supportChannel.id)
        return true
    }

    fun isSupportUser(userId: String, channelId: String): Boolean {
        return supportChannelRepository.getSupportChannelById(channelId)?.supportUserIds?.contains(userId) ?: false
    }
}