package service

import com.slack.api.methods.MethodsClient
import com.slack.api.model.User
import models.NewTask
import models.TaskPriority
import models.TaskType
import org.kodein.di.DI
import org.kodein.di.instance
import org.slf4j.LoggerFactory

class UserService(di: DI) {
    private val slackClient: MethodsClient by di.instance("slackClient")
    private val userSettingsService: UserSettingsService by di.instance()
    private val token: String by di.instance("SLACK_BOT_TOKEN")
    private val teamId: String by di.instance("TEAM_ID")
    private val logger = LoggerFactory.getLogger("userService")

    fun existsById(userId: String): Boolean {
        return slackClient.usersList { r ->
            r.token(token)
                .teamId(teamId)
        }.members.any { it.id == userId }
    }

    fun getUsers(): List<User> {
        val usersListResponse = slackClient.usersList { r ->
            r.token(token)
                .teamId(teamId)
        }
        return usersListResponse.members!!
    }

    fun isYouTrackUserMuted(userId: String, project: String): Boolean {
        val userSettings = userSettingsService.getUserSettingsById(userId)
        return userSettings.isYouTrackMuted || userSettings.mutedYouTrackProjects.contains(project)
    }

    fun isYouTrackUserMuted(userId: String, newTask: NewTask): Boolean {
        val userSettings = userSettingsService.getUserSettingsById(userId)
        return userSettings.isYouTrackMuted || userSettings.mutedYouTrackProjects.contains(newTask.projectName) ||
                !userSettings.notifyPriority.contains(
                    TaskPriority.valueOf(newTask.priority!!)
                ) ||
                !userSettings.notifyType.contains(
                    TaskType.valueOf(newTask.type!!)
                )

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

    fun getUserIdByEmail(email: String): String? {
        return slackClient.usersLookupByEmail { r ->
            r.email(email)
                .token(token)
        }?.user?.id
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