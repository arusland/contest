package io.arusland.contest.spoj;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Ruslan Absalyamov
 * @since 2017-09-10
 */
public class PRIME1 {
    public static void main(String[] args) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int number = Integer.parseInt(br.readLine());

            for (int i = 0; i < number; i++) {
                String line = br.readLine();
                String[] parts = line.split(" ");
                long m = Long.parseLong(parts[0]);
                long n = Long.parseLong(parts[1]);

                printPrimes(m, n);
                System.out.println("");
            }
        }
    }

    private static void printPrimes(long m, long n) {
        for (long x = m; x <= n; x++) {
            if (isPrimeFast(x)) {
                System.out.println(x);
            }
        }
    }

    private static boolean isPrimeFast(long x) {
        if (x == 1) return false;
        if (x == 2 || x == 3) return true;
        if (x % 2 == 0 || x % 3 == 0)
            return false;

        long upto = (long) Math.sqrt(x);
        for (long i = 4; i < upto + 1; i++) {
            if (x % i == 0) {
                return false;
            }
        }

        return true;
    }

    /*private static void testPerf() {
        long lastTime = System.currentTimeMillis();

        for (long x = 0; x < 1_000_000; x++) {
            isPrimeFast(x);
        }

        long timeFast = System.currentTimeMillis() - lastTime;

        System.out.println(String.format("timeFast=%d", timeFast));

        lastTime = System.currentTimeMillis();

        for (long x = 0; x < 1_000_000; x++) {
            isPrimeSlow(x);
        }
        long timeSlow = System.currentTimeMillis() - lastTime;

        System.out.println(String.format("timeSlow=%d", timeSlow));
    }

    private static boolean isPrimeSlow(long x) {
        if (x == 1) return false;
        if (x == 2 || x == 3) return true;
        if (x % 2 == 0 || x % 3 == 0)
            return false;

        for (long i = 4; i < x; i++) {
            if (x % i == 0) {
                return false;
            }
        }

        return true;
    }*/
}
