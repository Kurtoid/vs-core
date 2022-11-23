package org.valkyrienskies.core.game.ships.serialization.shipserver.dto

import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet
import org.valkyrienskies.core.game.ships.ShipPhysicsData
import org.valkyrienskies.core.game.ships.serialization.shipinertia.dto.ShipInertiaDataV0
import org.valkyrienskies.core.game.ships.serialization.shiptransform.dto.ShipTransformDataV0

class ServerShipDataV2(
    val id: ShipId,
    val name: String,
    val chunkClaim: ChunkClaim,
    val chunkClaimDimension: DimensionId,
    val physicsData: ShipPhysicsData,
    val inertiaData: ShipInertiaDataV0,
    val shipTransform: ShipTransformDataV0,
    val prevTickShipTransform: ShipTransformDataV0,
    val shipAABB: AABBdc,
    val shipVoxelAABB: AABBic?,
    val shipActiveChunksSet: IShipActiveChunksSet,
    val isStatic: Boolean,
    val persistentAttachedData: Map<Class<*>, Any>
)
