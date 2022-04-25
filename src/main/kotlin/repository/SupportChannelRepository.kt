package repository

import com.mongodb.client.MongoDatabase
import models.SupportChannel
import org.litote.kmongo.contains
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

class SupportChannelRepository(database: MongoDatabase) {
    private val collection = database.getCollection<SupportChannel>("support_channels")

    fun addSupportChannel(supportChannel: SupportChannel) {
        if (collection.findOne(SupportChannel::id eq supportChannel.id) == null) {
            collection.insertOne(supportChannel)
        } else {
            collection.replaceOne(SupportChannel::id eq supportChannel.id, supportChannel)
        }
    }

    fun delete(supportChannelId: String) {
        collection.deleteOne(SupportChannel::id eq supportChannelId)
    }

    fun getSupportChannelByTeamId(teamId: String): List<SupportChannel> {
        return collection.find(SupportChannel::teamId eq teamId).toList()
    }

    fun getSupportChannelByName(name: String): SupportChannel? {
        return collection.findOne(SupportChannel::name eq name)
    }

    fun getSupportChannelById(slackId: String): SupportChannel? {
        return collection.findOne { SupportChannel::id eq slackId }
    }

    fun getSupportChannelsByUsedId(usedId: String): List<SupportChannel> {
        return collection.find(SupportChannel::supportUserIds.contains(usedId)).toList()
    }
}