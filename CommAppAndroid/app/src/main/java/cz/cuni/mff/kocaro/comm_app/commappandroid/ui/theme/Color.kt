package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

public fun calculateCardColor(isSelected: Boolean, isCorrect: Boolean, isEvaluated: Boolean): Color {
    if (!isEvaluated) {
        return if (isSelected) Color.LightGray else Color.White
    }

    return when {
        isSelected && isCorrect -> Color(0xFFC8E6C9)
        isSelected && !isCorrect -> Color(0xFFFFCDD2)
        !isSelected && isCorrect -> Color(0xFFBBDEFB)
        else -> Color(0xFFF5F5F5)
    }
}