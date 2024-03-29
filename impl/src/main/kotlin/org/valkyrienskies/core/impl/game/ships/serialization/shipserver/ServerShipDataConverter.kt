package org.valkyrienskies.core.impl.game.ships.serialization.shipserver

import com.google.common.collect.MutableClassToInstanceMap
import dagger.Reusable
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.ShipInertiaConverter
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.ShipTransformConverter
import javax.inject.Inject

interface ServerShipDataConverter {
    fun convertToDto(model: ShipData): ServerShipDataV3

    fun convertToModel(dto: ServerShipDataV3): ShipData
}

@Reusable
class ServerShipDataConverterImpl @Inject constructor(
    private val inertiaConverter: ShipInertiaConverter,
    private val transformConverter: ShipTransformConverter
) : ServerShipDataConverter {
    override fun convertToDto(model: ShipData) = ServerShipDataV3(
        id = model.id,
        name = model.slug ?: "",
        chunkClaim = model.chunkClaim,
        chunkClaimDimension = model.chunkClaimDimension,
        velocity = model.physicsData.linearVelocity,
        omega = model.physicsData.angularVelocity,
        inertiaData = inertiaConverter.convertToDto(model.inertiaData),
        transform = transformConverter.convertToDto(model.transform),
        prevTickTransform = transformConverter.convertToDto(model.prevTickTransform),
        worldAABB = model.worldAABB,
        shipAABB = model.shipAABB,
        activeChunks = model.activeChunksSet,
        isStatic = model.isStatic,
        persistentAttachedData = model.persistentAttachedData
    )

    override fun convertToModel(dto: ServerShipDataV3): ShipData = ShipData(
        id = dto.id,
        slug = if (dto.name == "") null else dto.name,
        chunkClaim = dto.chunkClaim,
        chunkClaimDimension = dto.chunkClaimDimension,
        physicsData = ShipPhysicsData(dto.velocity, dto.omega),
        inertiaData = inertiaConverter.convertToModel(dto.inertiaData),
        shipTransform = transformConverter.convertToModel(dto.transform),
        prevTickShipTransform = transformConverter.convertToModel(dto.prevTickTransform),
        shipAABB = dto.worldAABB,
        shipVoxelAABB = dto.shipAABB,
        shipActiveChunksSet = dto.activeChunks,
        isStatic = dto.isStatic,
        persistentAttachedData = MutableClassToInstanceMap.create(dto.persistentAttachedData)
    )
}
