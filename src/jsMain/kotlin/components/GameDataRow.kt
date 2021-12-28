package components

import jezorko.github.gfngameslist.games.Game
import kotlinx.html.id
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import styled.*

external interface GameDataRowProps : Props {
    var id: Int
    var game: Game
}

class GameDataRow(props: GameDataRowProps) : RComponent<GameDataRowProps, State>(props) {

    override fun RBuilder.render() {
        styledTr {
            attrs {
                id = "gameDataRow${props.id}"
            }
            css {
                +GameDataRowStyles.tableRow
            }
            styledTd {
                css {
                    +GameDataRowStyles.tableData
                }
                styledImg {
                    attrs {
                        src = props.game.imageUrl
                        css {
                            +GameDataRowStyles.gameImage
                        }
                    }
                }
            }
            styledTd {
                css {
                    +GameDataRowStyles.tableData
                }
                styledDiv {
                    +props.game.title
                }
            }
            styledTd {
                css {
                    +GameDataRowStyles.tableData
                }
                styledDiv {
                    +props.game.launcher.toString()
                }
            }
        }
    }
}
