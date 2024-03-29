package org.valkyrienskies.core.impl.collision

import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConvexPolygonTest {

    @Test
    fun getProjectionAlongAxis() {
        val points = listOf(
            Vector3d(0.0, 0.0, 0.0),
            Vector3d(0.0, 0.0, 1.0),
            Vector3d(0.0, 1.0, 0.0),
            Vector3d(0.0, 1.0, 1.0),
            Vector3d(1.0, 0.0, 0.0),
            Vector3d(1.0, 0.0, 1.0),
            Vector3d(1.0, 1.0, 0.0),
            Vector3d(1.0, 1.0, 1.0)
        )
        val normals = listOf(
            Vector3d(1.0, 0.0, 0.0),
            Vector3d(0.0, 1.0, 0.0),
            Vector3d(0.0, 0.0, 1.0)
        )
        val convexPolygonImpl: ConvexPolygonc = ConvexPolygon.createFromPointsAndNormals(
            points, normals, AABBd(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        )
        val testedNormal: Vector3dc = Vector3d(0.0, 1.0, 0.0)

        val overlappingRange: CollisionRange = CollisionRange.create()

        convexPolygonImpl.getProjectionAlongAxis(testedNormal, overlappingRange)

        assertEquals(0.0, overlappingRange.min)
        assertEquals(1.0, overlappingRange.max)
    }
}
