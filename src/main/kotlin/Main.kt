import com.slack.api.bolt.App
import com.slack.api.bolt.WebEndpoint
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.model.event.MessageEvent
import handlers.commands.*
import handlers.events.MessageEventHandler
import handlers.youTrack.SLAHandler
import initializers.MyInitializer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.litote.kmongo.KMongo
import repository.ScheduleTimeRepository
import repository.SupportChannelRepository
import repository.UnansweredMessageRepository
import service.DigestService
import service.EveryWeekTaskService
import service.SupportChannelService
import service.UnansweredMessageService


fun main() {
    val app = App()

    val di = DI {
        bindSingleton("database") {
            KMongo.createClient().getDatabase("solanteq_slack_bot")
        }
        bindSingleton { BOT_CONFIG }
        bindSingleton { UnansweredMessageRepository(instance("database")) }
        bindSingleton { SupportChannelRepository(instance("database")) }
        bindSingleton { UnansweredMessageService(instance()) }
        bindSingleton { DigestService(di) }
        bindSingleton { SupportChannelService(di) }
        bindSingleton { ScheduleTimeRepository(instance("database")) }
        bindSingleton("slackClient") { app.client() }
    }

    app.initializer("myInitializer", MyInitializer(di))

    val everyWeekTaskService = EveryWeekTaskService(di) {
        val digestService: DigestService by di.instance()

        digestService.sendAllDigest(BOT_CONFIG.teamId)
    }

    app.command("/hello", HelloCommandHandler(di))
    app.command("/digest", DigestCommandHandler(di))
    app.command("/addsupportchannel", AddSupportChannel(di))
    app.command("/deletesupportchannel", DeleteSupportChannel(di))
    app.command("/showchannels", ShowAllChannels(di))
    app.command("/addtimenotification", AddTimeNotificationHandler(di, everyWeekTaskService))
    app.command("/showalltimes", ShowAllTimesNotification(di, everyWeekTaskService))

    app.event(MessageEvent::class.java, MessageEventHandler(di))

    app.endpoint(WebEndpoint.Method.POST, "/youtrack/sla", SLAHandler(app.client, BOT_CONFIG))

    val server = SlackAppServer(app)

    server.start()
}