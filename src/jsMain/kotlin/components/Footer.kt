package components

import api.ApiClient
import jezorko.github.gfngameslist.versions.VersionInfo
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import shared.setState
import styled.styledA
import styled.styledFooter

external interface FooterState : State {
    var version: VersionInfo?
}

class Footer(props: Props) : RComponent<Props, FooterState>(props) {

    override fun componentDidMount() {
        ApiClient.getVersionInfo().then { setState { version = it } }
    }

    override fun RBuilder.render() {
        styledFooter {
            +"Built from "
            styledA {
                val commitUrl = state.version?.run { "$repositoryUrl/commit$commitSlug" } ?: "unknown"
                +commitUrl
                attrs {
                    href = commitUrl
                }
            }
        }
    }

}