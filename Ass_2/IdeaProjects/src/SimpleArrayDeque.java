import java.util.Iterator;
import java.util.NoSuchElementException;

public class SimpleArrayDeque<T> implements SimpleDeque<T> {
    // The array in which the elements of the deque are stored.
    private Object[] elements;
    // The index of the element at the head of the deque, default value is 0.
    private int head;
    // The index at which the next element would be added to the tail of deque,
    // default value of tail is 0.
    private int tail;
    // The default minimum capacity of the deque
    private static final int INITIAL_CAPACITY = 8;

    /**
     * Constructs a new array based deque with limited capacity.
     * 
     * @param capacity the capacity
     * @throws IllegalArgumentException if capacity <= 0
     */
    public SimpleArrayDeque(int capacity) throws IllegalArgumentException {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        elements = new Object[INITIAL_CAPACITY];
    }

    /**
     * Constructs a new array based deque with limited capacity, and initially populates the deque
     * with the elements of another SimpleDeque.
     *
     * @param otherDeque the other deque to copy elements from. otherDeque should be left intact.
     * @param capacity the capacity
     * @throws IllegalArgumentException if capacity <= 0 or size of otherDeque is > capacity
     */
    public SimpleArrayDeque(int capacity, SimpleDeque<? extends T> otherDeque) 
            throws IllegalArgumentException {
        int dequeSize = otherDeque.size();
        if (capacity <= 0 || dequeSize > capacity) {
            throw new IllegalArgumentException();
        }
        elements = new Object[capacity];
        copyElements(otherDeque);
        head = 0;
        tail = dequeSize & (capacity - 1);
    }

    /**
     * Copies all the elements stored in otherDeque to this deque,
     * otherDeque should be left intact.
     *
     * @param otherDeque the other deque to copy elements from
     * time complexity O(n), memory complexity O(1)
     */
    private void copyElements(SimpleDeque<? extends T> otherDeque) {
        Iterator iterator = otherDeque.iterator();
        int initIndex = 0;
        while (iterator.hasNext()) {
            elements[initIndex++] = iterator.next();
        }
    }

    /**
     * Doubles the capacity of this deque.  Call only when full, i.e.,
     * when head and tail have wrapped around to become equal.
     * time complexity O(n), memory complexity O(1)
     */
    private void doubleCapacity() {
        assert head == tail;
        // the max size of the current elements array
        int length = elements.length;
        // number of elements to the right of head index
        int numElements = length - head;
        int newCapacity = length << 1; // double the size of elements array
        if (newCapacity < 0)
            throw new IllegalStateException("Deque is too big.");
        Object[] newElements = new Object[newCapacity];
        System.arraycopy(elements, head, newElements, 0, numElements);
        System.arraycopy(elements, 0, newElements, numElements, head);
        elements = newElements;
        head = 0;
        tail = length;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public boolean isEmpty() {
        return head == tail;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public boolean isFull() {
        return (head == tail) && (elements[0] != null);
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public void pushLeft(T e) throws RuntimeException {
        if (isFull()) {
            throw new RuntimeException();
        }
        // head index moves left 1 space, then mod
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public void pushRight(T e) throws RuntimeException {
        if (isFull()) {
            throw new RuntimeException();
        }
        elements[tail] = e;
        tail = (tail + 1) & (elements.length - 1);
        if ((tail = (tail + 1) & (elements.length - 1)) == head) {
            doubleCapacity();
        }
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public T peekLeft() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return (T)elements[head];
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public T peekRight() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        // since tail index points to the space after the rightmost element of
        // deque, so tail index back off 1 space, then mod
        return (T)elements[(tail - 1) & (elements.length - 1)];
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public T popLeft() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        @SuppressWarnings("unchecked")
        T element = (T)elements[head];
        elements[head] = null;
        head = (head + 1) & (elements.length - 1);
        return element;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public T popRight() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int newTail = (tail - 1) & (elements.length - 1); // back off 1 space
        @SuppressWarnings("unchecked")
        T element = (T)elements[newTail];
        elements[newTail] = null;
        tail = newTail;
        return element;
    }
    
    @Override
    // time complexity O(1), memory complexity O(1)
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public Iterator<T> reverseIterator() {
        return new ReversingIterator();
    }

    /**
     * An implementation class of Iterator.
     * Constructs an iterator for the deque in left to right sequence.
     */
    private class DequeIterator implements Iterator<T> {
        // index of element to be returned
        private int cursor = head;
        // tail index for checking whether the iterator has reached the end
        private int fence = tail;

        @Override
        // time complexity O(1), memory complexity O(1)
        public boolean hasNext() {
            return cursor != fence;
        }

        @Override
        // time complexity O(1), memory complexity O(1)
        public T next() {
            // checks whether the iterator has reached the end or it's empty
            // hasNext() should be called first prior to calling this function
            if (cursor == fence)
                throw new NoSuchElementException();
            @SuppressWarnings("unchecked")
            T element = (T)elements[cursor];
            cursor = (cursor + 1) & (elements.length - 1);
            return element;
        }
    }

    /**
     * An implementation class of Iterator.
     * Returns an iterator for the deque in right to left sequence.
     *
     * This class is nearly a mirror-image of DeqIterator, using tail instead
     * of head for initial cursor, and head instead of tail for fence.
     */
    private class ReversingIterator implements Iterator<T> {
        // index of element to be returned
        private int cursor = tail;
        // tail index for checking whether the iterator has reached the end
        private int fence = head;

        @Override
        // time complexity O(1), memory complexity O(1)
        public boolean hasNext() {
            return cursor != fence;
        }

        @Override
        // time complexity O(1), memory complexity O(1)
        public T next() {
            if (cursor == fence)
                throw new NoSuchElementException();
            cursor = (cursor - 1) & (elements.length - 1);
            @SuppressWarnings("unchecked")
            T element = (T)elements[cursor];
            return element;
        }
    }
}
