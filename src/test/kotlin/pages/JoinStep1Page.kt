package pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.Select
import support.Waits
import support.User
import support.TestDataFactory
import support.type   // <-- extension imports
import support.check  // <-- extension imports
import org.slf4j.LoggerFactory
import support.TestDataFactory.user


class JoinStep1Page(private val driver: WebDriver) {
    private val log = LoggerFactory.getLogger(javaClass)

    //  Modal guard (Step 1 visible)
    private val step1Title      = By.cssSelector("[data-cy^='quickregisterstep-'][class*='title']")
    private val formRoot        = By.cssSelector("form[data-cy^='form-']")

    //  Text fields
    private val username        = By.id("username")
    private val password        = By.id("password")
    private val confirmPassword = By.id("confirmPassword")
    private val email           = By.id("email")
    private val firstName       = By.id("forename")
    private val lastName        = By.id("surname")

    // Phone (intl-tel-input wrapper → inner <input type='tel'>)
    private val phoneInput      = By.cssSelector(".intl-tel-input input[type='tel']")

    // Promo
    private val promoDropdown   = By.cssSelector("[data-cy='selectelement-ecf39028'] select")
    private val promoManual     = By.id("offlineCode")

    // DOB (Month / Day / Year)
    private val dobSelects      = By.cssSelector("[data-cy='selectdob-2d710be8'] select") // 0=Mon,1=Day,2=Year

    // Language (values EN / FR)
    private val languageSelect  = By.cssSelector("[data-cy='select-5daef049'] select")

    // Checkboxes
    private val termsCheckbox   = By.id("check1")

    // Continue button
    private val continueBtn     = By.cssSelector("[data-cy='button-8fda5617']")

    fun waitForLoaded() {
        log.info("JoinStep1: wait for Step 1 modal to be visible")
        Waits.visible(driver, step1Title, 10)
        Waits.visible(driver, formRoot, 10)
        Waits.visible(driver, username, 10)
    }

    fun fillMandatory(u: User, pwd: String) {
        log.info("JoinStep1: fill mandatory fields for user=${user().username}")
        driver.type(username, u.username)
        driver.type(password, pwd)
        driver.type(confirmPassword, pwd)
        driver.type(email, u.email)
        driver.type(firstName, u.firstName)
        driver.type(lastName, u.lastName)

        // Phone (required)
        driver.findElements(phoneInput).firstOrNull()?.let { el ->
            el.clear(); el.sendKeys(u.phone)
        }

        // DOB selects (0: Month "Jan", 1: Day "1..31", 2: Year "1992")
        val selects = driver.findElements(dobSelects)
        require(selects.size >= 3) { "DOB selects not found / markup changed" }
        Select(selects[0]).selectByVisibleText(TestDataFactory.dobMonthShort(u.dob)) // <-- short month e.g., "Jan"
        Select(selects[1]).selectByVisibleText(u.dob.dayOfMonth.toString())
        Select(selects[2]).selectByVisibleText(TestDataFactory.dobYmd(u.dob))

        // Language (values EN/FR)
        val langValue = when (u.language.lowercase()) {git

            "english", "en" -> "EN"
            "french", "fr"  -> "FR"
            else            -> "EN"
        }
        Select(Waits.visible(driver, languageSelect, 5)).selectByValue(langValue)

        // T&C checkbox (required)
        driver.check(termsCheckbox, true)
    }

    fun fillOptionalPromo(u: User) {
        driver.findElements(promoDropdown).firstOrNull()?.let { /* Select it if needed */ }
        u.promoCode?.let { code ->
            driver.findElements(promoManual).firstOrNull()?.let { el ->
                el.clear(); el.sendKeys(code)
            }
        }
    }

    fun continueNext(){
        log.info("JoinStep1: click Continue")
        Waits.clickable(driver, continueBtn, 10).click()
    }

    fun isContinueEnabled(): Boolean {
        val btn = Waits.visible(driver, continueBtn, 5)
        val disabledAttr = btn.getAttribute("disabled")?.trim()?.lowercase()
        return btn.isEnabled && disabledAttr != "true"
    }
}
