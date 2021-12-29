import components.MainPage
import kotlinx.browser.document
import kotlinx.browser.window
import react.dom.render
import styled.css
import styled.styledDiv

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            styledDiv {
                css { +ClientStyles.mainContainer }
                child(MainPage::class) {}
            }
        }
    }
}
