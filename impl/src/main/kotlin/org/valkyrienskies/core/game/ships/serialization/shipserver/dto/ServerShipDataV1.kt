package org.valkyrienskies.core.game.ships.serialization.shipserver.dto

import com.google.common.collect.MutableClassToInstanceMap
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet
import org.valkyrienskies.core.game.ships.ShipInertiaDataImpl
import org.valkyrienskies.core.game.ships.ShipPhysicsData

data class ServerShipDataV1(
    val id: ShipId,
    val name: String,
    val chunkClaim: ChunkClaim,
    val chunkClaimDimension: DimensionId,
    val physicsData: ShipPhysicsData,
    val inertiaData: ShipInertiaDataImpl,
    val shipTransform: ShipTransform,
    val prevTickShipTransform: ShipTransform,
    val shipAABB: AABBdc,
    val shipVoxelAABB: AABBic?,
    val shipActiveChunksSet: IShipActiveChunksSet,
    val isStatic: Boolean,
    val persistentAttachedData: MutableClassToInstanceMap<Any>
)
