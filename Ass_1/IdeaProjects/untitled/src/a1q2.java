public class a1q2 {
    public static boolean FindPosition(int[] A, int i, int n) {
        if (n == 0) {
            return false;
        }
        if (A[n + i - 1] == n + i - 1) {
            return true;
        }
        if (n == 1) {
            return A[i] == i;
        } else {
            return FindPosition(A, i, n / 2) || FindPosition(A, i + n / 2, n / 2);
        }
    }

    public static void main(String[] args) {
        int[] A = {-1, 0, 2, 3, 10, 11, 23, 24, 102};
        System.out.println(FindPosition(A, 0, 9));
    }
}
