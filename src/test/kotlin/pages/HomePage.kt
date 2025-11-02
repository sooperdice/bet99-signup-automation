package pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import support.Waits

/**
 * Represents the BET99 home page.
 * Responsible only for user interactions before the registration modal opens,
 * such as dismissing jurisdiction popups and opening the Join modal.
 *
 * Keeps logic lightweight and reusable. No sleeps, no test logic, no reporting.
 */
class HomePage(private val driver: WebDriver) {
    private val log = LoggerFactory.getLogger(javaClass)

    // --- Locators ---
    private val joinBtn = By.cssSelector("[data-cy='registerBtn']")
    // Jurisdiction popup (appears for Ontario users)
    //private val stayOnThisPageBtn = By.xpath("//button[normalize-space()='Stay on this site']")
    private val stayOnThisPageBtn = By.cssSelector("[data-cy='wronglocationmodal-3f9c8c48']")
    // A simple guard element to confirm the Join modal (Step 1) is visible
    private val joinModal = By.xpath("//*[contains(., 'Join')]")

    // open base URL and wait for something stable on the home screen
    fun open(baseUrl: String){
        driver.get(baseUrl)
        waitForLoaded()
    }

    // “page is ready enough to interact” = Join button visible
    fun waitForLoaded(){
        Waits.visible(driver, joinBtn, 10)
    }

    // --- PAge Actions ---
    fun dismissJurisdictionPopup() {
        log.info("Dismissing jurisdiction popup if present")
        //driver.findElements(stayOnThisPageBtn).firstOrNull()?.click()
        Waits.clickable(driver, stayOnThisPageBtn, 10).click()

    }

    fun openJoin() {
        log.info("Clicking Join button")
        Waits.clickable(driver, joinBtn, 10).click()
    }

    fun waitForJoinModal() {
        log.info("Waiting for Join modal to be visible")
        Waits.visible(driver, joinModal, 10)
    }
}
