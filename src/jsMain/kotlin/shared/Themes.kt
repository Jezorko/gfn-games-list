package shared

import kotlinx.css.Color
import kotlinx.css.rgb
import kotlinx.css.rgba

interface Theme {
    val fontColor: Color
    val backgroundColor: Color

    val selectedToggleFontColor: Color
    val selectedToggleBackgroundColor: Color
    val unselectedToggleFontColor: Color
    val unselectedToggleBackgroundColor: Color

    val tableFontColor: Color
    val tableAvailableBackgroundColorEven: Color
    val tableAvailableBackgroundColorOdd: Color
    val tableMaintenanceBackgroundColorEven: Color
    val tableMaintenanceBackgroundColorOdd: Color
    val tablePatchingBackgroundColorEven: Color
    val tablePatchingBackgroundColorOdd: Color
    val tableUnknownBackgroundColorEven: Color
    val tableUnknownBackgroundColorOdd: Color

    val overlayBackgroundColor: Color
    val multiSelectBackground: Color
    val multiSelectTopBarBackground: Color

    val closeButtonFontColor: Color
    val closeButtonBackgroundColor: Color
}

object DarkTheme : Theme {
    override val fontColor = rgb(180, 180, 180)
    override val backgroundColor = rgb(15, 15, 15)

    override val selectedToggleFontColor = rgb(0, 51, 0)
    override val selectedToggleBackgroundColor = rgb(51, 153, 51)
    override val unselectedToggleFontColor = fontColor
    override val unselectedToggleBackgroundColor = rgb(51, 51, 51)

    override val tableFontColor = fontColor
    override val tableAvailableBackgroundColorEven = rgb(20, 65, 5)
    override val tableAvailableBackgroundColorOdd = rgb(25, 75, 10)
    override val tableMaintenanceBackgroundColorEven = rgb(100, 40, 10)
    override val tableMaintenanceBackgroundColorOdd = rgb(80, 30, 10)
    override val tablePatchingBackgroundColorEven = rgb(80, 65, 5)
    override val tablePatchingBackgroundColorOdd = rgb(80, 75, 10)
    override val tableUnknownBackgroundColorEven = rgb(40, 40, 40)
    override val tableUnknownBackgroundColorOdd = rgb(50, 50, 50)

    override val overlayBackgroundColor = rgba(0, 0, 0, 0.5)
    override val multiSelectBackground = rgb(17, 17, 17)
    override val multiSelectTopBarBackground = rgb(25, 25, 25)

    override val closeButtonFontColor = fontColor
    override val closeButtonBackgroundColor = rgb(100, 0, 0)
}

val theme: Theme get() = DarkTheme