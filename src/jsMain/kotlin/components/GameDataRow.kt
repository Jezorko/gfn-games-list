package components

import jezorko.github.gfngameslist.games.Game
import jezorko.github.gfngameslist.games.GameStatus
import jezorko.github.gfngameslist.localization.GameStatusMessages
import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.localization.get
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLImageElement
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import styled.*

external interface GameDataRowProps : Props {
    var id: Int
    var messages: Messages?
    var game: Game
}

class GameDataRow(props: GameDataRowProps) : RComponent<GameDataRowProps, State>(props) {

    override fun RBuilder.render() {
        styledTr {
            attrs { id = "gameDataRow${props.id}" }
            css {
                +when (props.game.status) {
                    GameStatus.AVAILABLE -> GameDataRowStyles.tableRowAvailable
                    GameStatus.MAINTENANCE -> GameDataRowStyles.tableRowMaintenance
                    GameStatus.PATCHING -> GameDataRowStyles.tableRowPatching
                    else -> GameDataRowStyles.tableRowUnknown
                }
            }
            styledTd {
                css { +GameDataRowStyles.tableData }
                styledImg {
                    attrs {
                        src = props.game.imageUrl
                        css { +GameDataRowStyles.gameImage }
                        var isEnlarged = false
                        onClickFunction = {
                            val imageElement = (it.target as HTMLImageElement)
                            imageElement.style.position = "sticky"
                            imageElement.style.transition = "transform 0.25s ease"
                            isEnlarged = !isEnlarged
                            if (!isEnlarged) {
                                imageElement.style.zIndex = "0"
                                imageElement.style.transform = "scale(1)"
                            } else {
                                imageElement.style.zIndex = "999"
                                imageElement.style.transform = "scale(2) translate(50%,0%)"
                            }
                        }
                    }
                }
            }
            styledTd {
                css { +GameDataRowStyles.tableData }
                styledDiv { +props.game.title }
            }
            styledTd {
                css { +GameDataRowStyles.tableData }
                styledDiv { +props.game.publisher }
            }
            styledTd {
                css { +GameDataRowStyles.tableData }
                styledDiv { +props.game.genres.joinToString(", ") }
            }

            styledTd {
                css { +GameDataRowStyles.tableData }
                props.game.storeUrl.let {
                    if (it.isNotBlank()) {
                        styledA {
                            +props.game.store.readableName
                            attrs {
                                href = props.game.storeUrl
                                target = "_blank"
                            }
                        }
                    } else {
                        styledDiv {
                            +props.game.store.readableName
                        }
                    }
                }
            }

            styledTd {
                css { +GameDataRowStyles.tableData }
                styledDiv {
                    +when (props.game.status) {
                        GameStatus.AVAILABLE -> props.messages[Messages::specificStatus, GameStatusMessages::available]
                        GameStatus.MAINTENANCE -> props.messages[Messages::specificStatus, GameStatusMessages::maintenance]
                        GameStatus.PATCHING -> props.messages[Messages::specificStatus, GameStatusMessages::patching]
                        GameStatus.UNKNOWN -> props.messages[Messages::specificStatus, GameStatusMessages::unknown]
                    }
                }
            }
        }
    }
}
