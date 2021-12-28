package components

import api.ApiClient
import jezorko.github.gfngameslist.games.GetGamesResponse
import jezorko.github.gfngameslist.games.Launcher
import jezorko.github.gfngameslist.games.validLaunchers
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.option
import shared.setState
import styled.styledInput
import styled.styledSelect
import kotlin.reflect.KMutableProperty1

external interface MainPageState : State {
    var limitSearch: Int?
    var titleSearch: String?
    var launcherSearch: Launcher?
    var getGamesResponse: GetGamesResponse?
}

class MainPage(props: Props) : RComponent<Props, MainPageState>(props) {

    override fun componentDidMount() = updateGamesList()

    override fun RBuilder.render() {
        styledInput(type = InputType.text) {
            attrs {
                id = "game-title-search"
                onChangeFunction = updateSearchParam(MainPageState::titleSearch)
            }
        }
        styledSelect {
            option { +"" }
            validLaunchers.forEach { validLauncher -> option { +validLauncher.name } }
            attrs {
                id = "launcher-search"
                onChangeFunction = updateSearchParam(MainPageState::launcherSearch) {
                    if (it == null || it.isEmpty()) {
                        null
                    } else {
                        Launcher.valueOf(it)
                    }
                }
            }
        }
        state.getGamesResponse?.games?.let { games ->
            child(GameDataTable::class) {
                attrs {
                    this.games = games
                }
            }
        }
    }

    private fun updateSearchParam(prop: KMutableProperty1<MainPageState, String?>) = updateSearchParam(prop) { it }
    private fun <T> updateSearchParam(prop: KMutableProperty1<MainPageState, T>, valueGetter: (String?) -> T)
            : (Event) -> Unit = { event: Event ->
        val target = event.target!!
        val newValue = valueGetter(
            when (target) {
                is HTMLInputElement -> target.value
                is HTMLSelectElement -> target.value
                else -> throw IllegalStateException("target ${target::class.simpleName} not supported")
            }
        )
        setState { prop.set(this, newValue) }.then { updateGamesList() }
    }

    private fun updateGamesList() {
        ApiClient.getGames(state.limitSearch ?: 10, state.titleSearch, state.launcherSearch)
            .then { response ->
                setState { getGamesResponse = response }
            }
    }

}
