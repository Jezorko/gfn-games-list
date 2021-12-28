package components

import jezorko.github.gfngameslist.games.Game
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import styled.styledTable
import styled.styledTbody
import styled.styledTh
import styled.styledTr

external interface GameDataTableProps : Props {
    var games: List<Game>
}

class GameDataTable(props: GameDataTableProps) : RComponent<GameDataTableProps, State>(props) {

    override fun RBuilder.render() {
        styledTable {
            styledTbody {
                styledTr {
                    styledTh { +"image" }
                    styledTh { +"title" }
                    styledTh { +"available on" }
                }
                props.games.forEachIndexed { index, game ->
                    child(GameDataRow::class) {
                        attrs {
                            this.id = index
                            this.game = game
                        }
                    }
                }
            }
        }
    }
}
