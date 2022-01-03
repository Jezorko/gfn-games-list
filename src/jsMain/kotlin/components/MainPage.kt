package components

import api.ApiClient
import getMainContainer
import jezorko.github.gfngameslist.games.GameGenre
import jezorko.github.gfngameslist.games.GameStore
import jezorko.github.gfngameslist.games.GetGamesResponse
import jezorko.github.gfngameslist.games.validStores
import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.localization.get
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
import shared.flatThen
import shared.setState
import shared.targetValue
import styled.*
import kotlin.js.Date
import kotlin.reflect.KMutableProperty1

external interface MainPageState : State {
    var allowScrollUpdate: Boolean?
    var loadingMoreGames: Boolean?
    var language: Language?
    var messages: Messages?
    var limitSearch: Int?
    var searchPage: Int?
    var textSearch: String?
    var genresSearch: List<String>?
    var storeSearch: List<String>?
    var getGamesResponse: GetGamesResponse?
}

class MainPage(props: Props) : RComponent<Props, MainPageState>(props) {

    override fun componentDidMount() {
        updateMessages()
        setState {
            allowScrollUpdate = true
        }.then {
            updateGamesList()
            infiniteScroll()
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css { +MainPageStyles.element }
            +state.messages[
                    Messages::supportedGamesCount,
                    state.getGamesResponse?.supportedGamesCount ?: 0,
                    (Date().getTime().toLong() - (state.getGamesResponse?.lastUpdatedAt ?: 0)) / (60 * 1_000)
            ]
        }
        styledSelect {
            css { +MainPageStyles.element }
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
            css { +MainPageStyles.element }
            attrs {
                id = "game-search-bar"
                onChangeFunction = updateState(MainPageState::textSearch)
                placeholder = state.messages[Messages::searchPlaceholder]
            }
        }
        child(MultiSelect::class) {
            attrs {
                id = "game-genres-search"
                name = state.messages[Messages::genreLabel]
                options =
                    GameGenre.values().map { genre ->
                        Option(
                            name = state.messages[{ it.genres[genre] }],
                            value = genre.name
                        )
                    }.filter { it.name.isNotEmpty() }
                onSelection = updateState(MainPageState::genresSearch)
                messages = state.messages
            }
        }
        child(MultiSelect::class) {
            attrs {
                id = "game-stores-search"
                name = state.messages[Messages::storeLabel]
                options =
                    validStores.map { store ->
                        Option(
                            name = store.readableName,
                            value = store.name
                        )
                    }.filter { it.name.isNotEmpty() }
                onSelection = updateState(MainPageState::storeSearch)
                messages = state.messages
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
        if (state.loadingMoreGames == true) {
            styledImg {
                attrs {
                    src = "static/spinner.gif"
                }
            }
        } else {
            styledDiv {
                css { +MainPageStyles.element }
                +state.messages[Messages::endOfGamesList]
            }
        }

        child(Footer::class) {}

        window.addEventListener("scroll", object : EventListener {
            override fun handleEvent(event: Event) = infiniteScroll()
        })
    }

    private fun infiniteScroll() {
        val scrolledToEndOfPage = window.scrollY + window.innerHeight >= getMainContainer().offsetHeight - 1000
        if (scrolledToEndOfPage && state.allowScrollUpdate == true) {
            setState {
                allowScrollUpdate = false
                searchPage = searchPage?.plus(1) ?: 1
            }.then {
                getGames(it.searchPage!!).then { response ->
                    setState {
                        getGamesResponse = getGamesResponse?.copy(
                            games = getGamesResponse?.games?.plus(response.games) ?: response.games
                        ) ?: response
                    }.then {
                        if (response.games.isNotEmpty()) {
                            setState { allowScrollUpdate = true }.then { infiniteScroll() }
                        } else {
                            println("reached end of games list!")
                        }
                    }
                }
            }
        }
    }

    private fun <T> updateState(prop: KMutableProperty1<MainPageState, T>)
            : (T) -> Unit = { newValue: T ->
        setState { prop.set(this, newValue) }.then { updateGamesList() }
    }

    private fun updateState(prop: KMutableProperty1<MainPageState, String?>) = updateSearchParam(prop) { it }
    private fun <T> updateSearchParam(prop: KMutableProperty1<MainPageState, T>, valueGetter: (String?) -> T)
            : (Event) -> Unit = { event: Event ->
        val newValue = valueGetter(event.targetValue())
        setState { prop.set(this, newValue) }.then { updateGamesList() }
    }

    private fun updateGamesList() {
        getGames(0).then { response ->
            setState {
                getGamesResponse = response
                searchPage = 0
            }.then { setState { allowScrollUpdate = true } }
        }
    }

    private fun updateMessages() {
        ApiClient.getMessages(state.language ?: Language.ENGLISH).then { response ->
            setState { messages = response }
        }
    }

    private fun getGames(page: Int) = setState { loadingMoreGames = true }.flatThen {
        ApiClient.getGames(
            state.limitSearch ?: 20,
            page,
            state.textSearch,
            state.storeSearch?.map(GameStore::valueOf),
            state.genresSearch?.map(GameGenre::valueOf)
        ).flatThen { response -> setState { loadingMoreGames = false }.then { response } }
    }

}
