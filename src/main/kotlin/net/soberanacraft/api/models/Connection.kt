package net.soberanacraft.api.models

import kotlinx.serialization.Serializable
import net.soberanacraft.api.UUIDSerializer
import java.util.UUID

@Serializable
data class Connection(
    @Serializable(with = UUIDSerializer::class) val playerUUID: UUID,
    val version: String,
    @Serializable(with = UUIDSerializer::class) val serverId: UUID
)