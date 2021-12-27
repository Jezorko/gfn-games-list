import components.GameDataRow
import kotlinx.browser.document
import kotlinx.browser.window
import react.dom.render

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(GameDataRow::class) {
                attrs {
                    id = 0
                    imageUrl = "https://geforcenow-games.com/media/games/251570.jpg"
                    gameTitle = "Test"
                }
            }
        }
    }
}
