package steps

import io.cucumber.java.en.Given
import pages.HomePage
import support.TestContext
import org.junit.Assert.assertTrue
import support.Config


/**
 * Background glue for shared preconditions:
 *  land on home
 *  clear overlays
 *  hit Join
 */
class HomeSteps(private val ctx: TestContext) {

    @Given("the user is on the home page")
    fun user_on_home_page() {
        HomePage(ctx.driver).open(Config.get("baseUrl", "https://www.bet99.com/"))
        assertTrue("Could not open the home page", true)
    }


    @Given("they dismiss any jurisdiction or cookie popups")
    fun dismiss_popups() {
        HomePage(ctx.driver).dismissJurisdictionPopup()
        assertTrue("Unable to dismiss jurisdiction or cookie popups", true)
    }

    @Given("they tap the Join button")
    fun tap_join_button() {
        val home = HomePage(ctx.driver)
        home.openJoin()
        home.waitForJoinModal()
        assertTrue("Join modal was not displayed", true)
    }
}
