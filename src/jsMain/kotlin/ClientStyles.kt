import kotlinx.css.*
import shared.DarkTheme
import styled.StyleSheet

object ClientStyles : StyleSheet("ClientStyles", isStatic = true) {
    val mainContainer by css {
        position = Position.absolute
        left = LinearDimension("0")
        top = LinearDimension("0")
        margin = "0"
        width = LinearDimension("100%")
        color = DarkTheme.fontColor
        backgroundColor = DarkTheme.backgroundColor
    }
} 
