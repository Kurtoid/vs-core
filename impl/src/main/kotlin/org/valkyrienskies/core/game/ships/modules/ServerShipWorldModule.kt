package org.valkyrienskies.core.game.ships.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.valkyrienskies.core.game.ChunkAllocator
import org.valkyrienskies.core.game.ChunkAllocatorProvider
import org.valkyrienskies.core.game.SingletonChunkAllocatorProviderImpl
import org.valkyrienskies.core.game.ships.MutableQueryableShipDataServer
import org.valkyrienskies.core.game.ships.QueryableShipDataImpl
import org.valkyrienskies.core.util.WorldScoped
import javax.inject.Named
import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Retention(BINARY)
@Qualifier
annotation class AllShips

/**
 * Creates the necessary dependency graph for the VSPipeline subcomponent, aka the stuff that each individual world
 * (save file, not dimension) needs. [allShips] are the existing ships in the world, and
 * [chunkAllocator] is the existing chunk allocator for the world.
 */
@Module
class ShipWorldModule(
    @get:Provides @get:WorldScoped @get:AllShips val allShips: MutableQueryableShipDataServer,
    @get:Provides @get:WorldScoped @get:Named("primary") val chunkAllocator: ChunkAllocator
) {

    @Module
    interface Declarations {
        @Binds
        fun chunkAllocatorProvider(impl: SingletonChunkAllocatorProviderImpl): ChunkAllocatorProvider
    }

    companion object {
        fun createEmpty() = ShipWorldModule(
            allShips = QueryableShipDataImpl(),
            chunkAllocator = ChunkAllocator.create()
        )
    }
}
