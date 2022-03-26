package service

import com.slack.api.methods.MethodsClient
import com.slack.api.model.User
import dsl.BotConfig
import org.kodein.di.DI
import org.kodein.di.instance

class UserService(di: DI) {
    private val slackClient: MethodsClient by di.instance("slackClient")
    private val botConfig: BotConfig by di.instance()

    fun existsById(userId: String): Boolean {
        return slackClient.usersList {r ->
            r.token(botConfig.slackBotToken)
                .teamId(botConfig.teamId) }.members.any {it.id == userId}
    }

    fun existsByName(name: String): Boolean {
        return slackClient.usersList {r ->
            r.token(botConfig.slackBotToken)
                .teamId(botConfig.teamId) }.members.any {it.name == name}
    }

    fun getUserIdByName(name: String): String {
        return slackClient.usersList { r ->
            r.token(botConfig.slackBotToken)
                .teamId(botConfig.teamId) }.members.find {it.name == name}?.name ?: throw IllegalArgumentException()
    }

    fun isAdmin(userId: String): Boolean {
        return getUserInfoById(userId).isAdmin
    }

    fun getUserInfoById(userId: String): User {
        val resp = slackClient.usersInfo { r -> r.token(botConfig.slackBotToken).user(userId) }

        check(resp.isOk)

        return resp.user
    }

    fun getUserIdByEmail(email: String): String {
        return slackClient.usersLookupByEmail {r ->
            r.email(email)
                .token(botConfig.slackBotToken)
        }.user.id
    }

    fun getUserInfoByName(name: String): User {
        val resp = slackClient.usersInfo { r -> r.token(botConfig.slackBotToken).user(getUserIdByName(name)) }

        check(resp.isOk)

        return resp.user
    }

    fun getName(userId: String): String {
        return getUserInfoById(userId).name
    }
}