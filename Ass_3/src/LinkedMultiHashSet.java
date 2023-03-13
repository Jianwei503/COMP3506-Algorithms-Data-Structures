import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * LinkedMultiHashSet is an implementation of a (@see MultiSet), using a hashtable as the internal
 * data structure, and with predictable iteration order based on the insertion order
 * of elements.
 * 
 * Its iterator orders elements according to when the first occurrence of the element 
 * was added. When the multiset contains multiple instances of an element, those instances 
 * are consecutive in the iteration order. If all occurrences of an element are removed,
 * after which that element is added to the multiset, the element will appear at the end of the
 * iteration.
 * 
 * The internal hashtable array should be doubled in size after an add that would cause it to be
 * at full capacity. The internal capacity should never decrease.
 * 
 * Collision handling for elements with the same hashcode (i.e. with hashCode()) should be done
 * using linear probing, as described in lectures.
 * 
 * @param <T> type of elements in the set
 */
public class LinkedMultiHashSet<T> implements MultiSet<T>, Iterable<T> {
    // The maximum capacity, used if a higher value is
    // implicitly specified by constructor with argument.
    private static final int MAX_CAPACITY = 1 << 30;

    // total count of all elements in the multiset
    // duplicates of an element all contribute to the count
    private int elementCount;

    // number of distinct elements currently stored in the set
    private int entryCount;

    // maximum number of distinct elements the internal data
    private int capacity;

    // resizable array to contain elements
    private Entry<T>[] array;

    // pointers that point to the elements 1st and last added
    private Entry<T> head, tail;

    public LinkedMultiHashSet(int initialCapacity) {
        capacity = initialCapacity;
        elementCount = entryCount = 0;
        head = tail = null;
        array = new Entry[initialCapacity];
    }

    @Override
    // worst case time complexity: O(n)  space complexity: O(1)
    public void add(T element) {
        if (element == null) {
            return;
        }
        Entry<T> newEntry = new Entry<>(element, 1);
        if (entryCount == 0 && head == null) {
            head = tail = newEntry;
        }
        int index = hashValue(element);
        while (array[index] != null && !array[index].equalsTo(newEntry)) {
            ++index;
            index = index % capacity;
        }
        if (array[index] == null) {
            array[index] = newEntry;
            ++elementCount;
            ++entryCount;
            if (!newEntry.equalsTo(tail)) {
                tail.setNext(newEntry);
                newEntry.setPrev(tail);
                tail = newEntry;
            }
        } else {
            array[index].addCount(1);
            ++elementCount;
        }
        if (isFull()) {
            expandArray();
        }

    }

    @Override
    // worst case time complexity: O(n)  space complexity: O(1)
    public void add(T element, int count) {
        if (element == null || count <= 0) {
            return;
        }
        Entry<T> newEntry = new Entry<>(element, count);
        if (entryCount == 0 && head == null) {
            head = tail = newEntry;
        }
        int index = hashValue(element);
        while (array[index] != null && !array[index].equalsTo(newEntry)) {
            ++index;
            index = index % capacity;
        }
        if (array[index] == null) {
            array[index] = newEntry;
            elementCount += count;
            ++entryCount;
            if (!newEntry.equalsTo(tail)) {
                tail.setNext(newEntry);
                newEntry.setPrev(tail);
                tail = newEntry;
            }
        } else {
            array[index].addCount(count);
            ++elementCount;
        }
        if (isFull()) {
            expandArray();
        }
    }

    @Override
    // worst case time complexity: O(n)  space complexity: O(1)
    public boolean contains(T element) {
        int index = hashValue(element);
        while (array[index] != null) {
            if (array[index].getValue() == element) {
                return true;
            }
            ++index;
            index = index % capacity;
        }
        return false;
    }

    @Override
    // worst case time complexity: O(n)  space complexity: O(1)
    public int count(T element) {
        int index = hashValue(element);
        while (array[index] != null) {
            if (array[index].getValue() == element) {
                return array[index].getCount();
            }
            ++index;
            index = index % capacity;
        }
        return 0;
    }

    @Override
    // worst case time complexity: O(n)  space complexity: O(1)
    public void remove(T element) throws NoSuchElementException {
        if (element == null) {
            throw new NoSuchElementException();
        }
        int index = hashValue(element);
        while (array[index] != null) {
            if (array[index].getValue() == element) {
                array[index].minusCount(1);
                --elementCount;
                if (array[index].getCount() == 0) {
                    Entry<T> prev = array[index].getPrev();
                    Entry<T> next = array[index].getNext();
                    if (prev != null && next != null) {
                        prev.setNext(next);
                        next.setPrev(prev);
                    } else if (prev == null && next != null) {
                        next.setPrev(null);
                        head = next;
                    } else if (prev != null && next == null) {
                        prev.setNext(null);
                        tail = prev;
                    } else {
                        head = tail = null;
                    }
                    array[index] = null;
                    --entryCount;
                }
                return;
            }
            ++index;
            index = index % capacity;
        }
        throw new NoSuchElementException();
    }

