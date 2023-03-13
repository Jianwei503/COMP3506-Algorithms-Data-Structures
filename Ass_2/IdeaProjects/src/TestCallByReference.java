public class TestCallByReference {
    public static void main(String[] args) {
        int[] a = {1, 2, 3};
        pass(a);
        System.out.println(a[0]);
        System.out.println(a[1]);
        System.out.println(a[2]);
    }

    public static void pass(int[] a) {
        edit(a);
    }

    public static void edit(int[] a) {
        a[1] = 99;
    }
}
