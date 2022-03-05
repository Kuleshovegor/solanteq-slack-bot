package repository

import com.mongodb.client.MongoDatabase
import models.Message
import org.litote.kmongo.*

class SlackMessageRepository(private val database: MongoDatabase) {
    private val collection = database.getCollection<Message>("messages")

    fun getMessages(): List<Message> {
        return collection.find().toList()
    }

    fun getMessage(ts: String): Message? {
        return collection.findOne(Message::ts eq ts)
    }

    fun addMessage(message: Message) {
        collection.insertOne(message)
    }

    fun getMessagesByChannelId(channelId: String): List<Message> {
        return collection.find(Message::channel eq channelId).toList()
    }

    fun deleteMessage(ts: String) {
        collection.deleteOne(Message::ts eq ts)
    }
}