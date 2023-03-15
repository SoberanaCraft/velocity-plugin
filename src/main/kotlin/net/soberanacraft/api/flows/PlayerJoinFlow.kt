package net.soberanacraft.api.flows

import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.util.RGBLike
import net.soberanacraft.SoberanaPlugin
import net.soberanacraft.api.Failure
import net.soberanacraft.api.SoberanaApi
import net.soberanacraft.api.Success
import net.soberanacraft.api.models.Connection
import net.soberanacraft.api.models.Platform
import net.soberanacraft.api.models.PlayerStub

suspend fun PlayerJoinFlow(ev: ServerPostConnectEvent) {
    ev.player.connect()
}

suspend fun Player.connect(){
    if(SoberanaApi.isAvailable(this.username)) {
        //TODO: Integrate with geyser to properly get Player platform.
        val response = SoberanaApi.Auth.createPlayer(PlayerStub(this.uniqueId, this.username, Platform.Java))
        when (response) {
            is Failure -> {
                this.sendMessage(Component.text ( "Um erro interno occorreu:")
                    .append(Component.text("${response.message}",
                        Style.style(TextColor.color(0xF5130F)))))

                return
            }
        }
    }

    val address = this.currentServer.get().server.serverInfo.address

    if (SoberanaPlugin.serverRefMap.containsKey(address)) {
        val serverUUID = SoberanaPlugin.serverRefMap[address]!!
        val conn = Connection(this.uniqueId, this.protocolVersion.name, serverUUID)
        val response = SoberanaApi.Auth.joinServer(conn)
        when (response) {
            is Failure -> {
                this.sendMessage(Component.text ( "Um erro interno occorreu:")
                    .append(Component.text("${response.message}",
                        Style.style(TextColor.color(0xF5130F)))))

                return
            }
        }

        SoberanaPlugin.connectionsRefMap[this.uniqueId] = conn
    }
}

