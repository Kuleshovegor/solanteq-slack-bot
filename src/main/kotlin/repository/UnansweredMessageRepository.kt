package repository

import com.mongodb.client.MongoDatabase
import models.UnansweredMessage
import org.litote.kmongo.*

class UnansweredMessageRepository(database: MongoDatabase) {
    private val collection = database.getCollection<UnansweredMessage>("messages")

    fun getMessages(): List<UnansweredMessage> {
        return collection.find().toList()
    }

    fun getMessage(ts: String): UnansweredMessage? {
        return collection.findOne(UnansweredMessage::ts eq ts)
    }

    fun addMessage(unansweredMessage: UnansweredMessage) {
        collection.insertOne(unansweredMessage)
    }

    fun getMessagesByChannelId(channelId: String): List<UnansweredMessage> {
        return collection.find(UnansweredMessage::channelId eq channelId).toList()
    }

    fun deleteMessage(ts: String) {
        collection.deleteOne(UnansweredMessage::ts eq ts)
    }

    fun deleteMessageByChannel(ts: String) {
        collection.deleteMany(UnansweredMessage::channelId eq ts)
    }
}