package runners

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

// Tels JUNIT to execute tests using Cucumber (e.g .feature files instead of methods)
@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["src/test/kotlin/features"],
    glue = ["steps", "support"],
    plugin = [
        "pretty",
        "summary",
        "io.qameta.allure.cucumber5jvm.AllureCucumber5Jvm",
        "json:build/cucumber.json"
    ],
    monochrome = true,
    tags = ["@registration and not @wip"] // Run all scenarios except those tagged with @wip (work in progress).

)
class CucumberRunner
