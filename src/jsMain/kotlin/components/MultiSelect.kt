package components

import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.localization.get
import kotlinx.browser.window
import kotlinx.css.Display
import kotlinx.css.LinearDimension
import kotlinx.css.display
import kotlinx.css.top
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.div
import react.dom.strong
import shared.setState
import styled.css
import styled.styledButton
import styled.styledDiv

data class Option(val name: String, val value: String)

external interface MultiSelectProps : Props {
    var id: String?
    var name: String
    var options: List<Option>
    var onSelection: ((List<String>) -> Unit)?
    var messages: Messages?
}

external interface MultiSelectState : State {
    var optionsDisplayed: Boolean?
    var selectedOptions: Set<Int>?
}

class MultiSelect(props: MultiSelectProps) : RComponent<MultiSelectProps, MultiSelectState>(props) {
    override fun RBuilder.render() {
        styledDiv {
            css { +MultiSelectStyles.element }
            attrs { props.id?.let { id = it } }

            styledButton {
                +"${props.name}${getButtonSelectCountNamePart()}"
                css { +MultiSelectStyles.element }
                attrs {
                    props.id?.let { id = "$it-choose-button" }
                    onClickFunction = {
                        setState { optionsDisplayed = true }
                    }
                }
            }

            styledDiv {
                css {
                    +MultiSelectStyles.container
                    display = if (state.optionsDisplayed == true) Display.inline else Display.none
                }
                attrs { props.id?.let { id = "$it-container" } }

                styledDiv {
                    css {
                        +MultiSelectStyles.options
                        top = LinearDimension("${window.innerHeight / 3}px")
                    }
                    attrs { props.id?.let { id = "$it-options" } }

                    styledDiv {
                        css { +MultiSelectStyles.topBar }
                        attrs { props.id?.let { id = "$it-top-bar" } }

                        styledDiv {
                            css { +MultiSelectStyles.optionsName }
                            attrs { props.id?.let { id = "$it-options-name" } }
                            strong { +props.name }
                        }

                        if ((state.selectedOptions?.size ?: 0) > 0) {
                            styledButton {
                                +"reset"
                                css { +MultiSelectStyles.element }
                                attrs {
                                    props.id?.let { id = "$it-reset-button" }
                                    onClickFunction = { updateSelection(emptySet()) }
                                }
                            }
                        }

                        styledButton {
                            +"X"
                            css { +MultiSelectStyles.closeButton }
                            attrs {
                                props.id?.let { id = "$it-close-options-button" }
                                onClickFunction = { setState { optionsDisplayed = false } }
                            }
                        }
                    }

                    div {
                        attrs { props.id?.let { id = "$it-toggles" } }
                        props.options.forEachIndexed { index, option ->
                            child(Toggle::class) {
                                attrs {
                                    props.id?.let { id = "$it-option-${option.value}" }
                                    text = option.name
                                    onClickFunction = { checked ->
                                        val initialState = state.selectedOptions ?: emptySet()
                                        updateSelection(
                                            if (checked) initialState + index else initialState - index
                                        )
                                    }
                                    initiallySelected = state.selectedOptions?.contains(index) == true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateSelection(newValue: Set<Int>) {
        setState { selectedOptions = newValue }.then {
            props.onSelection?.invoke(
                state.selectedOptions?.map { props.options[it].value } ?: emptyList()
            )
        }
    }

    private fun getButtonSelectCountNamePart(): String {
        val selectedCount = state.selectedOptions?.size ?: 0
        return if (selectedCount == 0) {
            ""
        } else if (selectedCount < props.options.size) {
            " (${props.messages[Messages::numberSelected, selectedCount]})"
        } else {
            " (${props.messages[Messages::allSelected]})"
        }
    }
}
