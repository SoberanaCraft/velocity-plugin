package net.soberanacraft.api

import com.github.michaelbull.logging.InlineLogger
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject
import net.soberanacraft.api.models.ErrorMessage
import net.soberanacraft.api.models.InvalidUUIDMessage
import net.soberanacraft.api.models.Message
import java.util.*

val logger = InlineLogger("http-client")

@Serializable
open class Fallible
@Serializable
class Failure(val message: Message) : Fallible()
@Serializable
class Success<T>(val value :T) : Fallible()

suspend inline fun <reified T> fallible(request: () -> HttpResponse) : Fallible {
    val stringBody = request().bodyAsText()
    val json = Json.parseToJsonElement(stringBody).jsonObject

	if (json.containsKey("error")) {
        logger.trace { "[Fallible Requests] Contains key: \"error\"" }
        val errorMessage = Json.decodeFromString<ErrorMessage>(stringBody)
        logger.info { "error: ${Json.encodeToString(errorMessage)}" }
        return Failure(errorMessage)
    }

    if (json.containsKey("param")) {
        logger.trace { "[Fallible Requests] Contains key: \"param\"" }
        val invalidUUIDMessage = Json.decodeFromString<InvalidUUIDMessage>(stringBody)
        logger.info { "error: ${Json.encodeToString(invalidUUIDMessage)}" }
        return Failure(invalidUUIDMessage)
    }

    logger.trace { "[Fallible Requests] Sucess!" }
    return Success<T>(Json.decodeFromString(stringBody))
}

suspend inline fun _delete(path: String) : HttpResponse {
    logger.trace { "[DELETE] ${SoberanaApiClient.config.api.endpoint}$path" }
    val response = SoberanaApiClient.client.get (SoberanaApiClient.config.api.endpoint + path) {
        this.bearerAuth(SoberanaApiClient.config.api.token)
        this.userAgent("SoberanaCraft-API Authorized Delete")
    }
    logger.trace { "Response: $response" }
    return response
}


suspend inline fun _get(path: String) : HttpResponse {
    logger.trace { "[GET] ${SoberanaApiClient.config.api.endpoint}$path" }
    val response = SoberanaApiClient.client.get (SoberanaApiClient.config.api.endpoint + path) {
        this.userAgent("SoberanaCraft-API Anonymous Get")
    }
    logger.trace { "Response: $response" }

    return response
}

suspend inline fun _authGet(path: String) : HttpResponse {
    logger.trace { "[GET] ${SoberanaApiClient.config.api.endpoint}$path" }
    val response = SoberanaApiClient.client.get (SoberanaApiClient.config.api.endpoint + path) {
        this.bearerAuth(SoberanaApiClient.config.api.token)
        this.userAgent("SoberanaCraft-API Authorized Get")
    }
    logger.trace { "Response: $response" }

    return response
}

suspend inline fun <reified A> _post(path: String, value : A? = null) : HttpResponse {
    logger.trace { "[POST] ${SoberanaApiClient.config.api.endpoint}$path" }
    val response = SoberanaApiClient.client.post (SoberanaApiClient.config.api.endpoint + path) {
        contentType(ContentType.Application.Json)
        if(value != null) this.setBody(value)
        this.bearerAuth(SoberanaApiClient.config.api.token)
        this.userAgent("SoberanaCraft-API Authorized POST")
    }
    logger.trace { "Response: $response" }
    return response
}

suspend inline fun <reified T> get(path: String): T {
    return _get(path).body()
}

suspend inline fun <reified T> delete(path: String) : T {
    return _delete(path).body()
}

suspend inline fun <reified T> authGet(path: String): T {
    return _authGet(path).body()
}

suspend inline fun <reified T, reified A> post(path: String, value: A?) : T {
    return _post(path, value).body()
}

fun String.intoUUID() : UUID = UUID.fromString(this)