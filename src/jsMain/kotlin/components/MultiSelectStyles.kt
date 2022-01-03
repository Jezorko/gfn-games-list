package components


import kotlinx.css.*
import shared.theme
import styled.StyleSheet

object MultiSelectStyles : StyleSheet("MultiSelectStyles", isStatic = true) {
    val element by css {
        color = theme.fontColor
        backgroundColor = theme.backgroundColor
    }

    val container by css {
        backgroundColor = theme.overlayBackgroundColor
        position = Position.absolute
        width = LinearDimension("100%")
        height = LinearDimension("100%")
        left = LinearDimension("0")
        top = LinearDimension("0")
        zIndex = 15
    }

    val options by css {
        backgroundColor = theme.multiSelectBackground
        position = Position.absolute
        width = LinearDimension("20%")
        left = LinearDimension("40%")
    }

    val topBar by css {
        backgroundColor = theme.multiSelectTopBarBackground
        display = Display.flex
        width = LinearDimension("100%")
        justifyContent = JustifyContent.spaceAround
        marginBottom = LinearDimension("2%")
    }

    val optionsName by css {
        flexGrow = 1.0
    }

    val closeButton by css {
        color = theme.closeButtonFontColor
        backgroundColor = theme.closeButtonBackgroundColor
    }
} 
