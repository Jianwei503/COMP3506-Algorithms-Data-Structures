import java.lang.reflect.Array;
public class TestGenericsArray {
    static <T extends Comparable> void merge(T[] input) {
//        T[] tempArray = new GenericsArray().getArray(T, 10);
    }
}
class GenericsArray {
//    @SuppressWarnings({"unchecked", "hiding"})
    public static <T> T[] getArray(Class<T> type, int length) {
        return (T[]) Array.newInstance(type, length);
    }
}