    @Override
    // worst case time complexity: O(n)  space complexity: O(1)
    public void remove(T element, int count) throws NoSuchElementException {
        if (element == null) {
            throw new NoSuchElementException();
        }
        if (count < 0) {
            return;
        }
        int index = hashValue(element);
        while (array[index] != null) {
            if (array[index].getValue() == element) {
                if (array[index].getCount() < count) {
                    throw new NoSuchElementException();
                }
                array[index].minusCount(count);
                elementCount -= count;
                if (array[index].getCount() == 0) {
                    Entry<T> prev = array[index].getPrev();
                    Entry<T> next = array[index].getNext();
                    if (prev != null && next != null) {
                        prev.setNext(next);
                        next.setPrev(prev);
                    } else if (prev == null && next != null) {
                        next.setPrev(null);
                        head = next;
                    } else if (prev != null && next == null) {
                        prev.setNext(null);
                        tail = prev;
                    } else {
                        head = tail = null;
                    }
                    array[index] = null;
                    --entryCount;
                }
                return;
            }
            ++index;
            index = index % capacity;
        }
        throw new NoSuchElementException();
    }

    @Override
    // worst case time complexity: O(1)  space complexity: O(1)
    public int size() {
        return elementCount;
    }

    @Override
    // worst case time complexity: O(1)  space complexity: O(1)
    public int internalCapacity() {
        return capacity;
    }

    @Override
    // worst case time complexity: O(1)  space complexity: O(1)
    public int distinctCount() {
        return entryCount;
    }

    @Override
    // worst case time complexity: O(1)  space complexity: O(1)
    public Iterator<T> iterator() {
        return new LinkedMultiHashSetIterator();
    }

    /**
     * Calculates a unique hash code for a given element.
     *
     * time complexity: O(1)
     * space complexity: O(1)
     *
     * @param value the given element to be hashed
     * @return a unique hash code
     */
    private int hashValue(T value) {
        return Objects.hashCode(value) % capacity;
    }

    /**
     * Checks whether the array which contains the elements is full or not.
     *
     * time complexity: O(1)
     * space complexity: O(1)
     *
     * @return ture if the array is full, false otherwise
     */
    private boolean isFull() {
        return entryCount == capacity;
    }

    /**
     * Doubles the size of array when it is full.
     *
     * time complexity: O(n)
     * space complexity: O(1)
     */
    private void expandArray() {
        int oldArraySize = capacity;
        capacity *= 2;
        elementCount = entryCount = 0;
        Entry[] oldArray = array;
        array = new Entry[capacity];
        for (int i = 0; i < oldArraySize; ++i) {
            if (oldArray[i] != null) {
                add((T) oldArray[i].getValue(), oldArray[i].getCount());
            }
        }
    }

    /**
     * A hashset entry, the elements contained in array of LinkedMultiHashSet
     * is the instances of this class.
     */
    private static class Entry<T> {
        T value;    // the actual value of element
        int count;  // number of duplicate occurrences of an element
        Entry<T> prev; // entry instance inserted before this entry
        Entry<T> next; // entry instance inserted after this entry

        /**
         * A constructor of Entry class.
         * @param value actual value of an element
         * @param count number of duplicate occurrences of an element
         */
        private Entry(T value, int count) {
            this.value = value;
            this.count = count;
            this.next = null;
            this.prev = null;
        }

        /**
         * Returns the value corresponding to this entry.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @return the value corresponding to this entry
         */
        private T getValue() {
            return value;
        }

        /**
         * Returns the number of duplicate occurrences of this element.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @return the number of duplicate occurrences of this element
         */
        private int getCount() {
            return count;
        }

        /**
         * Returns entry instance inserted after this entry.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @return entry instance inserted after this entry
         */
        private Entry<T> getNext() {
            return next;
        }

        /**
         * Returns entry instance inserted before this entry.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @return entry instance inserted before this entry
         */
        private Entry<T> getPrev() {
            return prev;
        }

        /**
         * Adds given number to the number of duplicate occurrences
         * of this element.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @param number given number to be added
         */
        private void addCount(int number) {
            if (number == 1) {
                ++count;
            } else {
                count += number;
            }
        }

        /**
         * Subtracts given number from the number of duplicate occurrences
         * of this element.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @param number given number to be subtracted
         */
        private void minusCount(int number) {
            if (number == 1) {
                --count;
            } else {
                count -= number;
            }
        }

        /**
         * Sets the next entry instance to be the given entry instance.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @param nextEntry given entry instance to be set
         */
        private void setNext(Entry<T> nextEntry) {
            next = nextEntry;
        }

        /**
         * Sets the previous entry instance to be the given entry instance.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @param prevEntry given entry instance to be set
         */
        private void setPrev(Entry<T> prevEntry) {
            prev = prevEntry;
        }

        /**
         * Checks whether a given Entry instance is equals to this Entry instance.
         *
         * time complexity: O(1)
         * space complexity: O(1)
         *
         * @param o the given Entry instance
         * @return true if they are equal, false otherwise
         */
        private boolean equalsTo(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof LinkedMultiHashSet.Entry) {
                return this.value == ((Entry<?>) o).value;
            }
            return false;
        }
    }

    /**
     * A iterator class of LinkedMultiHashSet
     */
    private class LinkedMultiHashSetIterator implements Iterator<T> {
        private Entry<T> next; // the next Entry instance
        int elementCount;      // number of duplicate occurrences of an element

        LinkedMultiHashSetIterator() {
            next = head;
            elementCount = head != null ? head.getCount() : 0;
        }

        @Override
        // worst case time complexity: O(1)  space complexity: O(1)
        public boolean hasNext() {
            return next != null;
        }

        @Override
        // worst case time complexity: O(1)  space complexity: O(1)
        public T next() {
            T element = next.getValue();
            --elementCount;
            if (elementCount == 0) {
                next = next.getNext();
                elementCount = next != null ? next.getCount() : 0;
            }
            return element;
        }
    }
}