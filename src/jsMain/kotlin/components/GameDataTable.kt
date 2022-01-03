package components

import jezorko.github.gfngameslist.games.Game
import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.localization.get
import kotlinx.css.LinearDimension
import kotlinx.css.width
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import styled.*

external interface GameDataTableProps : Props {
    var messages: Messages?
    var games: List<Game>
}

class GameDataTable(props: GameDataTableProps) : RComponent<GameDataTableProps, State>(props) {

    override fun RBuilder.render() {
        styledTable {
            css {
                +GameDataTableStyles.element
                width = LinearDimension("100%")
            }
            styledTbody {
                css { +GameDataTableStyles.element }
                styledTr {
                    css { +GameDataTableStyles.element }
                    listOf(
                        Messages::gameImage,
                        Messages::gameTitle,
                        Messages::publisher,
                        Messages::genreLabel,
                        Messages::availableOnPlatform,
                        Messages::status
                    ).forEach {
                        styledTh {
                            css { +GameDataTableStyles.element }
                            +props.messages[it]
                        }
                    }
                }
                props.games.forEachIndexed { index, game ->
                    child(GameDataRow::class) {
                        attrs {
                            this.id = index
                            this.game = game
                            this.messages = props.messages
                        }
                    }
                }
            }
        }
    }
}
