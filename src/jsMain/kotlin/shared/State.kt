package shared

import kotlinext.js.assign
import react.Component
import react.State
import kotlin.js.Promise

fun <S : State> Component<*, S>.setState(buildState: S.() -> Unit): Promise<S> {
    return Promise { resolve, reject ->
        try {
            var state: S? = null
            setState({ currentState ->
                assign(currentState, buildState).also { newState -> state = newState }
            }) {
                resolve(state!!)
            }
        } catch (exception: Exception) {
            reject(exception)
        }
    }
}