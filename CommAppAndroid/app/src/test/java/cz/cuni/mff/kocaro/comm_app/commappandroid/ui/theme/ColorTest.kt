package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorTest {

    @Test
    fun `calculateCardColor returns White when not evaluated and not selected`() {
        val result = calculateCardColor(isSelected = false, isCorrect = false, isEvaluated = false)
        assertEquals(Color.White, result)
    }

    @Test
    fun `calculateCardColor returns LightGray when not evaluated but selected`() {
        val result = calculateCardColor(isSelected = true, isCorrect = true, isEvaluated = false)
        assertEquals(Color.LightGray, result)
    }

    @Test
    fun `calculateCardColor returns Green when evaluated, selected, and correct`() {
        val result = calculateCardColor(isSelected = true, isCorrect = true, isEvaluated = true)
        assertEquals(Color(0xFFC8E6C9), result)
    }

    @Test
    fun `calculateCardColor returns Red when evaluated, selected, and incorrect`() {
        val result = calculateCardColor(isSelected = true, isCorrect = false, isEvaluated = true)
        assertEquals(Color(0xFFFFCDD2), result)
    }

    @Test
    fun `calculateCardColor returns Blue when evaluated, not selected, but correct (missed)`() {
        val result = calculateCardColor(isSelected = false, isCorrect = true, isEvaluated = true)
        assertEquals(Color(0xFFBBDEFB), result)
    }
}