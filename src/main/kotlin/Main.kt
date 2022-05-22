import com.slack.api.bolt.App
import com.slack.api.bolt.WebEndpoint
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.model.event.ChannelDeletedEvent
import com.slack.api.model.event.MessageDeletedEvent
import com.slack.api.model.event.MessageEvent
import com.slack.api.model.event.ReactionAddedEvent
import handlers.actions.*
import handlers.actions.home.*
import handlers.actions.submit.*
import handlers.commands.*
import handlers.events.ChannelDeleteEventHandler
import handlers.events.MessageDeleteEventHandler
import handlers.events.MessageEventHandler
import handlers.events.ReactionAddedEventHandler
import handlers.youTrack.NewTaskHandler
import handlers.youTrack.NewYouTrackComment
import handlers.youTrack.NewYouTrackMentionComment
import handlers.youTrack.SLAHandler
import initializers.MyInitializer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.litote.kmongo.KMongo
import repository.*
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
        bindSingleton { UserSettingsRepository(instance("database")) }
        bindSingleton { ScheduleTimeRepository(instance("database")) }

        bindSingleton { YouTrackCommentService(di) }
        bindSingleton { UnansweredMessageService(di) }
        bindSingleton { DigestService(di) }
        bindSingleton { MessageService(di) }
        bindSingleton { UserService(di) }
        bindSingleton { SupportChannelService(di) }
        bindSingleton { SlackBlockService(di) }
        bindSingleton { UserSettingsService(di) }
        bindSingleton { AppHomeService(di) }
        bindSingleton { ModalService(di) }

        bindSingleton("slackClient") { app.client() }
    }

    app.initializer("myInitializer", MyInitializer(di))

    val everyWeekTaskService = EveryWeekTaskService(di) {
        val digestService: DigestService by di.instance()

        digestService.sendAllDigest(System.getenv("TEAM_ID"))
    }

    app.command("/hello", HelloCommandHandler(di))
    app.command("/menu", MenuHandler(di))
    app.command("/digest", DigestCommandHandler(di))
    app.command("/addsupportchannel", AddSupportChannel(di))
    app.command("/deletesupportchannel", DeleteSupportChannel(di))
    app.command("/showchannels", ShowAllChannels(di))
    app.command("/addtimenotification", AddTimeNotificationHandler(di, everyWeekTaskService))
    app.command("/showalltimes", ShowAllTimesNotification(di, everyWeekTaskService))
    app.command("/cleanschedule", CleanScheduleHandler(di, everyWeekTaskService))
    app.command("/muteyoutrack", MuteYouTrackMessages(di))

    app.event(MessageEvent::class.java, MessageEventHandler(di))
    app.event(MessageDeletedEvent::class.java, MessageDeleteEventHandler(di))
    app.event(ChannelDeletedEvent::class.java, ChannelDeleteEventHandler(di))
    app.event(ReactionAddedEvent::class.java, ReactionAddedEventHandler(di))

    app.blockAction("homeDeleteTimeDigest", HomeDeleteTimeDigestHandler(di, everyWeekTaskService))
    app.blockAction("homeDigest", HomeDigestActionHandler(di))
    app.blockAction("homeAddNewChannel", HomeAddNewChannelHandler(di))
    app.blockAction("homeDeleteChannel", HomeDeleteChannelHandler(di))
    app.blockAction("selectChannel", DefaultActionHandler())
    app.blockAction("selectTime", DefaultActionHandler())
    app.blockAction("selectTimeDigest", DefaultActionHandler())
    app.blockAction("selectDay", DefaultActionHandler())
    app.blockAction("selectUsers", DefaultActionHandler())
    app.blockAction("homeAddTimeDigest", HomeAddTimeDigestHandler(di))
    app.blockAction("homeShowTimeDigest", HomeShowTimeDigestHandler(di, everyWeekTaskService))
    app.blockAction("homeShowChannelsDigest", HomeShowChannelsHandler(di))
    app.blockAction("homeUserSettings", HomeUserSettingsHandler(di))

    app.viewSubmission("addNewChannelCallbackId", SubmitNewChannelHandler(di))
    app.viewSubmission("deleteChannelCallbackId", SubmitDeleteChannelHandler(di))
    app.viewSubmission("addNewTimeCallbackId", SubmitNewTimeHandler(di, everyWeekTaskService))
    app.viewSubmission("deleteTimeDigestCallbackId", SubmitDeleteTimeDigestHandler(di, everyWeekTaskService))
    app.viewSubmission("showChannelsCallbackId") { _, ctx -> ctx.ack() }
    app.viewSubmission("showTimeDigestCallbackId") { _, ctx -> ctx.ack() }
    app.viewSubmission("userSettingsCallbackId", SubmitUserSettingsHandler(di))

    app.endpoint(WebEndpoint.Method.POST, "/youtrack/sla", SLAHandler(di))
    app.endpoint(WebEndpoint.Method.POST, "/youtrack/newtask", NewTaskHandler(di))
    app.endpoint(WebEndpoint.Method.POST, "/youtrack/mention", NewYouTrackMentionComment(di))
    app.endpoint(WebEndpoint.Method.POST, "/youtrack/comment", NewYouTrackComment(di))

    val server = SlackAppServer(app)

    server.start()
}