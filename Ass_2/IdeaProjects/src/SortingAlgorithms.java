import java.util.Stack;

public class SortingAlgorithms {
    /**
     * Sorts the given array using the selection sort algorithm.
     * This should modify the array in-place.
     *
     * @param input An array of comparable objects.
     * @param reversed If false, the array should be sorted ascending.
     *                 Otherwise, it should be sorted descending.
     * @requires input != null
     */
    static <T extends Comparable> void selectionSort(T[] input, boolean reversed) {
        if (input == null) {
            return;
        }
        int outer, inner, minimum; // indices
        int length = input.length - 1;
        T temp;
        for (outer = 0; outer < length; ++outer) {
            minimum = outer;
            for (inner = outer + 1; inner < input.length; ++inner) {
                if (input[minimum].compareTo(input[inner]) == 1) {
                    minimum = inner;
                }
            }
            temp = input[outer];
            input[outer] = input[minimum];
            input[minimum] = temp;
        }
        // reverse the order of array
        if (reversed) {
            reverse(input);
        }
    }

    /**
     * Rearranges the given array in reversed order.
     * This is a helper function.
     *
     * @param input An array of comparable objects.
     */
    private static <T extends Comparable> void reverse(T[] input) {
        T temp;
        for (int i = 0; i < input.length / 2; ++i) {
            temp = input[i];
            input[i] = input[input.length - i - 1];
            input[input.length - i - 1] = temp;
        }
//        Stack<T> temp = new Stack<>();
//        for (T t : input) {
//            temp.push(t);
//        }
//        for (int i = input.length - 1; i >= 0; --i) {
//            input[i] = temp.pop();
//        }
    }

    /**
     * Sorts the given array using the insertion sort algorithm.
     * This should modify the array in-place.
     *
     * @param input An array of comparable objects.
     * @param reversed If false, the array should be sorted ascending.
     *                 Otherwise, it should be sorted descending.
     * @requires input != null
     */
    static <T extends Comparable> void insertionSort(T[] input, boolean reversed) {
        int outer, inner;
        T temp;
        for (outer = 1; outer < input.length; ++outer) {
            inner = outer;
            temp = input[inner];
            while (inner > 0 && input[inner - 1].compareTo(temp) == 1) {
                input[inner] = input[inner - 1];
                --inner;
            }
            input[inner] = temp;
        }
        if (reversed) {
            reverse(input);
        }
    }

    /**
     * Sorts the given array using the merge sort algorithm.
     * This should modify the array in-place.
     *
     * @param input An array of comparable objects.
     * @param reversed If false, the array should be sorted ascending.
     *                 Otherwise, it should be sorted descending.
     * @requires input != null
     */
    static <T extends Comparable> void mergeSort(T[] input, boolean reversed) {
        if (input == null) {
            return;
        }
        runMergeSort(input, 0, input.length - 1);
        if (reversed) {
            reverse(input);
        }
    }

    /**
     * Sorts the given array using the merge sort algorithm.
     * This is a helper function for mergeSort.
     *
     * @param array An array of comparable objects.
     * @param start The starting index of the given array.
     * @param end The ending index of the given array.
     */
    private static <T extends Comparable> void runMergeSort(T[] array,
                                                            int start, int end) {
        if (start >= end) {
            return;
        }
        int middle = (start + end) / 2;
        // recursively, sort the left half of the array
        runMergeSort(array, start, middle);
        // recursively, sort the right half of the array
        runMergeSort(array, middle + 1, end);
        // merge the two sorted halves
        merge(array, start, middle, end);
    }

    /**
     * Merges the given array in ascending order.
     * This is a helper function for mergeSort.
     *
     * @param array An array of comparable objects.
     * @param start The starting index of the given array.
     * @param middle The index of the middle element of the given array.
     * @param end The ending index of the given array.
     */
    private static <T extends Comparable> void merge(T[] array, int start,
                                                     int middle, int end) {
        T[] tempArray = (T[])new Comparable[array.length];
        // starting index of the right half of the array
        int newStart = middle + 1;
        int tempIndex = start;
        int restoringIndex = start;
        // fills the tempArray in ascending order using the array elements,
        // until one of the half arrays is finished
        while (start <= middle && newStart <= end) {
            if (array[start].compareTo(array[newStart]) <= 0) {
                tempArray[tempIndex++] = array[start++];
            } else {
                tempArray[tempIndex++] = array[newStart++];
            }
        }
        // fills the tempArray using the rest array elements,
        // only one while loop will be executed in the next two while loops.
        while (start <= middle) {
            tempArray[tempIndex++] = array[start++];
        }
        while (newStart <= end) {
            tempArray[tempIndex++] = array[newStart++];
        }
        // restores the given array from tempArray.
        while (restoringIndex <= end) {
            array[restoringIndex] = tempArray[restoringIndex++];
        }
    }

    /**
     * Sorts the given array using the quick sort algorithm.
     * This should modify the array in-place.
     *
     * You should use the value at the middle of the input  array(i.e. floor(n/2))
     * as the pivot at each step.
     *
     * @param input An array of comparable objects.
     * @param reversed If false, the array should be sorted ascending.
     *                 Otherwise, it should be sorted descending.
     * @requires input != null
     */
    static <T extends Comparable> void quickSort(T[] input, boolean reversed) {
        if (input == null) {
            return;
        }
        runQuickSort(input, 0, input.length - 1);
        if (reversed) {
            reverse(input);
        }
    }

    /**
     * Sorts the given array using the quick sort algorithm.
     * This is a helper function for quickSort.
     *
     * @param input An array of comparable objects.
     * @param start The starting index of the given array.
     * @param end The ending index of the given array.
     */
    private static <T extends Comparable> void runQuickSort(T[] input, int start,
                                                            int end) {
        if (start >= end) {
            return;
        }
        int left = start; // indices
        int right = end;
        T pivot = input[start]; // choose a pivot value
        // partition the array, value less than the pivot goes left part of the
        // array, value greater than the pivot goes right part of the array
        while (left < right) {
            while (pivot.compareTo(input[right]) <= 0 && left < right) {
                --right;
            }
            while (pivot.compareTo(input[left]) >= 0 && left < right) {
                ++left;
            }
            if (left < right) {
                T swap;
                swap = input[left];
                input[left] = input[right];
                input[right] = swap;
            }
        }
        // returns the pivot to it's proper position
        input[start] = input[left];
        input[left] = pivot;
        // recursively, sort the values less than the pivot
        runQuickSort(input, start, right - 1);
        // recursively, sort the values greater than the pivot
        runQuickSort(input, right + 1, end);
    }
}
