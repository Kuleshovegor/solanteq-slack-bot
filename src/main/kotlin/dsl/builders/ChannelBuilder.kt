package dsl.builders

import dsl.DescriptionDsl
import dsl.ChannelDescription
import dsl.UserDescription

@DescriptionDsl
class ChannelBuilder(private val name: String) {
    private val users = mutableSetOf<UserDescription>()

    fun users(usersBuilder: UsersBuilder.() -> Unit) {
        users.addAll(UsersBuilder().apply(usersBuilder).build())
    }

    fun build(): ChannelDescription {
        return ChannelDescription(name, users)
    }
}