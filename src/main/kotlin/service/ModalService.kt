package service

import DAYS_OF_WEEK
import DAYS_OF_WEEK_TO_STRING
import com.slack.api.model.block.Blocks.*
import com.slack.api.model.block.composition.BlockCompositions.*
import com.slack.api.model.block.element.BlockElements.*
import com.slack.api.model.view.View
import com.slack.api.model.view.Views.*
import models.ScheduleTime
import models.SupportChannel
import models.TaskPriority
import org.kodein.di.DI
import org.kodein.di.instance

class ModalService(di: DI) {
    private val userService: UserService by di.instance()
    private val userSettingsService: UserSettingsService by di.instance()

    companion object {
        val addNewChannel = view { vb ->
            vb.type("modal")
                .callbackId("addNewChannelCallbackId")
                .title(
                    viewTitle { vt ->
                        vt
                            .type("plain_text")
                            .text("Add Support Channel")
                    }
                )
                .close(viewClose { vc ->
                    vc
                        .type("plain_text")
                        .text("Close")
                })
                .blocks(asBlocks(
                    input { inp ->
                        inp.label(
                            plainText("Select channel")
                        )
                            .element(channelsSelect { cs ->
                                cs.actionId("selectChannel")
                            })
                            .blockId("selectChannelBlock")
                    },
                    input { inp ->
                        inp
                            .label(
                                plainText("Select users")
                            )
                            .element(multiUsersSelect { cs ->
                                cs.actionId("selectUsers")
                            })
                            .blockId("selectUsersBlock")
                    }
                )
                )
                .submit(viewSubmit { vs ->
                    vs
                        .type("plain_text")
                        .text("Submit")
                })
        }

        val addNewTime = view { vb ->
            vb.type("modal")
                .callbackId("addNewTimeCallbackId")
                .title(
                    viewTitle { vt ->
                        vt
                            .type("plain_text")
                            .text("Add Digest Time")
                    }
                )
                .close(viewClose { vc ->
                    vc
                        .type("plain_text")
                        .text("Close")
                })
                .blocks(asBlocks(
                    input { inp ->
                        inp.label(
                            plainText("Select day of week")
                        )
                            .element(staticSelect { cs ->
                                cs.actionId("selectDay")
                                cs.options(
                                    DAYS_OF_WEEK.map {
                                        option { op ->
                                            op.text(plainText(it.key))
                                                .value(it.value.toString())
                                        }
                                    }
                                )
                            })
                            .blockId("selectDayBlock")
                    },
                    input { inp ->
                        inp
                            .label(
                                plainText("Select time")
                            )
                            .element(timePicker { tp ->
                                tp.actionId("selectTime")
                            })
                            .blockId("selectTimeBlock")
                    }
                )
                )
                .submit(viewSubmit { vs ->
                    vs
                        .type("plain_text")
                        .text("Submit")
                })
        }

        fun getDeleteChannelModal(channels: List<SupportChannel>): View {
            return view { vb ->
                vb.type("modal")
                    .callbackId("deleteChannelCallbackId")
                    .title(
                        viewTitle { vt ->
                            vt
                                .type("plain_text")
                                .text("Delete Support Channel")
                        }
                    )
                    .close(viewClose { vc ->
                        vc
                            .type("plain_text")
                            .text("Close")
                    })
                    .blocks(asBlocks(
                        input { inp ->
                            inp.label(
                                plainText("Select channel")
                            )
                                .element(staticSelect { ss ->
                                    ss.actionId("selectChannel")
                                        .placeholder(
                                            plainText("Select your favorites")
                                        )
                                        .options(channels.map {
                                            option { op ->
                                                op.text(plainText(it.name))
                                                    .value(it.id)
                                            }
                                        }
                                        )
                                }
                                )
                                .blockId("selectChannelBlock")
                        }
                    )
                    )
                    .submit(viewSubmit { vs ->
                        vs
                            .type("plain_text")
                            .text("Submit")
                    })
            }
        }

        fun getDeleteTimeDigestModal(times: List<ScheduleTime>): View {
            return view { vb ->
                vb.type("modal")
                    .callbackId("deleteTimeDigestCallbackId")
                    .title(
                        viewTitle { vt ->
                            vt
                                .type("plain_text")
                                .text("Delete Digest Time")
                        }
                    )
                    .close(viewClose { vc ->
                        vc
                            .type("plain_text")
                            .text("Close")
                    })
                    .blocks(asBlocks(
                        input { inp ->
                            inp.label(
                                plainText("Select timestamp")
                            )
                                .element(staticSelect { ss ->
                                    ss.actionId("selectTimeDigest")
                                        .placeholder(
                                            plainText("Select your favorites")
                                        )
                                        .options(
                                            times.sortedWith(Comparator
                                                .comparing<ScheduleTime?, Int?> { it.dayOfWeek }
                                                .thenComparingInt { it.hours }
                                                .thenComparingInt { it.minutes })
                                                .map { sch ->
                                                    option { op ->
                                                        op.text(plainText("${DAYS_OF_WEEK_TO_STRING[sch.dayOfWeek]} ${sch.hours}:${if (sch.minutes < 10) "0" else ""}${sch.minutes}"))
                                                            .value(sch.dayOfWeek.toString() + " " + sch.hours + " " + sch.minutes)
                                                    }
                                                }
                                        )
                                }
                                )
                                .blockId("selectTimeDigestBlock")
                        }
                    )
                    )
                    .submit(viewSubmit { vs ->
                        vs
                            .type("plain_text")
                            .text("Submit")
                    })
            }
        }

        fun getAllTimeDigestModal(times: List<ScheduleTime>): View {
            return view { vb ->
                vb.type("modal")
                    .callbackId("showTimeDigestCallbackId")
                    .title(
                        viewTitle { vt ->
                            vt
                                .type("plain_text")
                                .text("Digest Times")
                        }
                    )
                    .close(viewClose { vc ->
                        vc
                            .type("plain_text")
                            .text("Close")
                    })
                    .blocks(asBlocks(
                        section { c ->
                            c.text(plainText(times.sortedWith(Comparator
                                .comparing<ScheduleTime?, Int?> { it.dayOfWeek }
                                .thenComparingInt { it.hours }
                                .thenComparingInt { it.minutes }).joinToString(System.lineSeparator()) { sch ->
                                "${DAYS_OF_WEEK_TO_STRING[sch.dayOfWeek]} ${sch.hours}:${if (sch.minutes < 10) "0" else ""}${sch.minutes}"
                            })
                            )
                        }
                    )
                    )
                    .submit(viewSubmit { vs ->
                        vs
                            .type("plain_text")
                            .text("Ok")
                    })
            }
        }
    }

