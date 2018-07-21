package io.arusland.contest.spoj;

import java.io.*;
import java.util.Arrays;

/**
 * http://www.spoj.com/problems/PALIN
 *
 * @author Ruslan Absalyamov
 * @since 2017-09-03
 */
public class PALIN {
    public static void main(String[] args) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line = br.readLine();

            if (line != null) {
                long count = Long.parseLong(line);

                while (--count >= 0 && (line = br.readLine()) != null) {
                    System.out.println(nextPalindrome(line));
                }
            }
        }
    }

    /**
     * a|k|b => x|k'|x'
     */
    public static String nextPalindrome(String val) {
        char[] chr = val.toCharArray();

        return nextPalindrome(chr);
    }

    private static String nextPalindrome(char[] chr) {
        int pivot = chr.length / 2;
        int index2 = chr.length - pivot;
        char[] revertA = revertArray(chr, pivot, null);

        int cmp = compare(revertA, 0, chr, index2, pivot);

        if (cmp <= 0) {
            int pivotFull = chr.length % 2 == 0 ? pivot : pivot + 1;
            boolean extend = increment(chr, pivotFull);

            // arithmetic overflow -> need to extend string
            if (extend) {
                char[] newchr = new char[chr.length + 1];
                newchr[0] = '1';
                System.arraycopy(chr, 0, newchr, 1, pivotFull);
                Arrays.fill(newchr, index2 + 1, newchr.length, '0');

                return nextPalindrome(newchr);
            }

            revertArray(chr, pivot, revertA);
        }

        System.arraycopy(revertA, 0, chr, index2, pivot);

        return String.valueOf(chr);
    }

    private static boolean increment(char[] val, int length) {
        for (int i = length - 1; i >= 0; i--) {
            if (val[i] == '9') {
                val[i] = '0';
            } else {
                val[i] = (char) ((int) val[i] + 1);
                return false;
            }
        }

        return true;
    }

    private static char[] revertArray(char[] chr, int pivot, char[] buf) {
        char[] result = buf != null ? buf : new char[pivot];

        for (int i = 0; i < pivot; i++) {
            result[i] = chr[pivot - i - 1];
        }

        return result;
    }

    private static int compare(char[] val1, final int index1, char[] val2, final int index2, final int length) {
        for (int i = 0; i < length; i++) {
            int idx1 = i + index1;
            int idx2 = i + index2;

            if (val1[idx1] > val2[idx2]) {
                return 1;
            } else if (val1[idx1] < val2[idx2]) {
                return -1;
            }
        }

        return 0;
    }
}
