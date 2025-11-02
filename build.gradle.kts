plugins {
    kotlin("jvm") version "2.2.0"
    id("io.qameta.allure") version "2.12.0"
}

group = "com.ashgavai.qa"
version = "1.0-SNAPSHOT"


dependencies {
    val cucumber = "5.7.0"
    val selenium = "4.21.0"

    // Cucumber 5 + JUnit4
    testImplementation("io.cucumber:cucumber-java:$cucumber")
    testImplementation("io.cucumber:cucumber-junit:$cucumber")
    testImplementation("io.cucumber:cucumber-core:$cucumber")
    testImplementation("io.cucumber:cucumber-picocontainer:$cucumber")

    // Allure Cucumber5 adapter — single version, pinned
    testImplementation("io.qameta.allure:allure-cucumber5-jvm") {
        version { strictly("2.20.1") }
    }

    // Selenium
    testImplementation("org.seleniumhq.selenium:selenium-java:$selenium")
    testImplementation("org.seleniumhq.selenium:selenium-devtools-v125:$selenium")

    // JUnit4
    testImplementation("junit:junit:4.13.2")

    // Logging
    testImplementation("org.slf4j:slf4j-simple:2.0.12")
}

tasks.test {
    useJUnit() // JUnit4 runner (Cucumber 5.x)

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
    systemProperty("cucumber.publish.quiet", "true")
    // make sure results land in the default place Allure expects
    systemProperty("allure.results.directory", layout.buildDirectory.dir("allure-results").get().asFile.absolutePath)

    // -> always generate the HTML report after tests
    finalizedBy("allureReport")
}

// Convenience task: run tests then serve the report locally
tasks.register("testAndServe") {
    dependsOn("clean", "test", "allureReport", "allureServe")
}



kotlin { jvmToolchain(24) }

allure {
    version.set("2.29.0") // Allure report engine (fine to keep recent)
    adapter {
        autoconfigure.set(false)
        aspectjWeaver.set(false)
    }
}
