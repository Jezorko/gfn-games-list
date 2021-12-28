package components


import kotlinx.css.*
import styled.StyleSheet

object GameDataRowStyles : StyleSheet("GameDataRowStyles", isStatic = true) {
    val tableData by css { color = DarkTheme.tableFontColor }
    val tableRowAvailable by css {
        backgroundColor = DarkTheme.tableAvailableBackgroundColorOdd
        nthChild("2") { backgroundColor = DarkTheme.tableAvailableBackgroundColorEven }
    }
    val tableRowMaintenance by css {
        backgroundColor = DarkTheme.tableMaintenanceBackgroundColorOdd
        nthChild("2") { backgroundColor = DarkTheme.tableMaintenanceBackgroundColorEven }
    }
    val tableRowPatching by css {
        backgroundColor = DarkTheme.tablePatchingBackgroundColorOdd
        nthChild("2") { backgroundColor = DarkTheme.tablePatchingBackgroundColorEven }
    }
    val tableRowUnknown by css {
        backgroundColor = DarkTheme.tableUnknownBackgroundColorOdd
        nthChild("2") { backgroundColor = DarkTheme.tableUnknownBackgroundColorEven }
    }
    val gameImage by css {
        maxWidth = LinearDimension("50%")
        maxHeight = LinearDimension("50%")
    }
} 
