package repository

import com.mongodb.client.MongoDatabase
import models.ScheduleTime
import org.litote.kmongo.*
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
        return collection.findOne(and(
            ScheduleTime::teamId eq scheduleTime.teamId,
            ScheduleTime::minutes eq scheduleTime.minutes,
            ScheduleTime::hours eq scheduleTime.hours
        )) != null
    }

    fun delete(scheduleTime: ScheduleTime) {
        collection.deleteOne(and(
            ScheduleTime::teamId eq scheduleTime.teamId,
            ScheduleTime::minutes eq scheduleTime.minutes,
            ScheduleTime::hours eq scheduleTime.hours
        ))
    }

    fun clean(teamId: String) {
        collection.deleteMany(ScheduleTime::teamId eq teamId)
    }
}