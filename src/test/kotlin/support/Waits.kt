package support

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.*
import java.time.Duration
import java.util.function.Function

/**
 * Common wait helpers used across pages.
 * Keeps all sync logic in one place (no hard sleeps anywhere).
 */
object Waits {

    /**
     * Wait until element is visible and return it.
     * Handy for any interaction that needs the element in the DOM and displayed.
     */
    fun visible(driver: WebDriver, by: By, timeoutSec: Long = 10): WebElement =
        WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
            .until(ExpectedConditions.visibilityOfElementLocated(by))

    /**
     * Wait until element is clickable and return it.
     * Use this before clicking buttons or checkboxes that load async.
     */
    fun clickable(driver: WebDriver, by: By, timeoutSec: Long = 10): WebElement =
        WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
            .until(ExpectedConditions.elementToBeClickable(by))

    /**
     * Fluent wait for stuff that changes quickly (like toasts or live lists).
     * Keeps polling until the condition is met or timeout expires.
     * Ignores NoSuchElement and StaleElement errors by default.
     */
    fun <T> fluent(
        driver: WebDriver,
        timeoutSec: Long = 6,
        pollMs: Long = 200,
        f: (WebDriver) -> T
    ): T =
        FluentWait(driver)
            .withTimeout(Duration.ofSeconds(timeoutSec))
            .pollingEvery(Duration.ofMillis(pollMs))
            .ignoring(NoSuchElementException::class.java)
            .ignoring(StaleElementReferenceException::class.java)
            .until(Function<WebDriver, T> { d -> f(d) })

    /**
     * Wait until a custom condition (boolean) is true.
     * Good for checking non-element states like URL changes, data loading, etc.
     */
    fun until(driver: WebDriver, timeoutSec: Long = 10, condition: () -> Boolean) {
        WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
            .until { condition() }
    }

    /**
     * Try clicking if the element becomes visible within timeout.
     * Fails silently if it never appears (used for optional popups).
     */
    fun clickIfVisible(driver: WebDriver, by: By, timeoutSec: Long = 3) {
        try {
            visible(driver, by, timeoutSec).click()
        } catch (_: Exception) {}
    }
}
