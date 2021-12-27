import api.ApiClient
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import styled.css
import styled.styledDiv
import styled.styledInput

external interface WelcomeProps : Props {
    var name: String
}

data class WelcomeState(val name: String) : State

class Welcome(props: WelcomeProps) : RComponent<WelcomeProps, WelcomeState>(props) {

    init {
        state = WelcomeState(props.name)
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                +WelcomeStyles.textContainer
            }
            +"Hello, ${state.name}"
        }
        styledInput {
            css {
                +WelcomeStyles.textInput
            }
            attrs {
                type = InputType.text
                value = state.name
                onChangeFunction = { event ->
                    ApiClient().getMessages("pl-PL").then {
                        setState(
                            WelcomeState(name = it!!.supportedGamesCount)
                        )
                    }
                }
            }
        }
    }
}
