package org.valkyrienskies.core.datastructures

import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.util.iterateBits
import org.valkyrienskies.core.util.unwrapIndex
import org.valkyrienskies.core.util.wrapIndex
import kotlin.experimental.and
import kotlin.experimental.or

class SingleChunkDenseBlockPosSet {

    val data: ByteArray = ByteArray(512)

    companion object {
        val dimensions: Vector3ic = Vector3i(16, 16, 16)

        private fun Byte.isBitSet(bitIndex: Int): Boolean {
            return this and (1 shl bitIndex).toByte() != 0.toByte()
        }
    }

    inline fun forEach(fn: (Int, Int, Int) -> Unit) {
        data.forEachIndexed { index, byte ->
            byte.iterateBits { isSet, bitIndex ->
                if (isSet) {
                    unwrapIndex(index * 8 + bitIndex, dimensions) { x, y, z ->
                        fn(x, y, z)
                    }
                }
            }
        }
    }

    fun remove(x: Int, y: Int, z: Int): Boolean {
        require(x < dimensions.x() && x >= 0 && y < dimensions.y() && y >= 0 && z < dimensions.z() && z >= 0) {
            "Block coordinates must be within the bounds of the chunk"
        }
        val index = wrapIndex(x, y, z, dimensions)
        val realIndex = index / 8
        val offset = index % 8

        val prev = data[realIndex]
        data[realIndex] = prev and ((1 shl offset).inv().toByte())
        return prev.isBitSet(offset)
    }

    fun add(x: Int, y: Int, z: Int): Boolean {
        require(x < dimensions.x() && x >= 0 && y < dimensions.y() && y >= 0 && z < dimensions.z() && z >= 0) {
            "Block coordinates must be within the bounds of the chunk"
        }
        val index = wrapIndex(x, y, z, dimensions)
        val realIndex = index / 8
        val offset = index % 8

        val prev = data[realIndex]
        data[realIndex] = prev or ((1 shl offset).toByte())

        return prev.isBitSet(offset)
    }

    fun contains(x: Int, y: Int, z: Int): Boolean {
        val index = wrapIndex(x, y, z, dimensions)
        val realIndex = index / 8
        val offset = index % 8

        return data[realIndex].isBitSet(offset)
    }
}

