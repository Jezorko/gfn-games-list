package components

import jezorko.github.gfngameslist.games.Game
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
    var game: Game
}

class GameDataRow(props: GameDataRowProps) : RComponent<GameDataRowProps, State>(props) {

    override fun RBuilder.render() {
        styledTr {
            attrs { id = "gameDataRow${props.id}" }
            css { +GameDataRowStyles.tableRow }
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
                styledDiv { +props.game.store.toString() }
            }
            styledTd {
                css { +GameDataRowStyles.tableData }
                styledDiv { +props.game.publisher }
            }
        }
    }
}
