public class a1q1 {
    private static int n = 347423;
    private static int sum = 0;

    public static int calculate() {
        int count = 0;
        while(n > 0) {
            ++count;
            int k = n & 1;
            sum += k;
            n = n >> 1;
        }
        System.out.println(count);
        return sum;
    }

    public static void main(String[] args) {
        calculate();
    }
}
