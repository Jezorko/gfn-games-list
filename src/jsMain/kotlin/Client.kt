import components.MainPage
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.id
import org.w3c.dom.HTMLDivElement
import react.dom.attrs
import react.dom.render
import shared.theme
import styled.css
import styled.styledDiv

private const val MAIN_CONTAINER_ID = "main-container"
fun getMainContainer(): HTMLDivElement = document.getElementById(MAIN_CONTAINER_ID) as HTMLDivElement

fun main() {
    window.onload = {
        document.body?.style?.backgroundColor = theme.backgroundColor.toString()
        render(document.getElementById("root")) {
            styledDiv {
                css { +ClientStyles.mainContainer }
                attrs { id = MAIN_CONTAINER_ID }
                child(MainPage::class) {}
            }
        }
    }
}
