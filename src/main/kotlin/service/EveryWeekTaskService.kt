package service

import java.time.ZoneId
import java.util.*
import kotlin.concurrent.schedule

class EveryWeekTaskService(private val task: () -> Unit) {
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

    private val timer: Timer = Timer("scheduler",false)

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
}