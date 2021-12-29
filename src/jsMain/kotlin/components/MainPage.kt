package components

import api.ApiClient
import jezorko.github.gfngameslist.games.GetGamesResponse
import jezorko.github.gfngameslist.games.Store
import jezorko.github.gfngameslist.games.storeFromReadableName
import jezorko.github.gfngameslist.games.validStores
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
import styled.css
import styled.styledDiv
import styled.styledInput
import styled.styledSelect
import kotlin.js.Date
import kotlin.reflect.KMutableProperty1

var allowScrollUpdate = true

external interface MainPageState : State {
    var language: Language?
    var messages: Messages?
    var limitSearch: Int?
    var searchPage: Int?
    var titleSearch: String?
    var publisherSearch: String?
    var genresSearch: String?
    var storeSearch: Store?
    var getGamesResponse: GetGamesResponse?
}

class MainPage(props: Props) : RComponent<Props, MainPageState>(props) {

    override fun componentDidMount() {
        updateGamesList()
        updateMessages()
        infiniteScroll()
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
        styledDiv {
            css { +MainPageStyles.element }
            +state.messages[Messages::endOfGamesList]
        }

        window.addEventListener("scroll", object : EventListener {
            override fun handleEvent(event: Event) = infiniteScroll()
        })
    }

    private fun infiniteScroll() {
        if (
            window.scrollY + window.innerHeight
            >= (document.body?.offsetHeight?.minus(500) ?: Int.MAX_VALUE)
            && allowScrollUpdate
        ) {
            allowScrollUpdate = false
            setState { searchPage = searchPage?.plus(1) ?: 1 }.then {
                getGames(it.searchPage!!).then { response ->
                    setState {
                        getGamesResponse = getGamesResponse?.copy(
                            games = getGamesResponse?.games?.plus(response.games) ?: response.games
                        ) ?: response
                    }.then {
                        if (response.games.isNotEmpty()) {
                            allowScrollUpdate = true
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
            setState { getGamesResponse = response }.then { allowScrollUpdate = true }
        }
    }

    private fun updateMessages() {
        ApiClient.getMessages(state.language ?: Language.ENGLISH).then { response ->
            setState { messages = response }
        }
    }

    private fun getGames(page: Int) =
        ApiClient.getGames(
            state.limitSearch ?: 10,
            page,
            state.titleSearch,
            state.storeSearch,
            state.publisherSearch,
            state.genresSearch
        )

}
