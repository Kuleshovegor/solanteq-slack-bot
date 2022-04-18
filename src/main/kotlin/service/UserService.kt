package service

import com.slack.api.methods.MethodsClient
import com.slack.api.model.User
import org.kodein.di.DI
import org.kodein.di.instance

class UserService(di: DI) {
    private val slackClient: MethodsClient by di.instance("slackClient")
    private val token: String by di.instance("SLACK_BOT_TOKEN")
    private val teamId: String by di.instance("TEAM_ID")

    fun existsById(userId: String): Boolean {
        return slackClient.usersList { r ->
            r.token(token)
                .teamId(teamId)
        }.members.any { it.id == userId }
    }

    fun existsByName(name: String): Boolean {
        return slackClient.usersList { r ->
            r.token(token)
                .teamId(teamId)
        }.members.any { it.name == name }
    }

    fun getUserIdByName(name: String): String {
        return slackClient.usersList { r ->
            r.token(token)
                .teamId(teamId)
        }.members.find { it.name == name }?.name ?: throw IllegalArgumentException()
    }

    fun isAdmin(userId: String): Boolean {
        return getUserInfoById(userId).isAdmin
    }

    fun getUserInfoById(userId: String): User {
        val resp = slackClient.usersInfo { r -> r.token(token).user(userId) }

        check(resp.isOk) { resp.error }

        return resp.user
    }

    fun getUserIdByEmail(email: String): String {
        return slackClient.usersLookupByEmail { r ->
            r.email(email)
                .token(token)
        }.user.id
    }

    fun getUserEmail(userId: String): String {
        return slackClient.usersInfo { r ->
            r.token(token)
                .user(userId)
        }.user.profile.email
    }

    fun getUserByEmail(email: String): User {
        return slackClient.usersLookupByEmail { r ->
            r.email(email)
                .token(token)
        }.user
    }

    fun getName(userId: String): String {
        return getUserInfoById(userId).name
    }
}