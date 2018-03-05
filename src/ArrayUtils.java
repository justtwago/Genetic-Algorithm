import java.util.stream.IntStream;

public class ArrayUtils {
    public static void rotate(int[] arr, int order) {
        int offset = arr.length - order % arr.length;
        if (offset > 0) {
            int[] copy = arr.clone();
            for (int i = 0; i < arr.length; ++i) {
                int j = (i + offset) % arr.length;
                arr[i] = copy[j];
            }
        }
    }

    public static boolean isArrayUnique(int[] array) {
        int[] noDuplicates = IntStream.of(array).distinct().toArray();
        return noDuplicates.length == array.length;
    }

    public static boolean isArrayContainsValue(int[] array, int value) {
        return IntStream.of(array).anyMatch(x -> x == value);
    }
}
