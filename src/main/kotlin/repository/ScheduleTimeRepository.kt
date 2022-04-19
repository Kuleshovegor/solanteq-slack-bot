package repository

import com.mongodb.client.MongoDatabase
import models.ScheduleTime
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.util.idValue

class ScheduleTimeRepository(database: MongoDatabase) {
    private val collection = database.getCollection<ScheduleTime>("schedule_time")

    fun save(scheduleTime: ScheduleTime) {
        collection.insertOne(scheduleTime)
    }

    fun getByTeamId(teamId: String): List<ScheduleTime> {
        return collection.find(ScheduleTime::teamId eq teamId).toList()
    }

    fun contains(scheduleTime: ScheduleTime): Boolean {
        return scheduleTime.idValue?.let { collection.findOneById(it) } != null
    }

    fun clean(teamId: String) {
        collection.deleteMany(ScheduleTime::teamId eq teamId)
    }
}