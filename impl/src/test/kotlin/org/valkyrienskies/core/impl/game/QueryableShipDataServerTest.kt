package org.valkyrienskies.core.impl.game

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.property.Arb
import io.kotest.property.checkAll
import org.junit.jupiter.api.Assertions.*
import org.valkyrienskies.core.impl.VSRandomUtils
import org.valkyrienskies.core.impl.game.ships.QueryableShipDataImpl
import org.valkyrienskies.core.impl.game.ships.QueryableShipDataServer
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.test_utils.generators.shipData
import kotlin.random.Random

class QueryableShipDataServerTest : AnnotationSpec() {

    /**
     * Tests getting [ShipData] from [java.util.UUID].
     */
    @Test
    fun testGetShipFromUUID() {
        val queryableShipData = QueryableShipDataImpl<ShipData>()
        val shipData = VSRandomUtils.randomShipData()
        queryableShipData.addShipData(shipData)
        assertEquals(shipData, queryableShipData.getById(shipData.id))
    }

    /**
     * Tests getting [ShipData] from [ChunkClaim].
     */
    @Test
    fun testGetShipFromChunkClaim() {
        val queryableShipData = QueryableShipDataImpl<ShipData>()
        val shipData = VSRandomUtils.randomShipData()
        queryableShipData.addShipData(shipData)
        val shipChunkClaim = shipData.chunkClaim

        // Test chunks inside of the claim
        for (count in 1..1000) {
            val chunkX = Random.nextInt(shipChunkClaim.xStart, shipChunkClaim.xEnd + 1)
            val chunkZ = Random.nextInt(shipChunkClaim.zStart, shipChunkClaim.zEnd + 1)
            assertEquals(
                shipData,
                queryableShipData.getShipDataFromChunkPos(chunkX, chunkZ, shipData.chunkClaimDimension)
            )
        }

        // Test chunks outside of the claim
        for (count in 1..1000) {
            val chunkX = if (Random.nextBoolean()) {
                shipChunkClaim.xStart - Random.nextInt(1, 1000)
            } else {
                shipChunkClaim.xEnd + Random.nextInt(1, 1000)
            }
            val chunkZ = if (Random.nextBoolean()) {
                shipChunkClaim.zStart - Random.nextInt(1, 1000)
            } else {
                shipChunkClaim.zEnd + Random.nextInt(1, 1000)
            }
            assertEquals(null, queryableShipData.getShipDataFromChunkPos(chunkX, chunkZ, shipData.chunkClaimDimension))
        }

        // Test more chunks outside of the claim
        assertEquals(
            null,
            queryableShipData.getShipDataFromChunkPos(
                shipChunkClaim.xStart,
                shipChunkClaim.zStart - 1,
                shipData.chunkClaimDimension
            )
        )
        assertEquals(
            null,
            queryableShipData.getShipDataFromChunkPos(
                shipChunkClaim.xStart - 1,
                shipChunkClaim.zStart,
                shipData.chunkClaimDimension
            )
        )
        assertEquals(
            null,
            queryableShipData.getShipDataFromChunkPos(
                shipChunkClaim.xEnd,
                shipChunkClaim.zEnd + 1,
                shipData.chunkClaimDimension
            )
        )
        assertEquals(
            null,
            queryableShipData.getShipDataFromChunkPos(
                shipChunkClaim.xEnd + 1,
                shipChunkClaim.zEnd,
                shipData.chunkClaimDimension
            )
        )
    }

    /**
     * Test adding duplicate [ShipData].
     */
    @Test
    fun testAddDuplicateShip() {
        val queryableShipData = QueryableShipDataImpl<ShipData>()
        val shipData = VSRandomUtils.randomShipData()
        queryableShipData.addShipData(shipData)
        assertThrows(IllegalArgumentException::class.java) {
            queryableShipData.addShipData(shipData)
        }
    }

    /**
     * Test removing [ShipData] in [QueryableShipDataServer].
     */
    @Test
    suspend fun testRemovingShipNotInQueryableShipData() {
        checkAll(Arb.shipData(), Arb.shipData()) { shipData, otherShipData ->
            val queryableShipData = QueryableShipDataImpl<ShipData>()
            queryableShipData.addShipData(shipData)
            assertThrows(IllegalArgumentException::class.java) {
                queryableShipData.removeShipData(otherShipData)
            }
        }
    }

    /**
     * Test getting a [ShipData] by its [org.joml.primitives.AABBdc]
     */
    @Test
    suspend fun testGettingShipByBoundingBox() {
        checkAll(Arb.shipData()) { shipData ->
            val queryableShipData = QueryableShipDataImpl<ShipData>()
            queryableShipData.addShipData(shipData)
            val shipsIntersectingBB = queryableShipData.getShipDataIntersecting(shipData.worldAABB).iterator()
            assertTrue(shipsIntersectingBB.hasNext())
            assertEquals(shipsIntersectingBB.next(), shipData)
            assertFalse(shipsIntersectingBB.hasNext())
        }
    }
}
