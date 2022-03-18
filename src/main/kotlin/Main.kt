import handlers.commands.DigestCommandHandler
import handlers.commands.HelloCommandHandler
import com.slack.api.bolt.App
import com.slack.api.bolt.WebEndpoint
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.model.event.MessageEvent
import handlers.events.MessageEventHandler
import handlers.youTrack.SLAHandler
import initializers.MyInitializer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.litote.kmongo.KMongo
import repository.SlackMessageRepository
import repository.UserRepository
import service.MessageService


fun main() {
    val di = DI {
        bindSingleton("database") {
            KMongo.createClient().getDatabase("CompassTest")
        }
        bindSingleton { BOT_CONFIG }
        bindSingleton { UserRepository(instance("database")) }
        bindSingleton { SlackMessageRepository(instance("database")) }
        bindSingleton { MessageService(instance(), instance()) }
    }

    val app = App()

    app.initializer("myInitializer", MyInitializer(di))

    app.command("/hello", HelloCommandHandler(di))
    app.command("/digest", DigestCommandHandler(di))

    app.event(MessageEvent::class.java, MessageEventHandler(di))

    app.endpoint(WebEndpoint.Method.POST, "/youtrack/sla", SLAHandler(app.client, BOT_CONFIG))

    val server = SlackAppServer(app)
    server.start()
}