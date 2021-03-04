package org.valkyrienskies.core.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class VSIterationUtils {

    // region METHODS

    /**
     * Iterates 3d space from the start position (inclusive) to the end position (inclusive). End position must always
     * be greater than start position, otherwise they will be swapped.
     *
     * @param consumer The consumer to call with each iteration
     */
    public static void iterate3d(int startX, int startY, int startZ, int endX, int endY, int endZ,
        final IntTernaryConsumer consumer) {
        // Ensure that the start positions aren't greater than the end positions
        int temp;
        if (startX > endX) {
            temp = startX;
            startX = endX;
            endX = temp;
        }
        if (startY > endY) {
            temp = startY;
            startY = endY;
            endY = temp;
        }
        if (startZ > endZ) {
            temp = startZ;
            startZ = endZ;
            endZ = temp;
        }

        // Iterate
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    consumer.accept(x, y, z);
                }
            }
        }
    }

    /**
     * Iterate every point around one in 3d space.
     *
     * @param consumer The consumer to call with each iteration
     */
    public static void expand3d(final int originX, final int originY, final int originZ,
        final IntTernaryConsumer consumer) {

        expand3d(originX, originY, originZ, 1, consumer);
    }

    /**
     * Iterate every point around one in 3d space.
     *
     * @param toExpand The amount to expand about the origin position
     * @param consumer The consumer to call with each iteration
     */
    public static void expand3d(final int originX, final int originY, final int originZ, final int toExpand,
        final IntTernaryConsumer consumer) {

        iterate3d(originX - toExpand, originY - toExpand, originZ - toExpand,
            originX + toExpand, originY + toExpand, originZ + toExpand, consumer);
    }

    /**
     * Iterates 2d space from the start position (inclusive) to the end position (inclusive). End position must always
     * be greater than start position, otherwise they will be swapped.
     *
     * @param consumer The consumer to call with each iteration
     */
    public static void iterate2d(int startX, int startY, int endX, int endY,
        final IntBinaryConsumer consumer) {

        int temp;
        if (startX > endX) {
            temp = startX;
            startX = endX;
            endX = temp;
        }
        if (startY > endY) {
            temp = startY;
            startY = endY;
            endY = temp;
        }

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                consumer.accept(x, y);
            }
        }
    }

    /**
     * Iterate every point around one in 2d space.
     *
     * @param consumer The consumer to call with each iteration
     */
    public static void expand2d(final int originX, final int originY, final IntBinaryConsumer consumer) {
        expand2d(originX, originY, 1, consumer);
    }

    /**
     * Iterate every point around one in 2d space.
     *
     * @param toExpand The amount to expand about the origin position
     * @param consumer The consumer to call with each iteration
     */
    public static void expand2d(final int originX, final int originY, final int toExpand,
        final IntBinaryConsumer consumer) {
        iterate2d(originX - toExpand, originY - toExpand, originX + toExpand,
            originY + toExpand, consumer);
    }

    // endregion

    // region OVERLOADS

    /**
     * @see #iterate3d(int, int, int, int, int, int, IntTernaryConsumer)
     */
    public static void iterate3d(final int startX, final int startY, final int startZ, final int endX, final int endY,
        final int endZ,
        final Consumer<? super Vector3ic> consumer) {
        iterate3d(startX, startY, startZ, endX, endY, endZ, (x, y, z) ->
            consumer.accept(new Vector3i(x, y, z)));
    }

    // endregion

    // region CLASSES

    /**
     * Iterates 2d space from the start position (inclusive) to the end position (inclusive). End position must always
     * be greater than start position, otherwise they will be swapped.
     */
    public static class Int2dIterator implements Iterator<Vector2i> {

        int index = 0;
        final int startX;
        final int startY;
        final int maxX;
        final int maxY;

        public Int2dIterator(int startX, int startY, int endX, int endY) {
            int temp;
            if (startX > endX) {
                temp = startX;
                startX = endX;
                endX = temp;
            }
            if (startY > endY) {
                temp = startY;
                startY = endY;
                endY = temp;
            }

            this.startX = startX;
            this.startY = startY;
            this.maxX = (endX + 1) - startX;
            this.maxY = (endY + 1) - startY;
        }

        @Override
        public boolean hasNext() {
            return index < maxX * maxY;
        }

        @Override
        public Vector2i next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            final int x = index % maxX;
            final int y = index / maxY;

            index++;

            return new Vector2i(x + startX, y + startY);
        }
    }

    /**
     * Iterates 3d space from the start position (inclusive) to the end position (inclusive). End position must always
     * be greater than start position, otherwise they will be swapped.
     */
    public static class Int3dIterator implements Iterator<Vector3i> {

        int index = 0;
        final int startX;
        final int startY;
        final int startZ;
        final int maxX;
        final int maxY;
        final int maxZ;

        public Int3dIterator(int startX, int startY, int startZ, int endX, int endY, int endZ) {
            int temp;
            if (startX > endX) {
                temp = startX;
                startX = endX;
                endX = temp;
            }
            if (startY > endY) {
                temp = startY;
                startY = endY;
                endY = temp;
            }
            if (startZ > endZ) {
                temp = startZ;
                startZ = endZ;
                endZ = temp;
            }

            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.maxX = (endX + 1) - startX;
            this.maxY = (endY + 1) - startY;
            this.maxZ = (endZ + 1) - startZ;
        }

        @Override
        public boolean hasNext() {
            return index < maxX * maxY * maxZ;
        }

        @Override
        public Vector3i next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            final int x = index % maxX;
            final int y = (index / maxX) % maxY;
            final int z = index / (maxX * maxY);

            index++;

            return new Vector3i(x + startX, y + startY, z + startZ);
        }
    }

    @FunctionalInterface
    public interface IntTernaryConsumer {

        void accept(int x, int y, int z);

    }

    @FunctionalInterface
    public interface IntBinaryConsumer {

        void accept(int x, int y);

    }

    // endregion
}
