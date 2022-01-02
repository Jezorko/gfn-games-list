package components

import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.input
import react.dom.label
import shared.setState
import styled.css
import styled.styledDiv

data class Option(val name: String, val value: String)

external interface MultiSelectProps : Props {
    var id: String
    var name: String
    var options: List<Option>
    var onSelection: (List<String>) -> Unit
}

external interface MultiSelectState : State {
    var selectedOptions: Set<Int>?
}

class MultiSelect(props: MultiSelectProps) : RComponent<MultiSelectProps, MultiSelectState>(props) {
    override fun RBuilder.render() {
        styledDiv {
            +"${props.name}: "
            attrs { id = props.id }
            css { MultiSelectStyles.element }
            props.options.forEachIndexed { index, option ->
                label {
                    input(InputType.checkBox) {
                        attrs {
                            onClickFunction = {
                                setState {
                                    val initialState = state.selectedOptions ?: emptySet()
                                    selectedOptions =
                                        if ((it.target as HTMLInputElement).checked) initialState + index
                                        else initialState - index
                                }.then {
                                    props.onSelection(
                                        state.selectedOptions?.map(props.options::get)?.map { it.value } ?: emptyList()
                                    )
                                }
                            }
                        }
                    }
                    +option.name
                }
            }
        }
    }
}
