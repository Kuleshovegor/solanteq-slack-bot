package service

import models.UserSettings
import org.kodein.di.DI
import org.kodein.di.instance
import repository.UserSettingsRepository

class UserSettingsService(di: DI) {
    private val userSettingsRepository: UserSettingsRepository by di.instance()

    fun setUserSettings(userSettings: UserSettings) {
        userSettingsRepository.changeSettings(userSettings)
    }

    fun setMuteYouTrack(userId: String, isYouTrackMuted: Boolean) {
        val settings = userSettingsRepository.getSettingsByUserId(userId)
        settings.isYouTrackMuted = isYouTrackMuted
        userSettingsRepository.save(settings)
    }

    fun getUserSettingsById(userId: String): UserSettings {
        return userSettingsRepository.getSettingsByUserId(userId)
    }
}