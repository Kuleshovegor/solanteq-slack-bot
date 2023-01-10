package repository

import com.mongodb.client.MongoDatabase
import models.UserSettings
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

class UserSettingsRepository(database: MongoDatabase) {
    private val collection = database.getCollection<UserSettings>("usersSettings")

    fun save(userSettings: UserSettings) {
        if (collection.findOne(UserSettings::userId eq userSettings.userId) != null) {
            collection.replaceOne(UserSettings::userId eq userSettings.userId, userSettings)
        } else {
            collection.insertOne(userSettings)
        }
    }

    fun getSettingsByUserId(userId: String): UserSettings {
        return collection.findOne(UserSettings::userId eq userId) ?: UserSettings(userId)
    }

    fun changeSettings(userSettings: UserSettings) {
        collection.replaceOne(UserSettings::userId eq userSettings.userId, userSettings)
    }
}