package support

import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.time.Duration

object DriverFactory {

    /**
     * Handles how the browser actually gets created.
     *
     *   Central place to build ChromeOptions
     *   Sets headless mode, disables prompts, etc.
     *   Returns a ready-to-use WebDriver
     */

    fun create(): WebDriver {
        val headless = Config.get("headless", "true").toBoolean()

        // instantiates Chrome’s launch options
        val opts = ChromeOptions().apply {
            // modern headless – faster and renders like real Chrome
            if (headless) {
                addArguments("--headless=new", "--window-size=1400,900")
                // CI-only flags: safe to keep only in headless
                addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage")
            } else {
                // Headed: let Chrome manage GPU/sandbox; just start maximized
                addArguments("--start-maximized")
            }

            // handle OS/browser prompts that Selenium cannot click (e.g microphone, geolocation)
            val prefs = mapOf(
                // 1 = allow, 2 = block. We block to avoid modal.
                "profile.default_content_setting_values.geolocation" to 2,
                "profile.default_content_setting_values.notifications" to 2,
                "profile.default_content_setting_values.media_stream_mic" to 2,
                "profile.default_content_setting_values.media_stream_camera" to 2
            )
            setExperimentalOption("prefs", prefs)

            // how long the WebDriver waits for a page to finish loading
            this.setPageLoadStrategy(PageLoadStrategy.NORMAL)
        }

        // creates driiver (selenium manager automatically finds the right chrome driver binary, no need to specify)
        val driver = ChromeDriver(opts)
        // We rely on explicit/fluent waits; implicit = 0 prevents weird compounded waits
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0))
        return driver
    }
}
