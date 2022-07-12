package org.valkyrienskies.core.game.ships

import org.joml.Matrix4dc
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.Ship
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet
import org.valkyrienskies.core.datastructures.IBlockPosSet
import org.valkyrienskies.core.game.ChunkClaim
import org.valkyrienskies.core.game.DimensionId
import org.valkyrienskies.core.game.VSBlockType
import org.valkyrienskies.core.util.serialization.DeltaIgnore
import org.valkyrienskies.core.util.serialization.PacketIgnore
import org.valkyrienskies.core.util.toAABBd

open class ShipDataCommon(
    val id: ShipId,
    var name: String,
    val chunkClaim: ChunkClaim,
    val chunkClaimDimension: DimensionId,
    @DeltaIgnore
    val physicsData: ShipPhysicsData,
    shipTransform: ShipTransform,
    prevTickShipTransform: ShipTransform = shipTransform,
    shipAABB: AABBdc = shipTransform.createEmptyAABB(),
    var shipVoxelAABB: AABBic?,
    val shipActiveChunksSet: IShipActiveChunksSet
) : Ship {
    @DeltaIgnore
    var shipTransform: ShipTransform = shipTransform
        set(shipTransform) {
            field = shipTransform
            // Update the [shipAABB]
            shipAABB = shipVoxelAABB?.toAABBd(AABBd())?.transform(shipTransform.shipToWorldMatrix, AABBd())
                ?: shipTransform.createEmptyAABB()
        }

    @PacketIgnore
    var prevTickShipTransform: ShipTransform = prevTickShipTransform
        private set

    @DeltaIgnore
    var shipAABB: AABBdc = shipAABB
        private set

    fun updatePrevTickShipTransform() {
        prevTickShipTransform = shipTransform
    }

    /**
     * Updates the [IBlockPosSet] and [ShipInertiaData] for this [ShipData]
     */
    internal open fun onSetBlock(
        posX: Int,
        posY: Int,
        posZ: Int,
        oldBlockType: VSBlockType,
        newBlockType: VSBlockType,
        oldBlockMass: Double,
        newBlockMass: Double
    ) {
        // Sanity check
        require(
            chunkClaim.contains(
                posX shr 4,
                posZ shr 4
            )
        ) { "Block at <$posX, $posY, $posZ> is not in the chunk claim belonging to $this" }

        // Add the chunk to the active chunk set
        shipActiveChunksSet.addChunkPos(posX shr 4, posZ shr 4)
        // Add the neighbors too (Required for rendering code in MC 1.16, chunks without neighbors won't render)
        // TODO: Make a separate set for keeping track of neighbors
        shipActiveChunksSet.addChunkPos((posX shr 4) - 1, (posZ shr 4))
        shipActiveChunksSet.addChunkPos((posX shr 4) + 1, (posZ shr 4))
        shipActiveChunksSet.addChunkPos((posX shr 4), (posZ shr 4) - 1)
        shipActiveChunksSet.addChunkPos((posX shr 4), (posZ shr 4) + 1)
    }

    override val shipToWorld: Matrix4dc
        get() = shipTransform.shipToWorldMatrix
    override val worldToShip: Matrix4dc
        get() = shipTransform.worldToShipMatrix

    // Client side doesn't have any attachments, this is replaced in the server's [ShipData]
    override fun <T> getAttachment(clazz: Class<T>): T? = null

    override fun <T> setAttachment(clazz: Class<T>, value: T?) {}

    override fun <T> saveAttachment(clazz: Class<T>, value: T?) {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShipDataCommon

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
