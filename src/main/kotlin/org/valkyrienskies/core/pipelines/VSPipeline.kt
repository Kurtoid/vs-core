package org.valkyrienskies.core.pipelines

import dagger.Subcomponent
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.config.VSCoreConfig
import org.valkyrienskies.core.game.ships.SerializedShipDataModule
import org.valkyrienskies.core.game.ships.ShipObjectServerWorld
import org.valkyrienskies.core.hooks.AbstractCoreHooks
import org.valkyrienskies.core.util.WorldScoped
import javax.inject.Inject
import kotlin.concurrent.thread

@WorldScoped
@Subcomponent(modules = [SerializedShipDataModule::class])
interface VSPipelineComponent {
    fun newPipeline(): VSPipeline

    @Subcomponent.Factory
    interface Factory {
        fun newPipelineComponent(module: SerializedShipDataModule): VSPipelineComponent
    }
}

/**
 * A pipeline that moves data between the game, the physics, and the network stages.
 *
 * The Game stage sends [VSGameFrame]s to the Physics stage.
 *
 * The Physics stage sends [VSPhysicsFrame]s to the Game stage and to the Network stage.
 *
 * Game <--> Physics --> Network
 */
@WorldScoped
class VSPipeline @Inject constructor(
    val shipWorld: ShipObjectServerWorld,
    private val gameStage: VSGamePipelineStage,
    private val physicsStage: VSPhysicsPipelineStage,
    private val networkStage: VSNetworkPipelineStage,
    hooks: AbstractCoreHooks,
) {
    var synchronizePhysics = VSCoreConfig.SERVER.pt.synchronizePhysics
        private set

    private val physicsPipelineBackgroundTask: VSPhysicsPipelineBackgroundTask = VSPhysicsPipelineBackgroundTask(this)

    // start paused on client, start unpaused on server
    @Volatile
    var arePhysicsRunning = !hooks.isPhysicalClient

    // The thread the physics engine runs on
    private val physicsThread: Thread = thread(start = true, priority = 8, name = "Physics thread") {
        physicsPipelineBackgroundTask.run()
    }

    var deleteResources = false

    fun preTickGame() {
        synchronizePhysics = VSCoreConfig.SERVER.pt.synchronizePhysics
        if (synchronizePhysics) {
            physicsPipelineBackgroundTask.lock.lock()

            val physicsTicksPerGameTick = VSCoreConfig.SERVER.pt.physicsTicksPerGameTick

            // in the event the physics thread hasn't produced all the required ticks yet,
            // wait until it does
            while (physicsPipelineBackgroundTask.physicsTicksSinceLastGameTick <= physicsTicksPerGameTick)
                physicsPipelineBackgroundTask.shouldRunGameTick.await()
        }

        gameStage.preTickGame()
    }

    fun postTickGame() {
        val gameFrame = gameStage.postTickGame()
        physicsStage.pushGameFrame(gameFrame)

        if (synchronizePhysics) {
            // indicate the game tick has completed and signal the physics thread
            physicsPipelineBackgroundTask.physicsTicksSinceLastGameTick = 0
            physicsPipelineBackgroundTask.shouldRunPhysicsTick.signal()
        }
    }

    fun tickPhysics(gravity: Vector3dc, timeStep: Double) {
        if (deleteResources) {
            physicsStage.deleteResources()
            physicsPipelineBackgroundTask.tellTaskToKillItself()
            return
        }

        val physicsFrame = physicsStage.tickPhysics(gravity, timeStep, arePhysicsRunning)
        gameStage.pushPhysicsFrame(physicsFrame)
        networkStage.pushPhysicsFrame(physicsFrame)
    }

    fun getPhysicsGravity(): Vector3dc {
        return Vector3d(0.0, -10.0, 0.0)
    }

    fun computePhysTps(): Double {
        return physicsPipelineBackgroundTask.computePhysicsTPS()
    }
}
