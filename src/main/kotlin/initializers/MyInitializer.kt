package initializers

import com.slack.api.bolt.App
import com.slack.api.bolt.Initializer
import models.YouTrackMention
import org.kodein.di.DI
import org.kodein.di.instance
import service.AppHomeService
import service.UnansweredMessageService
import service.YouTrackCommentService
import java.util.*
import kotlin.concurrent.timerTask

class MyInitializer(di: DI) : Initializer {
    private val unansweredMessageService: UnansweredMessageService by di.instance()
    private val youTrackCommentService: YouTrackCommentService by di.instance()
    private val appHomeService: AppHomeService by di.instance()

    override fun accept(initApp: App?) {
        check(initApp != null)

        val timer = Timer("scheduler", true)
        timer.schedule(timerTask { appHomeService.publishAll() }, 0, 3600_000)

        unansweredMessageService.updateAllMessages()
//        youTrackCommentService.updateAll()
    }
}