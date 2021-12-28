package components

import api.ApiClient
import jezorko.github.gfngameslist.games.GetGamesResponse
import react.Props
import react.RBuilder
import react.RComponent
import react.State

data class MainPageState(
    val getGamesResponse: GetGamesResponse? = null
) : State

class MainPage(props: Props) : RComponent<Props, MainPageState>(props) {

    override fun componentWillMount() = updateGamesList()

    override fun RBuilder.render() {
        state.getGamesResponse?.games?.let { games ->
            child(GameDataTable::class) {
                attrs {
                    this.games = games
                }
            }
        }
    }

    private fun updateGamesList() {
        ApiClient.getGames(10, "").then {
            setState(MainPageState(it))
        }
    }

}
