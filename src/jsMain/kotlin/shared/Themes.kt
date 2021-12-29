package shared

import kotlinx.css.Color
import kotlinx.css.rgb

interface Theme {
    val fontColor: Color
    val backgroundColor: Color

    val tableFontColor: Color
    val tableAvailableBackgroundColorEven: Color
    val tableAvailableBackgroundColorOdd: Color
    val tableMaintenanceBackgroundColorEven: Color
    val tableMaintenanceBackgroundColorOdd: Color
    val tablePatchingBackgroundColorEven: Color
    val tablePatchingBackgroundColorOdd: Color
    val tableUnknownBackgroundColorEven: Color
    val tableUnknownBackgroundColorOdd: Color
}

object DarkTheme : Theme {
    override val fontColor: Color = rgb(180, 180, 180)
    override val backgroundColor: Color = rgb(15, 15, 15)

    override val tableFontColor: Color = fontColor
    override val tableAvailableBackgroundColorEven: Color = rgb(20, 65, 5)
    override val tableAvailableBackgroundColorOdd: Color = rgb(25, 75, 10)
    override val tableMaintenanceBackgroundColorEven: Color = rgb(100, 40, 10)
    override val tableMaintenanceBackgroundColorOdd: Color = rgb(80, 30, 10)
    override val tablePatchingBackgroundColorEven: Color = rgb(80, 65, 5)
    override val tablePatchingBackgroundColorOdd: Color = rgb(80, 75, 10)
    override val tableUnknownBackgroundColorEven: Color = rgb(40, 40, 40)
    override val tableUnknownBackgroundColorOdd: Color = rgb(50, 50, 50)
}
