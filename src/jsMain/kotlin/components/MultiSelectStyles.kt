package components


import kotlinx.css.*
import shared.DarkTheme
import styled.StyleSheet

object MultiSelectStyles : StyleSheet("MultiSelectStyles", isStatic = true) {
    val element by css {
        color = DarkTheme.fontColor
        backgroundColor = DarkTheme.backgroundColor
    }
} 
