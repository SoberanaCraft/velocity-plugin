package net.soberanacraft.api.flows

import com.velocitypowered.api.event.connection.DisconnectEvent
import net.soberanacraft.SoberanaPlugin
import net.soberanacraft.api.SoberanaApi

suspend fun DisconnectFlow(ev: DisconnectEvent) {
    if(SoberanaPlugin.connectionsRefMap.containsKey(ev.player.uniqueId)) {
        SoberanaApi.Auth.disconnectServer(SoberanaPlugin.connectionsRefMap[ev.player.uniqueId]!!)
        SoberanaPlugin.connectionsRefMap.remove(ev.player.uniqueId)
    }
}