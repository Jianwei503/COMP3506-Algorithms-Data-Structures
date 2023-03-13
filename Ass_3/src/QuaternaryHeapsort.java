public class QuaternaryHeapsort {

    /**
     * Sorts the input array, in-place, using a quaternary heap sort.
     *
     * time complexity: O(nlog n)
     * space complexity: O(1)
     *
     * @param input to be sorted (modified in place)
     */
    public static <T extends Comparable<T>> void quaternaryHeapsort(T[] input) {
        // TODO: implement question 1 here
        assert input != null;
        if (input.length < 2) {
            return;
        }
        int startIndex = get_start_index(input.length);
        // Adjusts the array to a legitimate heap
        for (int i = startIndex; i >= 0; --i) {
            quaternaryDownheap(input, i, input.length);
        }
        // Swaps top and tail element of heap, leaves tail element alone,
        // adjusts the remaining array to a legitimate heap
        for (int j = input.length - 1; j > 0; --j) {
            swap(input, 0, j);
            quaternaryDownheap(input, 0, j);
        }
    }

    /**
     * Performs a downheap from the element in the given position on the given max heap array.
     *
     * A downheap should restore the heap order by swapping downwards as necessary.
     * The array should be modified in place.
     * 
     * You should only consider elements in the input array from index 0 to index (size - 1)
     * as part of the heap (i.e. pretend the input array stops after the inputted size).
     *
     * time complexity: O(log n)
     * space complexity: O(1)
     *
     * @param input array representing a quaternary max heap.
     * @param start position in the array to start the downheap from.
     * @param size the size of the heap in the input array, starting from index 0
     */
    public static <T extends Comparable<T>> void quaternaryDownheap(T[] input, int start, int size) {
        // TODO: implement question 1 here
        T temp = input[start];
        int counter;
        int loopLimit;
        for (int i = start * 4 + 1; i < size; i = i * 4 + 1) {
            // finds the node which has greatest value among sibling nodes
            counter = i;
            loopLimit = counter + 3;
            while (counter < loopLimit) {
                if (counter + 1 >= size) {
                    break;
                }
                if (input[counter + 1].compareTo(input[i]) > 0) {
                    i = counter + 1;
                }
                ++counter;
            }
            // swaps the parent node and it's child node which has greatest value
            if (temp.compareTo(input[i]) >= 0) {
                break;
            } else {
                input[start] = input[i];
                start = i;
            }
        }
        input[start] = temp;
    }

    /**
     * Finds the index of the most right non-leaf node in a quaternary heap.
     *
     * both time and space complexity are: O(1)
     *
     * @param size the size of the heap in the input array
     * @return the index of the most right non-leaf node
     */
    private static int get_start_index(int size) {
        int mod = (size - 1) % 4;
        if (mod == 0) {
            return (size - 1) / 4 - 1;
        } else {
            return (size - 1) / 4;
        }
    }

    /**
     * Swaps two elements (input[x] & input[y]) of the given array.
     *
     * both time and space complexity are: O(1)
     *
     * @param input the given array
     * @param x the index of the 1st element to be swapped
     * @param y the index of the 2nd element to be swapped
     */
    private static <T extends Comparable<T>> void swap(T[] input, int x, int y) {
        T temp = input[x];
        input[x] = input[y];
        input[y] = temp;
    }
}
