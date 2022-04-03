package repository

import com.mongodb.client.MongoDatabase
import models.YouTrackComment
import models.YouTrackMention
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

class YouTrackCommentRepository(database: MongoDatabase) {
    private val collection = database.getCollection<YouTrackMention>("youTrackComments")

    fun save(youTrackMention: YouTrackMention) {
        collection.insertOne(youTrackMention)
    }

    fun getByEmail(email: String): List<YouTrackMention> {
        collection.find()
        return collection.find(YouTrackMention::userEmail eq email.lowercase()).toList()
    }

    fun delete(youTrackComment: YouTrackComment) {
        collection.deleteMany(and(YouTrackMention::issueId eq youTrackComment.issueId, YouTrackMention::userEmail eq youTrackComment.userEmail.lowercase()))
    }
}