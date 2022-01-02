package components


import kotlinx.css.backgroundColor
import kotlinx.css.color
import shared.theme
import styled.StyleSheet

object GameDataTableStyles : StyleSheet("GameDataTableStyles", isStatic = true) {
    val element by css {
        color = theme.fontColor
        backgroundColor = theme.backgroundColor
    }
} 
