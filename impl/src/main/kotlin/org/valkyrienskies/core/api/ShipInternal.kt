package org.valkyrienskies.core.api

import org.valkyrienskies.core.api.ships.ShipCore
import org.valkyrienskies.core.api.world.chunks.BlockType

/**
 * Abstraction of a ship, there are many types such as offline ships
 *  or loaded ships so this is the generic interface for all ships.
 */
interface ShipInternal : ShipCore {

    fun onSetBlock(
        posX: Int,
        posY: Int,
        posZ: Int,
        oldBlockType: BlockType,
        newBlockType: BlockType,
        oldBlockMass: Double,
        newBlockMass: Double
    )
}
