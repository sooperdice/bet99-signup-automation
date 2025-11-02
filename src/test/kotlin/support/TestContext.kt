package support

import org.openqa.selenium.WebDriver
/**
 * Per-scenario storage bag.
 *
 *   Each Cucumber scenario gets its own copy.
 *   Holds the WebDriver + any data you want to reuse across steps.
 *   Prevents us from using static/global drivers (so tests can run in parallel later).
 */

class TestContext {
    lateinit var driver: WebDriver
    val data = mutableMapOf<String, Any>() // optional scratchpad
}
