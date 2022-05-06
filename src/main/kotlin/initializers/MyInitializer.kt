package initializers

import com.slack.api.bolt.App
import com.slack.api.bolt.Initializer
import org.kodein.di.DI
import org.kodein.di.instance
import service.UnansweredMessageService

class MyInitializer(di: DI) : Initializer {
    private val unansweredMessageService: UnansweredMessageService by di.instance()

    override fun accept(initApp: App?) {
        check(initApp != null)

        unansweredMessageService.updateAllMessages()
    }
}