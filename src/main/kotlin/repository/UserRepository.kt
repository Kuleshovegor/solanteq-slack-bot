package repository

import com.mongodb.client.MongoDatabase
import models.UserChannels
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

class UserRepository(database: MongoDatabase) {
    private val collection = database.getCollection<UserChannels>("users_channels")

    fun getChannelsByUserId(id: String): UserChannels? {
        return collection.findOne{UserChannels::id eq id}
    }

    fun addUser(userChannels: UserChannels) {
        collection.insertOne(userChannels)
    }
}