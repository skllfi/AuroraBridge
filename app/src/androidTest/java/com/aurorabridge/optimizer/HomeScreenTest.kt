package com.aurorabridge.optimizer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.aurorabridge.optimizer.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    // Эта строка создает правило для запуска MainActivity перед тестом
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreen_displaysOptimizerTitle() {
        // Находим на экране узел (элемент) с текстом "Optimizer"
        val title = composeTestRule.onNodeWithText("Optimizer", useUnmergedTree = true)

        // Проверяем, что этот узел отображается на экране
        title.assertIsDisplayed()
    }
}
