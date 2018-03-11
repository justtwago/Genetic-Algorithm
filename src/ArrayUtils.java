import java.util.stream.IntStream;

public class ArrayUtils {

    public static boolean isArrayUnique(int[] array) {
        int[] noDuplicates = IntStream.of(array).distinct().toArray();
        return noDuplicates.length == array.length;
    }

    public static boolean isArrayContainsValue(int[] array, int value) {
        return IntStream.of(array).anyMatch(x -> x == value);
    }
}
