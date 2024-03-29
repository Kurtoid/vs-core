package org.valkyrienskies.core.apigame.world.chunks

import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.apigame.world.IPlayer
import org.valkyrienskies.core.apigame.world.properties.DimensionId

interface ChunkUnwatchTask : Comparable<ChunkUnwatchTask> {
    val chunkPos: Long
    val dimensionId: DimensionId
    val playersNeedUnwatching: Iterable<IPlayer>
    val shouldUnload: Boolean
    val ship: ServerShip
    val chunkX: Int
    val chunkZ: Int
}
