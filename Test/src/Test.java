import java.util.*;

public class Test {
    public static void main(String[] args) {
        List<Integer> l = new ArrayList<>();
        l.add(0);l.add(1);l.add(2);l.add(3);l.add(4);
        for (Integer i : l) {
            if (i == 2) {
                l.remove(i);
            }
            System.out.print(i + "\t");
        }
        System.out.println();
        for (Integer n : l) {
            System.out.print(n + "\t");
        }


//        List<Integer> l = new ArrayList<>();
//        l = List.of(5, 4, 9, 0);
//        PriorityQueue<Integer> q = new PriorityQueue<>(l);
//        Integer[] arr = q.toArray(new Integer[4]);
//        for (Integer n : arr) {
//            System.out.println(n);
//        }
//        for (Integer i : q) {
//            System.out.println(i);
//        }
//        System.out.println("***********");
//        Iterator<Integer> it = q.iterator();
////        while (it.hasNext()) {
////            System.out.println(it.next());
////        }
//        System.out.println("*************");
//        while (it.hasNext()) {
//            System.out.println(q.poll());
//        }

//        ArrayList[][] lists = new ArrayList[5][8];
//        int[][] arr = new int[5][8];
//        System.out.println(arr.length);

//        int sum = 5;
//        int count = 1;
//        while (count++ < sum) {
//            System.out.println('c');
//        }


//        int k = Objects.hashCode("sdfsd");
//        int j = Objects.hashCode("sdfsd");
//        int x = Objects.hash("sdfsd");
//        System.out.println(k);
//        System.out.println(j);
//        System.out.println(x);

//        char a = 'a';
//        int k = a%10;
//        System.out.println(k);


//        int k = 2;
//        if (k++ == 2) {
//            System.out.println(k);
//        }


//        int arr[] = {30,160,20};
//        int size = 3;
//        int i = 0;
//        int counter = i;
//        int sum = counter + 3;
//        while (counter < sum) {
//            if (counter + 1 >= size) {
//                break;
//            }
//            if (arr[counter + 1] > arr[i]) {
//                i = counter + 1;
//            }
//            counter++;
//        }
//        System.out.println(arr[i]);


//        if ('c' > 'a') {
//            System.out.println("char can be compared");
//        }


//        boolean[] b = {true};
//        System.out.println(b[0]);
//        b[0] = false;
//        System.out.println(b[0]);
//        editArr(b);
//        System.out.println(b[0]);

//        int[] arr = new int[5];
//        System.out.println(arr.length);

    }

//    static void editArr(boolean[] b) {
//        b[0] = true;
//    }
}
