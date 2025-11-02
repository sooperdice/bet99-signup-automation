package pages

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.Select
import support.Waits

data class AddressAutofill(
    val line1: String,
    val city: String,
    val postal: String,
    val provinceValue: String, // e.g., "ON"
    val countryValue: String   // e.g., "CA"
)

class JoinStep2Page(private val driver: WebDriver) {

    // Guards / anchors
    private val step2Form        = By.cssSelector("form[data-cy='form-6915f3f7']")
    private val addressLookup    = By.cssSelector("[data-cy='autocompleteaddress-cb946322']")
    private val submitBtn        = By.cssSelector("[data-cy='button-8fda5617']")

    // Autofilled fields (readonly then enabled once picked)
    private val line1Input       = By.id("houseNo")
    private val cityInput        = By.id("city")
    private val postalInput      = By.id("postcode")
    private val provinceSelect   = By.cssSelector("[data-cy='stateselect-0a1ca5eb']")   // <option> markers
    private val provinceSelectBox= By.xpath("//label[@for='postcode']/ancestor::div[contains(@data-cy,'inputwrapper-')]/following::select[1] | //select[option[@data-cy='stateselect-0a1ca5eb']]")
    private val countrySelect    = By.cssSelector("[data-cy='countryselect-25074a35']") // <option> markers
    private val countrySelectBox = By.xpath("//select[option[@data-cy='countryselect-25074a35']]")

    // Optional marketing checkbox
    private val marketingOptIn   = By.id("communicationConsent")

    // --- Autocomplete  ---
    private val addressInput    = By.cssSelector("div[data-cy^='autocompleteaddress-'] input")
    private val suggestionList  = By.cssSelector("div[data-cy^='autocompleteaddress-'] ul[data-cy^='autocompleteaddress-']")
    private val suggestionItems = By.cssSelector("div[data-cy^='autocompleteaddress-'] li[data-cy^='autocompleteaddress-']")


    fun waitForLoaded(): JoinStep2Page {
        //Waits.visible(driver, step2Form, 10)
        //Waits.visible(driver, addressLookup, 10)
        Waits.visible(driver, submitBtn, 10)
        return this
    }

    /**
     * Types into the address search box and confirms with ENTER.
     * We then wait until the readonly fields are populated and province/country selects are present.
     */
    fun searchAndPickAddress(query: String, pickTextContains: String? = null, index: Int = 0): JoinStep2Page {
        val input = Waits.visible(driver, addressInput, 10)
        input.clear()
        input.sendKeys(query)
        Waits.visible(driver, suggestionList, 8)
        Waits.fluent(driver, timeoutSec = 8, pollMs = 120) {
            driver.findElements(suggestionItems).isNotEmpty()
        }

        val items = driver.findElements(suggestionItems)
        val target = if (!pickTextContains.isNullOrBlank()) {
            items.firstOrNull { it.text.contains(pickTextContains, ignoreCase = true) } ?: items.getOrNull(index)
        } else items.getOrNull(index)

        requireNotNull(target) { "No suggestion items available to pick." }
        runCatching { target.click() }.onFailure {
            (driver as JavascriptExecutor).executeScript("arguments[0].click();", target)
        }

        Waits.fluent(driver, timeoutSec = 8, pollMs = 150) {
            val line = driver.findElement(line1Input).getAttribute("value")?.isNotBlank() == true
            val city = driver.findElement(cityInput).getAttribute("value")?.isNotBlank() == true
            val pc   = driver.findElement(postalInput).getAttribute("value")?.isNotBlank() == true
            line && city && pc
        }

        return this
    }



    /**
     * Asserts the autofilled values match what we expect from our test data
     * (light normalization on spaces/case; postal uppercased & trimmed).
     */
    fun assertAutofilled(expected: support.Address): JoinStep2Page {
        fun norm(s: String?) = s?.trim()?.lowercase().orEmpty()

        val gotLine1 = driver.findElement(line1Input).getAttribute("value")
        val gotCity  = driver.findElement(cityInput).getAttribute("value")
        val gotPC    = driver.findElement(postalInput).getAttribute("value")?.replace(" ", "")?.uppercase()

        check(norm(gotLine1) == norm(expected.line1)) {
            "Line1 mismatch. Got '$gotLine1' expected '${expected.line1}'"
        }
        check(norm(gotCity) == norm(expected.city)) {
            "City mismatch. Got '$gotCity' expected '${expected.city}'"
        }
        check(gotPC == expected.postalCode.replace(" ", "").uppercase()) {
            "Postal mismatch. Got '$gotPC' expected '${expected.postalCode}'"
        }

        // Province/country (if selects are enabled, validate by value; if disabled, just ensure options exist)
        val provBox = driver.findElement(provinceSelectBox)
        val countryBox = driver.findElement(countrySelectBox)

        val provSel = Select(provBox)
        val countrySel = Select(countryBox)

        // If component enables province and sets value, check it. Otherwise just ensure expected option exists.
        val expectedProv = provinceToCode(expected.province) // "Ontario" -> "ON", etc.
        val currentProv = provSel.allSelectedOptions.firstOrNull()?.getAttribute("value")
        if (!currentProv.isNullOrBlank()) {
            check(currentProv == expectedProv) { "Province mismatch. Got '$currentProv' expected '$expectedProv'" }
        } else {
            check(provSel.options.any { it.getAttribute("value") == expectedProv }) {
                "Expected province option '$expectedProv' not present"
            }
        }

        val currentCountry = countrySel.allSelectedOptions.firstOrNull()?.getAttribute("value")
        if (!currentCountry.isNullOrBlank()) {
            check(currentCountry == "CA") { "Country mismatch. Got '$currentCountry' expected 'CA'" }
        } else {
            check(countrySel.options.any { it.getAttribute("value") == "CA" }) {
                "Expected country option 'CA' not present"
            }
        }

        return this
    }

    fun setMarketingOptIn(desired: Boolean): JoinStep2Page {
        val el = Waits.visible(driver, marketingOptIn, 5)
        val checked = el.getAttribute("checked") == "true" || el.isSelected
        if (checked != desired) el.click()
        return this
    }

    fun isSubmitEnabled(): Boolean {
        val btn = Waits.visible(driver, submitBtn, 5)
        val disabledAttr = btn.getAttribute("disabled")?.trim()?.lowercase()
        return btn.isEnabled && disabledAttr != "true"
    }

    fun submit(): JoinStep2Page {
        Waits.clickable(driver, submitBtn, 10).click()
        return this
    }

    // --- helpers ---
    private fun provinceToCode(nameOrCode: String): String {
        val m = mapOf(
            "alberta" to "AB", "british columbia" to "BC", "manitoba" to "MB",
            "new brunswick" to "NB", "newfoundland and labrador" to "NL",
            "northwest territories" to "NT", "nova scotia" to "NS", "nunavut" to "NU",
            "prince edward island" to "PE", "ontario" to "ON", "quebec" to "QC",
            "saskatchewan" to "SK", "yukon" to "YT"
        )
        val s = nameOrCode.trim()
        return when {
            s.length == 2 -> s.uppercase()
            else -> m[s.lowercase()] ?: s.uppercase()
        }
    }
}
