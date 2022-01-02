package components


import kotlinx.css.backgroundColor
import kotlinx.css.color
import shared.theme
import styled.StyleSheet

object MultiSelectStyles : StyleSheet("MultiSelectStyles", isStatic = true) {
    val element by css {
        color = theme.fontColor
        backgroundColor = theme.backgroundColor
    }
} 
