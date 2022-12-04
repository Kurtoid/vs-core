package org.valkyrienskies.core.impl.datastructures;

/**
 * This is effectively a Map&lt;Integer, Integer&gt; with all the entries stored as "Nodes" in a LinkedList. To get O(1)
 * runtime, these LinkedList nodes are stored in an array.
 *
 * <p>Unlike a Map&lt;Integer, Integer&gt;, we are only allowed to increment/decrement the value of a given key. Values
 * below 0 are not allowed.
 *
 * <p>We also cannot directly view the value of a key, we can only get the minimum and maximum keys that have non-zero
 * values.</p>
 */
public class FastMinMaxMap {

    /**
     * The "Node struct" is defined as follows:
     * <pre>
     * struct Node {
     *     unsigned int value;
     *     Node* prev, next;
     * }
     * </pre>
     * However, since this is Java not C we emulate this behavior as 3 integers in an int[] array.
     */
    private final int[] backing;
    private final int capacity;
    private int front;
    private int back;
    private int size;

    /**
     * @param capacity The capacity of this map.
     */
    public FastMinMaxMap(final int capacity) {
        this.backing = new int[capacity * 3];
        this.capacity = capacity;
        this.front = -1;
        this.back = -1;
        this.size = 0;
        clear();
    }

    public void increment(final int key) throws IllegalArgumentException {
        final int curValue = getValue(key);
        // Update the pointers
        if (size == 0) {
            front = back = key;
            setPrev(key, -1);
            setNext(key, -1);
        } else if (curValue == 0) {
            if (key < front) {
                setPrev(front, key);
                setNext(key, front);
                front = key;
            } else if (key > back) {
                setNext(back, key);
                setPrev(key, back);
                back = key;
            } else {
                // Unfortunately this isn't O(1)
                int leftKey = -1;
                for (int i = key - 1; i >= 0; i--) {
                    if (getValue(i) != 0) {
                        leftKey = i;
                        break;
                    }
                }
                final int leftsNext = getNext(leftKey);
                setNext(key, leftsNext);
                setPrev(key, leftKey);

                setNext(leftKey, key);
                setPrev(leftsNext, key);
            }
        }
        // Update the value
        setValue(key, curValue + 1);
        size++;
    }

    public void decrement(final int key) throws IllegalArgumentException {
        if (size <= 0) {
            throw new IllegalArgumentException("Cannot decrement when list is empty");
        }
        final int curValue = getValue(key);
        if (curValue <= 0) {
            throw new IllegalArgumentException("Cannot store negative values");
        } else if (curValue == 1) {
            // Update pointers
            if (size == 1) {
                // This is now empty, make the pointers correct
                setNext(key, -1);
                setPrev(key, -1);
                front = -1;
                back = -1;
            } else if (key == front) {
                final int frontNext = getNext(front);
                setNext(front, -1);
                setPrev(frontNext, -1);
                front = frontNext;
            } else if (key == back) {
                final int backPrev = getPrev(back);
                setPrev(back, -1);
                setNext(backPrev, -1);
                back = backPrev;
            } else {
                // Generic case
                final int prevPtr = getPrev(key);
                final int nextPtr = getNext(key);
                setNext(prevPtr, nextPtr);
                setPrev(nextPtr, prevPtr);
            }
            setValue(key, 0);
        } else {
            // Only need to update the value
            setValue(key, curValue - 1);
        }
        size--;
    }

    private void setValue(final int key, final int value) {
        ensureCapacity(key * 3);
        backing[key * 3] = value;
    }

    private void setPrev(final int key, final int prev) {
        ensureCapacity(key * 3 + 1);
        backing[key * 3 + 1] = prev;
    }

    private void setNext(final int key, final int next) {
        ensureCapacity(key * 3 + 2);
        backing[key * 3 + 2] = next;
    }

    private int getValue(final int key) {
        ensureCapacity(key * 3);
        return backing[key * 3];
    }

    private int getPrev(final int key) {
        ensureCapacity(key * 3 + 1);
        return backing[key * 3 + 1];
    }

    private int getNext(final int key) {
        ensureCapacity(key * 3 + 2);
        return backing[key * 3 + 2];
    }

    private void ensureCapacity(final int key) {
        if (key < 0 || key > capacity * 3) {
            throw new IllegalArgumentException("Cannot store key of value " + key);
        }
    }

    public void clear() {
        this.size = 0;
        for (int i = 0; i < capacity; i++) {
            setValue(i, 0);
            setPrev(i, -1);
            setNext(i, -1);
        }
    }

    public int getFront() {
        return front;
    }

    public int getBack() {
        return back;
    }
}
