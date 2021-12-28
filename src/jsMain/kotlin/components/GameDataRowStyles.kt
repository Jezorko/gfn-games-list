package components


import kotlinx.css.*
import styled.StyleSheet

object GameDataRowStyles : StyleSheet("GameDataRowStyles", isStatic = true) {
    val tableRow by css {
        backgroundColor = Styles.backgroundColor
    }

    val tableData by css {
        color = Styles.textColor
    }

    val gameImage by css {
        maxWidth = LinearDimension("50%")
        maxHeight = LinearDimension("50%")
    }
} 
