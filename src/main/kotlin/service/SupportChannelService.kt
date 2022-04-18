package service

import models.SupportChannel
import org.kodein.di.DI
import org.kodein.di.instance
import repository.SupportChannelRepository
import repository.UnansweredMessageRepository

class SupportChannelService(di: DI) {
    private val supportChannelRepository: SupportChannelRepository by di.instance()
    private val unansweredMessageRepository: UnansweredMessageRepository by di.instance()

    fun isSupportChannel(channelId: String): Boolean {
        return supportChannelRepository.getSupportChannelById(channelId) != null
    }

    fun isSupportUser(userId: String): Boolean {
        return supportChannelRepository.getSupportChannelsByUsedId(userId).isNotEmpty()
    }

    fun addSupportChannel(supportChannel: SupportChannel) {
        supportChannelRepository.addSupportChannel(supportChannel)
    }

    fun getAllChannels(teamId: String): List<SupportChannel> {
        return supportChannelRepository.getSupportChannelByTeamId(teamId)
    }

    fun deleteSupportChannel(supportChannelId: String) {
        unansweredMessageRepository.deleteMessageByChannel(supportChannelId)
        supportChannelRepository.delete(supportChannelId)
    }

    fun isSupportUser(userId: String, channelId: String): Boolean {
        return supportChannelRepository.getSupportChannelById(channelId)?.supportUserIds?.contains(userId) ?: false
    }
}