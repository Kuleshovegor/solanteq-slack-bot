import com.slack.api.bolt.App
import com.slack.api.bolt.WebEndpoint
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.model.event.ChannelDeletedEvent
import com.slack.api.model.event.MessageDeletedEvent
import com.slack.api.model.event.MessageEvent
import com.slack.api.model.event.ReactionAddedEvent
import handlers.commands.*
import handlers.events.ChannelDeleteEventHandler
import handlers.events.MessageDeleteEventHandler
import handlers.events.MessageEventHandler
import handlers.events.ReactionAddedEventHandler
import handlers.youTrack.NewYouTrackMentionComment
import handlers.youTrack.NewTaskHandler
import handlers.youTrack.NewYouTrackComment
import handlers.youTrack.SLAHandler
import initializers.MyInitializer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.litote.kmongo.KMongo
import org.slf4j.LoggerFactory
import repository.ScheduleTimeRepository
import repository.SupportChannelRepository
import repository.UnansweredMessageRepository
import repository.YouTrackCommentRepository
import service.*


fun main() {
    val app = App()

    val di = DI {
        bindSingleton("database") {
            KMongo.createClient(System.getenv("MONGODB_CONNSTRING")).getDatabase("solanteq_slack_bot")
        }
        bindSingleton("SLACK_BOT_TOKEN") { System.getenv("SLACK_BOT_TOKEN") }
        bindSingleton("TEAM_ID") { System.getenv("TEAM_ID") }
        bindSingleton { UnansweredMessageRepository(instance("database")) }
        bindSingleton { SupportChannelRepository(instance("database")) }
        bindSingleton { YouTrackCommentRepository(instance("database")) }
        bindSingleton { YouTrackCommentService(di) }
        bindSingleton { UnansweredMessageService(di) }
        bindSingleton { DigestService(di) }
        bindSingleton { MessageService(di) }
        bindSingleton { UserService(di) }
        bindSingleton { SupportChannelService(di) }
        bindSingleton { ScheduleTimeRepository(instance("database")) }
        bindSingleton("slackClient") { app.client() }
    }

    app.initializer("myInitializer", MyInitializer(di))

    val everyWeekTaskService = EveryWeekTaskService(di) {
        val digestService: DigestService by di.instance()

        digestService.sendAllDigest(System.getenv("TEAM_ID"))
    }

    app.command("/hello", HelloCommandHandler(di))
    app.command("/digest", DigestCommandHandler(di))
    app.command("/addsupportchannel", AddSupportChannel(di))
    app.command("/deletesupportchannel", DeleteSupportChannel(di))
    app.command("/showchannels", ShowAllChannels(di))
    app.command("/addtimenotification", AddTimeNotificationHandler(di, everyWeekTaskService))
    app.command("/showalltimes", ShowAllTimesNotification(di, everyWeekTaskService))
    app.command("/cleanschedule", CleanScheduleHandler(di, everyWeekTaskService))

    app.event(MessageEvent::class.java, MessageEventHandler(di))
    app.event(MessageDeletedEvent::class.java, MessageDeleteEventHandler(di))
    app.event(ChannelDeletedEvent::class.java, ChannelDeleteEventHandler(di))
    app.event(ReactionAddedEvent::class.java, ReactionAddedEventHandler(di))

    app.endpoint(WebEndpoint.Method.POST, "/youtrack/sla", SLAHandler(di))
    app.endpoint(WebEndpoint.Method.POST, "/youtrack/newtask", NewTaskHandler(di))
    app.endpoint(WebEndpoint.Method.POST, "/youtrack/mention", NewYouTrackMentionComment(di))
    app.endpoint(WebEndpoint.Method.POST, "/youtrack/comment", NewYouTrackComment(di))

    val server = SlackAppServer(app)

    server.start()
}