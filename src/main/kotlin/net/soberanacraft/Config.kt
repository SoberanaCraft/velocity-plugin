package net.soberanacraft

import com.github.michaelbull.logging.InlineLogger
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

val logger = InlineLogger("config-manager")

@Serializable
data class ApiConfig(val endpoint: String, val token: String) {
    companion object {
        val Default = ApiConfig("http://localhost:8080/", "")
    }
}

@Serializable
data class Config(val api: ApiConfig) {
    companion object {
        val Default = Config(ApiConfig.Default)
    }
}

fun fromFile(path: String): Config {
    logger.info {"Getting config from file: $path"}
    val path = Path(path)
    if (!path.exists()) {
        logger.warn { "No existing config found at $path" }
        logger.warn { "Cannot access POST endpoints unless TOKEN is changed." }
        val s = Json.encodeToString(Config.Default)
        path.writeText(s)
        logger.error { "Please edit the config created at $path" }
    }
    return Json.decodeFromString(path.readText())
}