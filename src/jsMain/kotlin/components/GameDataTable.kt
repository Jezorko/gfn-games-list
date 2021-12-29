package components

import jezorko.github.gfngameslist.games.Game
import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.localization.get
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import styled.styledTable
import styled.styledTbody
import styled.styledTh
import styled.styledTr

external interface GameDataTableProps : Props {
    var messages: Messages?
    var games: List<Game>
}

class GameDataTable(props: GameDataTableProps) : RComponent<GameDataTableProps, State>(props) {

    override fun RBuilder.render() {
        styledTable {
            styledTbody {
                styledTr {
                    styledTh { +props.messages[Messages::gameImage] }
                    styledTh { +props.messages[Messages::gameTitle] }
                    styledTh { +props.messages[Messages::publisher] }
                    styledTh { +props.messages[Messages::availableOnPlatform] }
                    styledTh { +props.messages[Messages::status] }
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
