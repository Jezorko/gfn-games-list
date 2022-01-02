package components


import kotlinx.css.backgroundColor
import kotlinx.css.color
import shared.theme
import styled.StyleSheet

object ToggleStyles : StyleSheet("ToggleStyles", isStatic = true) {
    val selected by css {
        color = theme.selectedToggleFontColor
        backgroundColor = theme.selectedToggleBackgroundColor
    }
    val unselected by css {
        color = theme.unselectedToggleFontColor
        backgroundColor = theme.unselectedToggleBackgroundColor
    }
} 
