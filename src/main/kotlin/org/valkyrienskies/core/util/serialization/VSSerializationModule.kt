package org.valkyrienskies.core.util.serialization

import com.fasterxml.jackson.databind.module.SimpleModule
import org.valkyrienskies.core.chunk_tracking.IShipActiveChunksSet
import org.valkyrienskies.core.chunk_tracking.ShipActiveChunksSet
import org.valkyrienskies.core.datastructures.BlockPosSetAABBGenerator
import org.valkyrienskies.core.datastructures.IBlockPosSet
import org.valkyrienskies.core.datastructures.IBlockPosSetAABB
import org.valkyrienskies.core.datastructures.SmallBlockPosSet

internal class VSSerializationModule : SimpleModule() {
    init {
        addAbstractTypeMapping<IBlockPosSet, SmallBlockPosSet>()
        addAbstractTypeMapping<IBlockPosSetAABB, BlockPosSetAABBGenerator>()
        addAbstractTypeMapping<IShipActiveChunksSet, ShipActiveChunksSet>()
    }
}
