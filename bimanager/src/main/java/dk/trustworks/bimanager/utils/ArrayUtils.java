package dk.trustworks.bimanager.utils;

/**
 * Created by hans on 08/07/16.
 */
public class ArrayUtils {
    public static double average(int[] array, int length) {
        // 'average' is undefined if there are no elements in the list.
        System.out.println("array.length = " + array.length);
        System.out.println("array == null = " + array == null);
        if (array == null || array.length == 0)
            return 0.0;
        // Calculate the summation of the elements in the list
        long sum = 0;
        int n = length;
        // Iterating manually is faster than using an enhanced for loop.
        for (int i = 0; i < n; i++)
            sum += array[i];
        // We don't want to perform an integer division, so the cast is mandatory.
        System.out.println("((double) sum) / n = " + ((double) sum) / n);
        return ((double) sum) / n;
    }
}
