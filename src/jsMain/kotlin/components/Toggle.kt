package components

import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import shared.setState
import styled.css
import styled.styledButton

external interface ToggleProps : Props {
    var id: String?
    var text: String?
    var onClickFunction: ((Boolean) -> Unit)?
    var initiallySelected: Boolean?
}

external interface ToggleState : State {
    var isSelected: Boolean?
}

class Toggle(props: ToggleProps) : RComponent<ToggleProps, ToggleState>(props) {
    override fun RBuilder.render() {
        styledButton {
            +(props.text ?: "toggle needs a text!")
            css {
                +if (currentlySelected) ToggleStyles.selected else ToggleStyles.unselected
            }
            attrs {
                props.id?.let { id = it }
                onClickFunction = {
                    val isNowSelected = state.isSelected != true
                    setState { isSelected = isNowSelected }.then { props.onClickFunction?.invoke(isNowSelected) }
                }
            }
        }
    }

    private val currentlySelected: Boolean get() {
        val result = state.isSelected ?: (props.initiallySelected == true)
        println("checking selection for ${props.text} = $result")
        return result
    }
}