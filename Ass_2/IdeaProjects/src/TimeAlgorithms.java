import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class TimeAlgorithms {
    private Integer[] unsorted;
    private Integer[] ascending;
    private Integer[] descending;

    public void generateArrays(int n, String s) {
        Random num =  new Random();
        Integer[] temp = new Integer[n];
        for (int i = 0; i < n; ++i) {
            temp[i] = num.nextInt(n);
        }
        if (s.equals("unsorted")) {
            unsorted = new Integer[n];
            unsorted = temp;
        } else if (s.equals("ascending")) {
            Arrays.sort(temp);
            ascending = new Integer[n];
            ascending = temp;
        } else {
            Arrays.sort(temp, Collections.reverseOrder());
            descending = new Integer[n];
            descending = temp;
        }
    }

    public void runTest(int n, Integer[] array) {
        System.out.printf("n = %d%n", n);

        // test for Selection Sort
        Integer[] arrayS = new Integer[n];
        System.arraycopy(array, 0, arrayS, 0, n);
        long startS = System.nanoTime();
        SortingAlgorithms.selectionSort(arrayS, false);
        long endS = System.nanoTime();
        double timeS = (endS - startS) / 1000000.0;
        System.out.println("Selection Sort Runtime: " + timeS + " ms");

        // test for Insertion Sort
        Integer[] arrayI = new Integer[n];
        System.arraycopy(array, 0, arrayI, 0, n);
        long startI = System.nanoTime();
        SortingAlgorithms.insertionSort(arrayI, false);
        long endI = System.nanoTime();
        double timeI = (endI - startI) / 1000000.0;
        System.out.println("Insertion Sort Runtime: " + timeI + " ms");

        // test for Merge Sort
        Integer[] arrayM = new Integer[n];
        System.arraycopy(array, 0, arrayM, 0, n);
        long startM = System.nanoTime();
        SortingAlgorithms.mergeSort(arrayM, false);
        long endM = System.nanoTime();
        double timeM = (endM - startM) / 1000000.0;
        System.out.println("Merge Sort Runtime: " + timeM + " ms");

        // test for Quick Sort
        Integer[] arrayQ = new Integer[n];
        System.arraycopy(array, 0, arrayQ, 0, n);
        long startQ = System.nanoTime();
        SortingAlgorithms.quickSort(arrayQ, false);
        long endQ = System.nanoTime();
        double timeQ = (endQ - startQ) / 1000000.0;
        System.out.println("Quick Sort Runtime: " + timeQ + " ms");
    }

    public static void main(String[] args) {
        int[] cases = {5, 10, 50, 100, 500, 1000, 10000};
        TimeAlgorithms test = new TimeAlgorithms();

        System.out.println("********** test for unsorted numbers **********");
        for (int i = 0; i < 7; ++i) {
            test.generateArrays(cases[i], "unsorted");
            test.runTest(cases[i], test.unsorted);
        }

        System.out.println("********** test for ascending numbers **********");
        for (int i = 0; i < 7; ++i) {
            test.generateArrays(cases[i], "ascending");
            test.runTest(cases[i], test.ascending);
        }

        System.out.println("********** test for descending numbers **********");
        for (int i = 0; i < 7; ++i) {
            test.generateArrays(cases[i], "descending");
            test.runTest(cases[i], test.descending);
        }
    }
}
