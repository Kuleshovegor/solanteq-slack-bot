package repository

import com.mongodb.client.MongoDatabase
import models.ScheduleTime
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

class ScheduleTimeRepository(database: MongoDatabase) {
    private val collection = database.getCollection<ScheduleTime>("schedule_time")

    fun save(scheduleTime: ScheduleTime) {
        collection.insertOne(scheduleTime)
    }

    fun getByTeamId(teamId: String): List<ScheduleTime> {
        return collection.find(ScheduleTime::teamId eq teamId).toList()
    }

    fun clean(teamId: String) {
        collection.deleteMany(ScheduleTime::teamId eq teamId)
    }
}