package support

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.Select

/**
 * Extension functions for cleaner WebDriver element interactions.
 * Used across page classes to avoid repeating Waits.visible → clear → sendKeys.
 */
fun WebDriver.clearAndType(by: By, value: String) {
    val el = Waits.visible(this, by, 10)
    el.clear()
    el.sendKeys(value)
}

fun WebDriver.selectByText(by: By, visibleText: String) {
    val el = Waits.visible(this, by, 10)
    Select(el).selectByVisibleText(visibleText)
}

fun WebDriver.type(by: By, value: String) {
    val el = Waits.visible(this, by, 8)
    el.clear()
    el.sendKeys(value)
}

fun WebDriver.check(by: By, desired: Boolean) {
    val el = Waits.visible(this, by, 5)
    val isChecked = el.getAttribute("checked") == "true" || el.isSelected
    if (desired != isChecked) el.click()
}


