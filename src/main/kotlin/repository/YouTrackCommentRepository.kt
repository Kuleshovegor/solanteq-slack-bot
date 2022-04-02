package repository

import com.mongodb.client.MongoDatabase
import models.YouTrackComment
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

class YouTrackCommentRepository(database: MongoDatabase) {
    private val collection = database.getCollection<YouTrackComment>("youTrackComments")

    fun save(youTrackComment: YouTrackComment) {
        collection.insertOne(youTrackComment)
    }

    fun getByEmail(email: String): List<YouTrackComment> {
        collection.find()
        return collection.find(YouTrackComment::userEmail eq email).toList()
    }
}