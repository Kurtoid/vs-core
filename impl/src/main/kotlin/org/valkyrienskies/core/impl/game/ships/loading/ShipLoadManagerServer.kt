package org.valkyrienskies.core.impl.game.ships.loading

import org.valkyrienskies.core.apigame.world.properties.DimensionId
import org.valkyrienskies.core.apigame.world.IPlayer
import org.valkyrienskies.core.apigame.world.chunks.ChunkUnwatchTask
import org.valkyrienskies.core.apigame.world.chunks.ChunkWatchTask
import org.valkyrienskies.core.apigame.world.chunks.ChunkWatchTasks
import org.valkyrienskies.core.impl.chunk_tracking.ShipObjectServerWorldChunkTracker
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.loading.ShipLoadManagerServer.Stages.*
import org.valkyrienskies.core.impl.game.ships.networking.ShipObjectNetworkManagerServer
import org.valkyrienskies.core.impl.util.assertions.stages.TickStageEnforcer
import java.util.*
import javax.inject.Inject

class ShipLoadManagerServer @Inject constructor(
    private val networkManager: ShipObjectNetworkManagerServer,
    private val tracker: ShipObjectServerWorldChunkTracker,
    private val allocators: ChunkAllocatorProvider
) {
    private enum class Stages {
        PRE_TICK, SET_EXECUTED, POST_TICK
    }

    private val stageEnforcer = TickStageEnforcer(PRE_TICK) {
        requireStagesAndOrder(*Stages.values())
    }

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
        stageEnforcer.stage(PRE_TICK)

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
        if (allocators.forDimension(dimensionId).isChunkInShipyard(chunkX, chunkZ)) {
            return tracker.getPlayersWatchingChunk(chunkX, chunkZ, dimensionId).iterator()
        }
        return Collections.emptyIterator()
    }
}
