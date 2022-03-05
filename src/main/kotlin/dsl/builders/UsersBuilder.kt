package dsl.builders

import dsl.DescriptionDsl
import dsl.UserDescription

@DescriptionDsl
class UsersBuilder {
    private val users = mutableListOf<UserDescription>()

    fun user(name: String, email: String = "") {
        users.add(UserDescription(name, email))
    }

    fun build(): List<UserDescription> {
        return users
    }
}