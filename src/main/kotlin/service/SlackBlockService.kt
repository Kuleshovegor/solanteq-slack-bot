package service

import com.slack.api.model.block.ActionsBlock
import com.slack.api.model.block.InputBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.BlockElements
import org.kodein.di.DI

class SlackBlockService(di: DI) {
    fun getAdminMenu(): ActionsBlock {
        return ActionsBlock(BlockElements.asElements(
            BlockElements.button { r ->
                r.actionId("menuDigest")
                    .value("menu_digest")
                    .text(PlainTextObject("Digest", false))
            },
            BlockElements.button { r ->
                r.actionId("menuAddNewChannel")
                    .value("menu_add_new_channel")
                    .text(PlainTextObject("Add Support Channel", false))
            },
//            BlockElements.button { r ->
//                r.actionId("menuDeleteChannel")
//                    .value("menu_delete_channel")
//                    .text(PlainTextObject("Delete Support Channel", false))
//            },
//            BlockElements.button { r ->
//                r.actionId("menuDeleteChannel")
//                    .value("menu_delete_channel")
//                    .text(PlainTextObject("Delete Support Channel", false))
//            },
//            BlockElements.button { r ->
//                r.actionId("menuAddTimestamp")
//                    .value("menu_add_timestamp")
//                    .text(PlainTextObject("Add Time Digest", false))
//            },
//            BlockElements.button { r ->
//                r.actionId("menuDeleteTimestamp")
//                    .value("menu_delete_timestamp")
//                    .text(PlainTextObject("Delete Time Digest", false))
//            },
        ), "menu"
        )
    }

    fun getAddNewChannel(): List<LayoutBlock> {
        return listOf(
            InputBlock("selectNewChannelBlock", PlainTextObject("Выберете новый канал", false),
                BlockElements.channelsSelect { r ->
                    r.actionId("selectNewChannel")
                }, false, PlainTextObject("menuAddNewChannel", false), false
            ),
            InputBlock("selectUsersBlock", PlainTextObject("Выберете пользователей", false),
                BlockElements.multiUsersSelect { r ->
                    r.actionId("selectUsers")
                }, false, PlainTextObject("menuAddNewChannel", false), false
            ),
            ActionsBlock(
                listOf(BlockElements.button { r ->
                    r.actionId("saveNewChannel")
                        .text(PlainTextObject("save", false))
                        .value("saveNewChannel")
                }),
                "saveNewChannelButton"
            )
        )
    }
}