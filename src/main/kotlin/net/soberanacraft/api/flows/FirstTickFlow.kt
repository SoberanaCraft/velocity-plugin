package net.soberanacraft.api.flows

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import net.soberanacraft.SoberanaPlugin
import net.soberanacraft.api.SoberanaApi
import net.soberanacraft.api.logger
import net.soberanacraft.api.models.Server
import net.soberanacraft.api.models.ServerStub

suspend fun FirstTickFlow(server: ProxyServer) {
    // Check if registered servers exist on the backend
    for (registeredServer in server.allServers) {
        val backendServer = registeredServer.serverInfo.getExistingServerDataOrNull()
        if (backendServer == null) {
            logger.warn { "Server with id: ${registeredServer.serverInfo.name} wasn't registered on the backend when the proxy initialized." }
            return
        }
        SoberanaPlugin.serverRefMap[registeredServer.serverInfo.address!!] = backendServer.id
    }
}

suspend fun ServerInfo.getExistingServerDataOrNull(): Server? {
    val apiServers = SoberanaApi.servers()
    if(apiServers.any { it.name == this.name }) {
        return apiServers.first { it.name == this.name }
    }
    return null
}