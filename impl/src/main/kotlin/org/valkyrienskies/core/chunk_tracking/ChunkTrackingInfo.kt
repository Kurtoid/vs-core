package org.valkyrienskies.core.chunk_tracking

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.LongSet
import org.valkyrienskies.core.api.ServerShipInternal
import org.valkyrienskies.core.api.world.IPlayer

/**
 * A class containing the result of the chunk tracking. **This object is only valid for the tick it was produced in!**
 * Many of the maps/sets will be reused for efficiency's sake.
 */
data class ChunkTrackingInfo(
    val playersToShipsWatchingMap: Map<IPlayer, Map<ServerShipInternal, LongSet>>,
    val shipsToPlayersWatchingMap: Long2ObjectMap<MutableSet<IPlayer>>,
    val playersToShipsNewlyWatchingMap: Map<IPlayer, Set<ServerShipInternal>>,
    val playersToShipsNoLongerWatchingMap: Map<IPlayer, Set<ServerShipInternal>>,
    val shipsToLoad: Set<ServerShipInternal>,
    val shipsToUnload: Set<ServerShipInternal>,
) {
    fun getShipsPlayerIsWatching(player: IPlayer): Iterable<ServerShipInternal> {
        return (playersToShipsWatchingMap[player] ?: emptyMap()).keys
    }
}
