package components

import api.ApiClient
import getMainContainer
import jezorko.github.gfngameslist.games.*
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
    var titleSearch: String?
    var publisherSearch: String?
    var genresSearch: String?
    var storeSearch: GameStore?
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
                id = "game-title-search"
                onChangeFunction = updateState(MainPageState::titleSearch)
                placeholder = state.messages[Messages::searchByTitlePlaceholder]
            }
        }
        styledInput(type = InputType.text) {
            css { +MainPageStyles.element }
            attrs {
                id = "game-publisher-search"
                onChangeFunction = updateState(MainPageState::publisherSearch)
                placeholder = state.messages[Messages::searchByPublisherPlaceholder]
            }
        }
        child(MultiSelect::class) {
            attrs {
                id = "game-genres-search"
                name = state.messages[Messages::searchByGenresPlaceholder]
                options =
                    GameGenre.values().map { genre ->
                        Option(
                            name = state.messages[{ it.genres[genre] }],
                            value = genre.name
                        )
                    }.filter { it.name.isNotEmpty() }
                onSelection = { println("current selection is $it") }
            }
        }
        styledInput(type = InputType.text) {
            css { +MainPageStyles.element }
            attrs {
                id = "game-genres-search"
                onChangeFunction = updateState(MainPageState::genresSearch)
                placeholder = state.messages[Messages::searchByGenresPlaceholder]
            }
        }
        styledSelect {
            css { +MainPageStyles.element }
            option { +"" }
            validStores.forEach { validStore -> option { +validStore.readableName } }
            attrs {
                id = "store-search"
                onChangeFunction = updateSearchParam(MainPageState::storeSearch) {
                    if (it == null || it.isEmpty()) {
                        null
                    } else {
                        storeFromReadableName(it)
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
            state.titleSearch,
            state.storeSearch,
            state.publisherSearch,
            state.genresSearch
        ).flatThen { response -> setState { loadingMoreGames = false }.then { response } }
    }

}
