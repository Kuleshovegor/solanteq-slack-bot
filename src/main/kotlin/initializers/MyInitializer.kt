package initializers

import com.slack.api.bolt.App
import com.slack.api.bolt.Initializer
import dsl.BotConfig
import models.SupportChannel
import org.kodein.di.DI
import org.kodein.di.instance
import repository.SupportChannelRepository

class MyInitializer(di: DI) : Initializer {
    override fun accept(initApp: App?) {
        check(initApp != null)
    }
}