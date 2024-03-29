package org.valkyrienskies.core.impl.game.ships

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.apigame.world.chunks.BlockType
import org.valkyrienskies.core.apigame.world.properties.DimensionId
import org.valkyrienskies.core.impl.api.ShipInternal
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider
import org.valkyrienskies.core.impl.util.coroutines.TickableCoroutineDispatcher
import org.valkyrienskies.core.impl.util.logger

/**
 * Manages all the [ShipObject]s in a world.
 */
abstract class ShipObjectWorld<ShipObjectType : ShipObject>(
    private val chunkAllocators: ChunkAllocatorProvider
) : ShipWorld {

    abstract override val allShips: QueryableShipData<ShipInternal>
    abstract override val loadedShips: QueryableShipData<ShipObjectType>

    @Deprecated(message = "use loadedShips", replaceWith = ReplaceWith("loadedShips"))
    val shipObjects: Map<ShipId, ShipObjectType> get() = loadedShips.idToShipData

    private val _dispatcher = TickableCoroutineDispatcher()
    val dispatcher: CoroutineDispatcher = _dispatcher
    val coroutineScope = MainScope() + _dispatcher

    var tickNumber = 0
        private set

    override fun isChunkInShipyard(chunkX: Int, chunkZ: Int, dimensionId: DimensionId) =
        chunkAllocators.forDimension(dimensionId).isChunkInShipyard(chunkX, chunkZ)

    override fun isBlockInShipyard(blockX: Int, blockY: Int, blockZ: Int, dimensionId: DimensionId): Boolean =
        chunkAllocators.forDimension(dimensionId).isBlockInShipyard(blockX, blockY, blockZ)

    open fun preTick() {
        try {
            _dispatcher.tick()
        } catch (ex: Exception) {
            logger.error("Error while ticking ships", ex)
        }
        tickNumber++
    }

    open fun onSetBlock(
        posX: Int,
        posY: Int,
        posZ: Int,
        dimensionId: DimensionId,
        oldBlockType: BlockType,
        newBlockType: BlockType,
        oldBlockMass: Double,
        newBlockMass: Double
    ) {
        // If there is a ShipData at this position and dimension, then tell it about the block update
        allShips.getByChunkPos(posX shr 4, posZ shr 4, dimensionId)?.onSetBlock(
            posX, posY, posZ, oldBlockType, newBlockType, oldBlockMass, newBlockMass, this is ServerShipWorld
        )
    }

    @Deprecated("redundant", ReplaceWith("loadedShips.getShipDataIntersecting(aabb)"))
    override fun getShipObjectsIntersecting(aabb: AABBdc): List<ShipObjectType> =
        loadedShips.getShipDataIntersecting(aabb).toList()

    abstract fun destroyWorld()

    companion object {
        private val logger by logger()
    }
}
