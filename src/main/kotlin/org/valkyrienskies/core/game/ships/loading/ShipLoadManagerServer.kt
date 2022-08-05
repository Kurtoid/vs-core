package org.valkyrienskies.core.game.ships.loading

import org.valkyrienskies.core.chunk_tracking.ChunkUnwatchTask
import org.valkyrienskies.core.chunk_tracking.ChunkWatchTask
import org.valkyrienskies.core.chunk_tracking.ChunkWatchTasks
import org.valkyrienskies.core.chunk_tracking.ShipObjectServerWorldChunkTracker
import org.valkyrienskies.core.game.ChunkAllocator
import org.valkyrienskies.core.game.DimensionId
import org.valkyrienskies.core.game.IPlayer
import org.valkyrienskies.core.game.ships.ShipData
import org.valkyrienskies.core.game.ships.loading.ShipLoadManagerServer.Stages.POST_TICK
import org.valkyrienskies.core.game.ships.loading.ShipLoadManagerServer.Stages.PRE_TICK
import org.valkyrienskies.core.game.ships.loading.ShipLoadManagerServer.Stages.SET_EXECUTED
import org.valkyrienskies.core.game.ships.networking.ShipObjectNetworkManagerServer
import org.valkyrienskies.core.util.assertions.TickStageEnforcer
import org.valkyrienskies.core.util.assertions.TickStageEnforcer.Constraints
import java.util.Collections
import javax.inject.Inject

class ShipLoadManagerServer @Inject internal constructor(
    private val networkManager: ShipObjectNetworkManagerServer,
    private val tracker: ShipObjectServerWorldChunkTracker
) {
    private enum class Stages {
        PRE_TICK, SET_EXECUTED, POST_TICK
    }

    private val stageEnforcer = TickStageEnforcer(
        Constraints.requireStagesAndOrder(*Stages.values())
    )

    private lateinit var executedChunkWatchTasks: Iterable<ChunkWatchTask>
    private lateinit var executedChunkUnwatchTasks: Iterable<ChunkUnwatchTask>

    val playersToTrackedShips by networkManager::playersToTrackedShips

    lateinit var chunkWatchTasks: ChunkWatchTasks
        private set

    fun preTick(
        players: Set<IPlayer>,
        lastTickPlayers: Set<IPlayer>,
        ships: Iterable<ShipData>,
        deletedShips: Iterable<ShipData>
    ) {
        stageEnforcer.stage(PRE_TICK, true)

        chunkWatchTasks = tracker.generateChunkWatchTasksAndUpdatePlayers(players, lastTickPlayers, ships, deletedShips)

        // todo queue ship load/unloads
    }

    fun postTick(players: Set<IPlayer>) {
        stageEnforcer.stage(POST_TICK)

        val trackingInfo = tracker.applyTasksAndGenerateTrackingInfo(executedChunkWatchTasks, executedChunkUnwatchTasks)

        networkManager.tick(players, trackingInfo)
    }

    fun setExecutedChunkWatchTasks(watchTasks: Iterable<ChunkWatchTask>, unwatchTasks: Iterable<ChunkUnwatchTask>) {
        stageEnforcer.stage(SET_EXECUTED)

        executedChunkWatchTasks = watchTasks
        executedChunkUnwatchTasks = unwatchTasks
    }

    fun getIPlayersWatchingShipChunk(chunkX: Int, chunkZ: Int, dimensionId: DimensionId): Iterator<IPlayer> {
        // Check if this chunk potentially belongs to a ship
        if (ChunkAllocator.isChunkInShipyard(chunkX, chunkZ)) {
            return tracker.getPlayersWatchingChunk(chunkX, chunkZ, dimensionId).iterator()
        }
        return Collections.emptyIterator()
    }
}
