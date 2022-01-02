package components


import kotlinx.css.*
import shared.theme
import styled.StyleSheet

object GameDataRowStyles : StyleSheet("GameDataRowStyles", isStatic = true) {
    val tableData by css { color = theme.tableFontColor }
    val tableRowAvailable by css {
        backgroundColor = theme.tableAvailableBackgroundColorOdd
        nthChild("even") { backgroundColor = theme.tableAvailableBackgroundColorEven }
    }
    val tableRowMaintenance by css {
        backgroundColor = theme.tableMaintenanceBackgroundColorOdd
        nthChild("even") { backgroundColor = theme.tableMaintenanceBackgroundColorEven }
    }
    val tableRowPatching by css {
        backgroundColor = theme.tablePatchingBackgroundColorOdd
        nthChild("even") { backgroundColor = theme.tablePatchingBackgroundColorEven }
    }
    val tableRowUnknown by css {
        backgroundColor = theme.tableUnknownBackgroundColorOdd
        nthChild("even") { backgroundColor = theme.tableUnknownBackgroundColorEven }
    }
    val gameImage by css {
        maxWidth = LinearDimension("25%")
        maxHeight = LinearDimension("25%")
    }
} 
