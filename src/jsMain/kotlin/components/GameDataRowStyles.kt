package components


import kotlinx.css.backgroundColor
import kotlinx.css.color
import styled.StyleSheet

object GameDataRowStyles : StyleSheet("GameDataRowStyles", isStatic = true) {
    val tableRow by css {
        backgroundColor = Styles.backgroundColor
    }

    val tableData by css {
        color = Styles.textColor
    }
} 
