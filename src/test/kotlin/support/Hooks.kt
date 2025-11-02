package support

import io.cucumber.java.After
import io.cucumber.java.AfterStep
import io.cucumber.java.Before
import io.cucumber.java.Scenario
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import java.nio.charset.StandardCharsets


/**
 * Global setup/teardown that runs before and after every scenario.
 * Uses dependency injection (Cucumber + PicoContainer) to share the same TestContext
 * instance with the step files.
 */

class Hooks(private val ctx: TestContext) {

    // Spins up fresh chrome browser with options
    // Runs once per cucumber scenario, before first step
    @Before
    fun beforeScenario() {
        ctx.driver = DriverFactory.create()
    }

    @AfterStep
    fun afterStep(scenario: Scenario) {
        if (scenario.isFailed) {
            runCatching {
                val png = (ctx.driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
                scenario.attach(png, "image/png", "Failure screenshot")   // <- under the failed step
            }
            runCatching {
                val src = ctx.driver.pageSource.toByteArray(StandardCharsets.UTF_8)
                scenario.attach(src, "text/plain", "Page source")
            }
        }
    }

    @After
    fun afterScenario(@Suppress("UNUSED_PARAMETER") scenario: Scenario) {
        runCatching { ctx.driver.quit() }   // keep teardown clean; no more attachments here
    }

}
