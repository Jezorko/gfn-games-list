package components

import api.ApiClient
import jezorko.github.gfngameslist.games.GetGamesResponse
import jezorko.github.gfngameslist.games.Launcher
import jezorko.github.gfngameslist.games.validLaunchers
import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.localization.get
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import localization.Language
import localization.languageFromReadableName
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.option
import shared.setState
import shared.targetValue
import styled.styledDiv
import styled.styledInput
import styled.styledSelect
import kotlin.reflect.KMutableProperty1

var updatingOnScroll = false

external interface MainPageState : State {
    var language: Language?
    var messages: Messages?
    var limitSearch: Int?
    var searchPage: Int?
    var titleSearch: String?
    var launcherSearch: Launcher?
    var getGamesResponse: GetGamesResponse?
}

class MainPage(props: Props) : RComponent<Props, MainPageState>(props) {

    override fun componentDidMount() {
        updateGamesList()
        updateMessages()
    }

    override fun RBuilder.render() {
        styledDiv {
            +state.messages[Messages::supportedGamesCount, state.getGamesResponse?.supportedGamesCount ?: 0]
        }
        styledSelect {
            +Language.ENGLISH.readableName
            Language.values().forEach { language -> option { +language.readableName } }
            attrs {
                id = "language-select"
                onChangeFunction = {
                    setState { language = languageFromReadableName(it.targetValue()) }.then { updateMessages() }
                }
            }
        }
        styledInput(type = InputType.text) {
            attrs {
                id = "game-title-search"
                onChangeFunction = updateState(MainPageState::titleSearch)
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
                    this.messages = state.messages
                }
            }
        }
        window.addEventListener("scroll", object : EventListener {
            override fun handleEvent(event: Event) {
                if (
                    window.scrollY + window.innerHeight
                    >= (document.body?.offsetHeight?.minus(500) ?: Int.MAX_VALUE)
                    && !updatingOnScroll
                ) {
                    updatingOnScroll = true
                    setState { searchPage = searchPage?.plus(1) ?: 1 }.then {
                        ApiClient.getGames(
                            state.limitSearch ?: 10,
                            it.searchPage!!,
                            state.titleSearch,
                            state.launcherSearch
                        ).then { response ->
                            setState {
                                getGamesResponse = getGamesResponse?.copy(
                                    games = getGamesResponse?.games?.plus(response.games) ?: response.games
                                ) ?: response
                            }.then {
                                updatingOnScroll = false
                            }
                        }
                    }
                }
            }
        })
    }

    private fun updateState(prop: KMutableProperty1<MainPageState, String?>) = updateSearchParam(prop) { it }
    private fun <T> updateSearchParam(prop: KMutableProperty1<MainPageState, T>, valueGetter: (String?) -> T)
            : (Event) -> Unit = { event: Event ->
        val newValue = valueGetter(event.targetValue())
        setState { prop.set(this, newValue) }.then { updateGamesList() }
    }

    private fun updateGamesList() {
        ApiClient.getGames(state.limitSearch ?: 10, 0, state.titleSearch, state.launcherSearch)
            .then { response ->
                setState { getGamesResponse = response }
            }
    }

    private fun updateMessages() {
        ApiClient.getMessages(state.language ?: Language.ENGLISH).then { response ->
            setState { messages = response }
        }
    }

}
