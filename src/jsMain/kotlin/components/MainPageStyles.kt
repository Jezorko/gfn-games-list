package components


import kotlinx.css.backgroundColor
import kotlinx.css.color
import shared.DarkTheme
import styled.StyleSheet

object MainPageStyles : StyleSheet("MainPageStyles", isStatic = true) {
    val element by css {
        color = DarkTheme.fontColor
        backgroundColor = DarkTheme.backgroundColor
    }
} 
