package service

import models.SupportChannel
import org.kodein.di.DI
import org.kodein.di.instance
import repository.SupportChannelRepository

class SupportChannelService(di: DI) {
    private val supportChannelRepository: SupportChannelRepository by di.instance()

    fun isSupportChannel(channelId: String): Boolean {
        return supportChannelRepository.getSupportChannelById(channelId) != null
    }

    fun isSupportUser(userId: String): Boolean {
        return supportChannelRepository.getSupportChannelsByUsedId(userId).isNotEmpty()
    }

    fun addSupportChannel(supportChannel: SupportChannel) {
        supportChannelRepository.addSupportChannel(supportChannel)
    }

    fun deleteSupportChannel(supportChannelId: String) {
        supportChannelRepository.delete(supportChannelId)
    }

    fun isSupportUser(userId: String, channelId: String): Boolean {
        return supportChannelRepository.getSupportChannelById(channelId)?.supportUserIds?.contains(userId) ?: false
    }
}