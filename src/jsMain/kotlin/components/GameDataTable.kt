package components

import jezorko.github.gfngameslist.games.Game
import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.localization.get
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
            css { +GameDataTableStyles.element }
            styledTbody {
                css { +GameDataTableStyles.element }
                styledTr {
                    css { +GameDataTableStyles.element }
                    styledTh {
                        css { +GameDataTableStyles.element }
                        +props.messages[Messages::gameImage]
                    }
                    styledTh {
                        css { +GameDataTableStyles.element }
                        +props.messages[Messages::gameTitle]
                    }
                    styledTh {
                        css { +GameDataTableStyles.element }
                        +props.messages[Messages::publisher]
                    }
                    styledTh {
                        css { +GameDataTableStyles.element }
                        +props.messages[Messages::genre]
                    }
                    styledTh {
                        css { +GameDataTableStyles.element }
                        +props.messages[Messages::availableOnPlatform]
                    }
                    styledTh {
                        css { +GameDataTableStyles.element }
                        +props.messages[Messages::status]
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
