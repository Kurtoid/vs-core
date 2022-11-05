package org.valkyrienskies.core.game.ships

import com.google.common.collect.MutableClassToInstanceMap
import org.joml.Vector3d
import org.valkyrienskies.core.api.LoadedServerShipCore
import org.valkyrienskies.core.api.ServerShipCore
import org.valkyrienskies.core.api.ServerShipUser
import org.valkyrienskies.core.api.ShipForcesInducer
import org.valkyrienskies.core.api.Ticked
import org.valkyrienskies.core.networking.delta.DeltaEncodedChannelServerTCP
import org.valkyrienskies.core.util.serialization.VSJacksonUtil

class ShipObjectServer(
    override val shipData: ShipData
) : ShipObject(shipData), LoadedServerShipCore, ServerShipCore by shipData {

    internal val shipDataChannel = DeltaEncodedChannelServerTCP(
        jsonDiffDeltaAlgorithm,
        VSJacksonUtil.deltaMapper.valueToTree(shipData)
    )

    // runtime attached data only server-side, cus syncing to clients would be pain
    internal val attachedData = MutableClassToInstanceMap.create<Any>()
    internal val forceInducers = mutableListOf<ShipForcesInducer>()
    internal val toBeTicked = mutableListOf<Ticked>()

    init {
        for (data in shipData.persistentAttachedData) {
            applyAttachmentInterfaces(data.key, data.value)
        }
    }

    override fun <T> setAttachment(clazz: Class<T>, value: T?) {
        if (value == null) {
            attachedData.remove(clazz)
        } else {
            attachedData[clazz] = value
        }

        applyAttachmentInterfaces(clazz, value)
    }

    override fun <T> getAttachment(clazz: Class<T>): T? =
        attachedData.getInstance(clazz) ?: shipData.getAttachment(clazz)

    override fun <T> saveAttachment(clazz: Class<T>, value: T?) {
        applyAttachmentInterfaces(clazz, value)

        shipData.saveAttachment(clazz, value)
    }

    private fun applyAttachmentInterfaces(clazz: Class<*>, value: Any?) {
        if (value == null) {
            forceInducers.removeIf { clazz.isAssignableFrom(it::class.java) }
            toBeTicked.removeIf { clazz.isAssignableFrom(it::class.java) }
        } else {
            if (value is ShipForcesInducer) {
                forceInducers.add(value)
            }
            if (value is ServerShipUser) {
                value.ship = this
            }
            if (value is Ticked) {
                toBeTicked.add(value)
            }
        }
    }

    /**
     * This will be implemented in the future for portals, but for now we just return 0 for all positions
     */
    fun getSegmentId(localPos: Vector3d): Int = 0
}