    fun getAllChannels(channels: List<SupportChannel>): View {
        return view { vb ->
            vb.type("modal")
                .callbackId("showChannelsCallbackId")
                .title(
                    viewTitle { vt ->
                        vt
                            .type("plain_text")
                            .text("Support Channels")
                    }
                )
                .close(viewClose { vc ->
                    vc
                        .type("plain_text")
                        .text("Close")
                })
                .blocks(
                    asBlocks(
                        section { c ->
                            c.text(plainText(channels.joinToString(System.lineSeparator()) {
                                """
                Channel name: ${it.name}
                Support users: ${
                                    it.supportUserIds
                                        .joinToString(" ") { userId -> userService.getName(userId) }
                                }
            """.trimIndent()
                            }
                            ))
                        })
                )
                .submit(viewSubmit { vs ->
                    vs
                        .type("plain_text")
                        .text("Ok")
                })
        }
    }

    fun getNotificationSettings(userId: String): View {
        val userSettings = userSettingsService.getUserSettingsById(userId)
        return view { vb ->
            vb.type("modal")
                .callbackId("userSettingsCallbackId")
                .title(
                    viewTitle { vt ->
                        vt
                            .type("plain_text")
                            .text("Settings")
                    }
                )
                .close(viewClose { vc ->
                    vc
                        .type("plain_text")
                        .text("Close")
                })
                .blocks(
                    asBlocks(
                        input { r ->
                            r.blockId("selectSlackMuteBlock")
                                .label(plainText("Slack notifications"))
                                .element(
                                    staticSelect { s ->
                                        s.options(listOf(
                                            option { op -> op.text(plainText("mute")).value("mute") },
                                            option { op -> op.text(plainText("unmute")).value("unmute") }
                                            )
                                        ).initialOption(
                                            if (userSettings.isSlackDigestMuted) {
                                                option { op -> op.text(plainText("mute")).value("mute") }
                                            } else {
                                                option { op -> op.text(plainText("unmute")).value("unmute") }
                                            }
                                        ).actionId("selectSlackMute")
                                    }
                                )
                        },
                        input { r ->
                            r.blockId("selectYouTrackMuteBlock")
                                .label(plainText("YouTrack notifications"))
                                .element(
                                    staticSelect { s ->
                                        s.options(listOf(
                                            option { op -> op.text(plainText("mute")).value("mute") },
                                            option { op -> op.text(plainText("unmute")).value("unmute") }
                                        )
                                        ).initialOption(
                                            if (userSettings.isYouTrackMuted) {
                                                option { op -> op.text(plainText("mute")).value("mute") }
                                            } else {
                                                option { op -> op.text(plainText("unmute")).value("unmute") }
                                            }
                                        ).actionId("selectYouTrackMute")
                                    }
                                )
                        },
                        input { r ->
                            r.blockId("selectYouTrackPriorityBlock")
                                .label(plainText("YouTrack task priorities"))
                                .element(
                                    multiStaticSelect { s ->
                                        s.options(
                                            TaskPriority.values().map {
                                                option { op -> op.text(plainText(it.name)).value(it.name) }
                                            }
                                        ).initialOptions(
                                            userSettings.notifyPriority.map {
                                                option { op -> op.text(plainText(it.name)).value(it.name) }
                                            }
                                        ).actionId("selectYouTrackPriority")
                                    }
                                )
                        }, input { r ->
                            r.blockId("inputYouTrackProjectBlock")
                                .label(plainText("Muted YouTrack projects"))
                                .element(plainTextInput { pti ->
                                    pti.actionId("inputYouTrackProject")
                                        .placeholder(plainText("ProjectName1, ProjectName2, ..."))
                                        .initialValue(userSettings.mutedYouTrackProjects.joinToString(", "))
                                }).optional(true)
                        }
                    )
                )
                .submit(viewSubmit { vs ->
                    vs
                        .type("plain_text")
                        .text("Save")
                })
        }
    }
}