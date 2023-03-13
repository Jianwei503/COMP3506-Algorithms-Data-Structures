import java.util.Iterator;
import java.util.NoSuchElementException;

public class ReversibleDeque<T> implements SimpleDeque<T> {
    // total number of linked nodes
    private int size = 0;
    // maximum number of nodes, -1 represents unlimited capacity
    private int capacity = -1;
    // pointer to first node
    private Node<T> first = null;
    // pointer to last node
    private Node<T> last = null;

    /**
     * Constructs a new reversible deque, using the given data deque to store
     * elements.
     * The data deque must not be used externally once this ReversibleDeque
     * is created.
     * @param data a deque to store elements in.
     */
    public ReversibleDeque(SimpleDeque<T> data) {
        addAll(data);
    }

    // time complexity O(n), memory complexity O(1)
    public void reverse() {
        Node<T> temp = null;
        Node<T> current = first;
        last = first; // allocates the pointer last to beginning of the deque
        while (current != null) {
            //swaps prev and next pointer for current node
            temp = current.prev;
            current.prev = current.next;
            current.next = temp;
            // move to the next node
            current = current.prev;
        }
        // allocates the pointer first to ending of the deque
        if (temp != null) {
            first = temp.prev;
        }
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public boolean isFull() {
        if (capacity == -1) {
            return false;
        }
        return size >= capacity;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public int size() {
        return size;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public void pushLeft(T e) throws RuntimeException {
        if (isFull()) {
            throw new RuntimeException();
        }
        final Node<T> secondNode = first;
        final Node<T> newNode = new Node<T>(null, e, secondNode);
        first = newNode;
        if (secondNode == null) {
            last = newNode;
        } else {
            secondNode.prev = newNode;
        }
        size++;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public void pushRight(T e) throws RuntimeException {
        if (isFull()) {
            throw new RuntimeException();
        }
        final Node<T> secondLast = last;
        final Node<T> newNode = new Node<>(secondLast, e, null);
        last = newNode;
        if (secondLast == null) {
            first = newNode;
        } else {
            secondLast.next = newNode;
        }
        size++;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public T peekLeft() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        final Node<T> firstNode = first;
        return (firstNode == null) ? null : firstNode.item;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public T peekRight() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        final Node<T> lastNode = last;
        return (lastNode == null) ? null : lastNode.item;
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public T popLeft() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        final Node<T> firstNode = first;
        return unlinkFirst(firstNode);
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public T popRight() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        final Node<T> lastNode = last;
        return unlinkLast(lastNode);
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public Iterator<T> iterator() {
        return new LinkedIterator(0);
    }

    @Override
    // time complexity O(1), memory complexity O(1)
    public Iterator<T> reverseIterator() {
        return new ReversingIterator();
    }

    /**
     * An container class to hold elements and their associative relations
     */
    private static class Node<T> {
        // element of current node
        T item;
        // pointer to next node
        Node<T> next;
        // pointer to previous node
        Node<T> prev;

        /**
         * Constructs a Node class with given element and associative relations
         * @param prev previous node
         * @param element value stored by current node
         * @param next next node
         */
        Node(Node<T> prev, T element, Node<T> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * An implementation class of Iterator.
     * Constructs an iterator for the deque in left to right sequence.
     */
    private class LinkedIterator implements Iterator<T> {
        // the node previously accessed
        private Node<T> lastReturned = null;
        // the node to be accessed
        private Node<T> next;
        // the index of the node to be accessed
        private int nextIndex;

        /**
         * Constructs a new LinkedIterator based deque.
         *
         * @param index the index of the first node of deque
         * time complexity O(1), memory complexity O(1)
         */
        LinkedIterator(int index) {
            next = (index == size) ? null : getNode(index);
            nextIndex = index;
        }

        @Override
        // time complexity O(1), memory complexity O(1)
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        // time complexity O(1), memory complexity O(1)
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        /**
         * Checks whether the current node has a previous node.
         *
         * @return true if has, false otherwise.
         * time complexity O(1), memory complexity O(1)
         */
        private boolean hasPrevious() {
            return nextIndex > 0;
        }

        /**
         * Gets the element of previous node of current node.
         * Check hasPrevious() prior to calling this function.
         *
         * @return the element stored in previous node
         * time complexity O(1), memory complexity O(1)
         */
        private T getPrevious() {
            if (!hasPrevious())
                throw new NoSuchElementException();
            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }
    }

    /**
     *An implementation class of Iterator.
     * Provides descending iterators via LinkedIterator.
     * time complexity O(1), memory complexity O(1)
     */
    private class ReversingIterator implements Iterator<T> {
        // it's a LinkedIterator essentially, traverses elements reversely
        private final LinkedIterator iterator = new LinkedIterator(size());

        @Override
        public boolean hasNext() {
            return iterator.hasPrevious();
        }

        @Override
        public T next() {
            return iterator.getPrevious();
        }
    }

    /**
     * Returns the Node at the specified element index.
     *
     * @param index a specified element index
     * @return the Node at the specified index
     * time complexity O(n), memory complexity O(1)
     */
    private Node<T> getNode(int index) {
        // halves the nodes to speed up the searching
        if (index < (size >> 1)) {
            Node<T> node = first;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
            return node;
        } else {
            Node<T> node = last;
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
            return node;
        }
    }

    /**
     * Copies and adds all nodes from otherDeque to this deque.
     * otherDeque should be left intact.
     * @param otherDeque the other deque to copy nodes from
     * time complexity O(n), memory complexity O(1)
     */
    private void addAll(SimpleDeque<? extends T> otherDeque) {
        int size = otherDeque.size(); // number of elements that otherDeque has
        Iterator iterator = otherDeque.iterator();
        if (size == 0) {
            return;
        } else if (size == 1) {
            first = last = new Node<T>(null, (T)iterator.next(), null);
        } else {
            Node<T> prevNode = new Node<T>(null, (T)iterator.next(), null);
            Node<T> nextNode;
            first = prevNode;
            while (iterator.hasNext()) {
                nextNode = new Node<T>(prevNode, (T)iterator.next(), null);
                prevNode.next = nextNode;
                prevNode = nextNode;
            }
            last = prevNode;
        }
        this.size = size;
        if (otherDeque.isFull()) {
            capacity = size;
        }
    }

    /**
     * Unlinks non-null first node of deque, and returns its element.
     * @param firstNode the first node of deque
     * @return the element of first node
     * time complexity O(1), memory complexity O(1)
     */
    private T unlinkFirst(Node<T> firstNode) {
        final T element = firstNode.item;
        final Node<T> next = firstNode.next;
        firstNode.item = null;
        firstNode.next = null;
        first = next;
        if (next == null) {
            last = null;
        } else {
            next.prev = null;
        }
        size--;
        return element;
    }

    /**
     * Unlinks non-null last node of deque, and returns its element
     * @param lastNode the last node of deque
     * @return the element of last node
     * time complexity O(1), memory complexity O(1)
     */
    private T unlinkLast(Node<T> lastNode) {
        final T element = lastNode.item;
        final Node<T> prev = lastNode.prev;
        lastNode.item = null;
        lastNode.prev = null;
        last = prev;
        if (prev == null) {
            first = null;
        } else {
            prev.next = null;
        }
        size--;
        return element;
    }
}

