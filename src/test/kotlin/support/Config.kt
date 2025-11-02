package support

import java.io.File
import java.util.*

/**
 * Reads config from:
 * 1) JVM system properties (-Dkey=value), then
 * 2) config/test.properties, then
 * 3) provided default in code.
 *
 * Usage: Config.get("baseUrl", "https://bet99.com/")
 */
object Config {
    private val props = Properties().apply {
        // Load once from config/test.properties if it exists
        val f = File("config/test.properties")
        if (f.exists()) f.inputStream().use { load(it) }
    }

    fun get(key: String, def: String = ""): String =
        // Precedence: -D overrides file, else fallback to def
        System.getProperty(key) ?: props.getProperty(key, def)
}
