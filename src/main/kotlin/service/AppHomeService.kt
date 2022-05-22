package service

import com.slack.api.methods.MethodsClient
import com.slack.api.model.block.Blocks.*
import com.slack.api.model.block.SectionBlock.SectionBlockBuilder
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.BlockCompositions.plainText
import com.slack.api.model.block.element.BlockElements.button
import com.slack.api.model.view.View
import com.slack.api.model.view.Views
import org.kodein.di.DI
import org.kodein.di.instance


class AppHomeService(di: DI) {
    private val userService: UserService by di.instance()
    private val token: String by di.instance("SLACK_BOT_TOKEN")
    private val client: MethodsClient by di.instance("slackClient")

    private val userElements = listOf(
        button { b ->
            b.actionId("homeDigest")
                .value("homeDigest")
                .text(
                    plainText("Digest")
                )
        },
        button { b ->
            b.actionId("homeShowTimeDigest")
                .value("homeShowTimeDigest")
                .text(
                    plainText("Show Time Digest")
                )
        },
        button { b ->
            b.actionId("homeUserSettings")
                .value("homeUserSettings")
                .text(
                    plainText("User Settings")
                )
        },
    )
    private val adminElements = listOf(
        button { b ->
            b.actionId("homeAddNewChannel")
                .value("homeAddNewChannel")
                .text(
                    plainText("Add New Channel")
                )
        },
        button { b ->
            b.actionId("homeDeleteChannel")
                .value("homeDeleteChannel")
                .text(
                    plainText("Delete Channel")
                )
        },
        button { b ->
            b.actionId("homeAddTimeDigest")
                .value("homeAddTimeDigest")
                .text(
                    plainText("Add Time Digest")
                )
        },
        button { b ->
            b.actionId("homeDeleteTimeDigest")
                .value("homeDeleteTimeDigest")
                .text(
                    plainText("Delete Time Digest")
                )
        },
        button { b ->
            b.actionId("homeShowChannelsDigest")
                .value("homeShowChannelsDigest")
                .text(
                    plainText("Show Support Channels")
                )
        },
    )

    private val adminView: View = Views.view { v: View.ViewBuilder ->
        v
            .type("home")
            .blocks(
                asBlocks(
                    section { s: SectionBlockBuilder ->
                        s.text(markdownText { mt ->
                            mt.text("*Welcome home :house:*")
                        })
                    },
                    section { s: SectionBlockBuilder ->
                        s.text(markdownText { mt ->
                            mt.text("Настройки администрации")
                        })
                    },
                    actions { ac ->
                        ac.elements(
                            adminElements
                        )
                    },
                    section { s: SectionBlockBuilder ->
                        s.text(markdownText { mt ->
                            mt.text("Элементы пользователя")
                        })
                    },
                    actions { ac ->
                        ac.elements(
                            userElements
                        )
                    },

                    )
            )
    }

    private val userView: View = Views.view { v: View.ViewBuilder ->
        v
            .type("home")
            .blocks(
                asBlocks(
                    section { s: SectionBlockBuilder ->
                        s.text(markdownText { mt ->
                            mt.text("*Welcome home :house:*")
                        })
                    },
                    section { s: SectionBlockBuilder ->
                        s.text(markdownText { mt ->
                            mt.text("Элементы пользователя")
                        })
                    },
                    actions { ac ->
                        ac.elements(
                            userElements
                        )
                    },

                    )
            )
    }

    fun publishOne(userId: String) {
        client.viewsPublish { r ->
            r
                .token(token)
                .userId(userId)
                .view(if (userService.isAdmin(userId)) adminView else userView)
        }
    }

    fun publishAll() {
        userService.getUsers().forEach {
            publishOne(it.id)
        }
    }
}