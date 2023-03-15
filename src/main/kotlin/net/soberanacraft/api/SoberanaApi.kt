package net.soberanacraft.api

import com.github.michaelbull.logging.InlineLogger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.soberanacraft.Config
import net.soberanacraft.api.models.*
import java.util.UUID

object SoberanaApiClient {
    private val logger = InlineLogger()
    lateinit var client: HttpClient
    lateinit var config: Config

    @OptIn(ExperimentalSerializationApi::class)
    fun init(config: Config) {
        logger.info { "Starting..." }
        this.config = config
        logger.info { "Creating new HttpClient" }
        client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                })
            }
        }
        logger.info { "Done" }
    }

}

object SoberanaApi {
    suspend fun players () = get<List<Player>>("/players")
    suspend fun player (uuid: UUID) = get<Player>("/player/$uuid")
    suspend fun nonce () = get<String>("/nonce")
    suspend fun auth (state: String) = fallible<String> { _get("/auth?state=$state") }
    suspend fun isAvailable(nick: String) =  get<Boolean>("/player/isAvailable/$nick")
    suspend fun servers() = get<List<Server>>("/servers")

    // Authenticated Routes
    object Auth {
        suspend fun me() = authGet<String>("/@me")
        suspend fun createPlayer(stub: PlayerStub) = fallible<Player> { _post("/player/create", stub) }
        suspend fun addReferee(player: UUID, referee: UUID) = fallible<Boolean> { _post<Any>("/player/refer/$player/$referee") }
        suspend fun revokePlayer(uuid: UUID) = fallible<Boolean> { _post<Any>("/player/revoke?uuid=$uuid") }
        suspend fun createNonce(owner: UUID, nonce: String) = fallible<Nonce> { _post<Any>("/nonce/create?uuid=$owner&nonce=$nonce") }
        suspend fun revokeNonce(owner: UUID) = fallible<Boolean> { _delete("/nonce/revoke?uuid=$owner") }
        suspend fun joinServer(connection: Connection) = fallible<SucessMessage> { _post("/server/join", connection) }
        suspend fun disconnectServer(connection: Connection) = fallible<SucessMessage> { _post("/server/disconnect", connection) }
        suspend fun createServer(server: ServerStub) = fallible<Server> { _post("/server/create", server) }
        suspend fun revokeServer(uuid: UUID) = fallible<Boolean> {  _delete("/server/revoke?id=$uuid") }
    }
}