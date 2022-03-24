package repository

import com.mongodb.client.MongoDatabase
import models.BrokenSLA
import org.litote.kmongo.getCollection

class BrokenSLARepository(database: MongoDatabase) {
    private val collection = database.getCollection<BrokenSLA>("broken_sla")

    fun save(brokenSLA: BrokenSLA) {

    }

    fun delete(youTrackId: String) {

    }
}