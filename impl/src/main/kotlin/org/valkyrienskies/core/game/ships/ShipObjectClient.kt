package org.valkyrienskies.core.game.ships

import com.fasterxml.jackson.databind.JsonNode
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ClientShipInternal
import org.valkyrienskies.core.api.ShipInternal
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.networking.delta.DeltaEncodedChannelClientTCP
import org.valkyrienskies.core.util.serialization.VSJacksonUtil
import org.valkyrienskies.core.util.toAABBd

class ShipObjectClient(
    shipData: ShipDataCommon,
    shipDataJson: JsonNode = VSJacksonUtil.defaultMapper.valueToTree(shipData)
) : ShipObject(shipData), ClientShipInternal, ShipInternal by shipData {
    // The last ship transform sent by the sever
    internal var nextShipTransform: ShipTransform

    override lateinit var renderTransform: ShipTransform
        private set

    override lateinit var renderAABB: AABBdc
        private set

    internal var latestNetworkTransform: ShipTransform = shipData.transform
    internal var latestNetworkTTick = Int.MIN_VALUE

    internal val shipDataChannel = DeltaEncodedChannelClientTCP(jsonDiffDeltaAlgorithm, shipDataJson)

    init {
        nextShipTransform = shipData.transform
        renderTransform = shipData.transform
        renderAABB = shipData.transform.createEmptyAABB()
    }

    fun tickUpdateShipTransform() {
        this.nextShipTransform = latestNetworkTransform
        shipData.updatePrevTickShipTransform()
        shipData.transform = ShipTransformImpl.createFromSlerp(shipData.transform, nextShipTransform, EMA_ALPHA)
    }

    fun updateRenderShipTransform(partialTicks: Double) {
        renderTransform =
            ShipTransformImpl.createFromSlerp(shipData.prevTickTransform, shipData.transform, partialTicks)
        renderAABB = shipData.shipAABB?.toAABBd(AABBd())?.transform(renderTransform.shipToWorld, AABBd())
            ?: renderTransform.createEmptyAABB()
    }

    companion object {
        // Higher values will converge to the transforms sent by the server faster, but lower values make movement
        // smoother. Ideally this should be configurable, but leave it constant for now.
        private const val EMA_ALPHA = 0.7
    }
}
