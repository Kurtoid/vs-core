package org.valkyrienskies.core.impl.chunk_tracking

import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.apigame.world.properties.DimensionId
import org.valkyrienskies.core.apigame.world.IPlayer
import org.valkyrienskies.core.apigame.world.chunks.ChunkUnwatchTask

fun ChunkUnwatchTask(
    chunkPos: Long,
    dimensionId: DimensionId,
    playersNeedUnwatching: Iterable<IPlayer>,
    shouldUnload: Boolean,
    distanceToClosestPlayer: Double,
    ship: ServerShip
): ChunkUnwatchTask = ChunkUnwatchTaskImpl(chunkPos, dimensionId, playersNeedUnwatching, shouldUnload, distanceToClosestPlayer, ship)

/**
 * This task says that the chunk at [chunkPos] should no longer be watched by [playersNeedUnwatching].
 */
class ChunkUnwatchTaskImpl(
    override val chunkPos: Long,
    override val dimensionId: DimensionId,
    override val playersNeedUnwatching: Iterable<IPlayer>,
    override val shouldUnload: Boolean,
    private val distanceToClosestPlayer: Double,
    override val ship: ServerShip
) : Comparable<ChunkUnwatchTask>, ChunkUnwatchTask {

    override val chunkX: Int get() = IShipActiveChunksSet.longToChunkX(chunkPos)
    override val chunkZ: Int get() = IShipActiveChunksSet.longToChunkZ(chunkPos)

    override fun compareTo(other: ChunkUnwatchTask): Int {
        other as ChunkUnwatchTaskImpl
        return distanceToClosestPlayer.compareTo(other.distanceToClosestPlayer)
    }
}
