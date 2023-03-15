package net.soberanacraft

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.velocitypowered.api.event.Continuation
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.soberanacraft.api.*
import net.soberanacraft.api.flows.DisconnectFlow
import net.soberanacraft.api.flows.FirstTickFlow
import net.soberanacraft.api.flows.PlayerJoinFlow
import net.soberanacraft.api.models.Connection
import org.slf4j.Logger
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.file.Path
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

@Plugin(
    id = "soberanacraft",
    name = "SoberanaCraft",
    version = "1.0.0-SNAPSHOT",
    authors = ["roridev@soberanacraft.net"],
    dependencies = []
)
class SoberanaPlugin : CoroutineScope {
    @Inject
    lateinit var logger : Logger
    val inlineLogger get() = InlineLogger(logger)

    @Inject
    lateinit var server : ProxyServer

    @Inject
    @DataDirectory
    lateinit var dataDirectory : Path

    lateinit var config : Config

    override val coroutineContext: CoroutineContext = Dispatchers.Default + CoroutineName("SoberanaPlugin")

    @Subscribe
    fun onProxyInitialization(ev: ProxyInitializeEvent, continuation: Continuation) : Unit {
        inlineLogger.info { "Initializing Plugin" }
        if (!dataDirectory.exists()) dataDirectory.createDirectories()
        val configPath = dataDirectory.resolve("config.json").toAbsolutePath()
        config = fromFile(configPath.toString())
        inlineLogger.info { "Config loaded" }

        SoberanaApiClient.init(config)
        inlineLogger.info { "HttpClient loaded" }
        continuation await launch {
            inlineLogger.info { "First tick flow started." }
            FirstTickFlow(server)
            inlineLogger.info { "First tick flow finished." }
        }
    }

    @Subscribe
    fun onPlayerLogin(ev: ServerPostConnectEvent, continuation: Continuation) {
        continuation await launch {
            PlayerJoinFlow(ev)
        }
    }

    @Subscribe
    fun onPlayerDisconnect(ev: DisconnectEvent, continuation: Continuation) {
        continuation await launch {
            DisconnectFlow(ev)
        }
    }

    companion object {
        val serverRefMap : MutableMap<InetSocketAddress, UUID> = mutableMapOf()
        val connectionsRefMap: MutableMap<UUID, Connection> = mutableMapOf()
    }
}