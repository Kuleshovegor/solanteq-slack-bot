package service

import models.ScheduleTime
import org.kodein.di.DI
import org.kodein.di.instance
import repository.ScheduleTimeRepository
import java.time.ZoneId
import java.util.*
import kotlin.concurrent.schedule

class EveryWeekTaskService(di: DI, private val task: () -> Unit) {
    companion object {
        private val DEFAULT_TIME_ZONE = TimeZone.getTimeZone(ZoneId.of("Europe/Moscow"))
        private val DEFAULT_LOCAL = Locale("ru")
        private val WEEK_PERIOD: Long

        init {
            val calendar = Calendar.getInstance(DEFAULT_TIME_ZONE, DEFAULT_LOCAL)
            calendar.time = Date(0L)
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
            WEEK_PERIOD = calendar.timeInMillis
        }
    }

    private val timer: Timer = Timer("scheduler", true)
    private val scheduleTimeRepository: ScheduleTimeRepository by di.instance()
    private val teamId: String by di.instance("TEAM_ID")

    init {
        val schedules = scheduleTimeRepository.getByTeamId(teamId)
        schedules.forEach {
            addNewTime(it.dayOfWeek, it.hours, it.minutes)
        }
    }

    fun addNewTime(dayOfWeek: Int, hour: Int, minute: Int) {
        val currentDate = Calendar.getInstance(DEFAULT_TIME_ZONE, DEFAULT_LOCAL)
        val firstDate = Calendar.getInstance(DEFAULT_TIME_ZONE, DEFAULT_LOCAL)

        currentDate.time = Date()
        firstDate.time = currentDate.time

        firstDate.add(Calendar.DAY_OF_WEEK, dayOfWeek - currentDate[Calendar.DAY_OF_WEEK])
        firstDate.add(Calendar.HOUR_OF_DAY, hour - currentDate[Calendar.HOUR_OF_DAY])
        firstDate.add(Calendar.MINUTE, minute - currentDate[Calendar.MINUTE])
        firstDate.add(Calendar.SECOND, -currentDate[Calendar.SECOND])
        firstDate.add(Calendar.MILLISECOND, -currentDate[Calendar.MILLISECOND])

        if (currentDate.time >= firstDate.time) {
            firstDate.add(Calendar.WEEK_OF_MONTH, 1)
        }

        timer.schedule(firstDate.time, WEEK_PERIOD) {
            task()
        }
    }

    fun getAllTimes(teamId: String): List<ScheduleTime> {
        return scheduleTimeRepository.getByTeamId(teamId)
    }

    fun addAndSaveNewTime(teamId: String, dayOfWeek: Int, hour: Int, minute: Int) {
        addNewTime(dayOfWeek, hour, minute)

        scheduleTimeRepository.save(ScheduleTime(teamId, dayOfWeek, hour, minute))
    }
}