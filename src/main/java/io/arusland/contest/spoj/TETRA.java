package io.arusland.contest.spoj;

import java.io.*;

/**
 * http://www.spoj.com/problems/TETRA
 *
 * @author Ruslan Absalyamov
 * @since 2018-08-19
 */
public class TETRA {

    public static void main(String[] args) throws Exception {
        new TETRA().solve(System.in);
    }

    void solve(InputStream stream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line = br.readLine();

            if (line != null) {
                int count = Integer.parseInt(line);

                while (--count >= 0 && (line = br.readLine()) != null) {
                    if (line.length() > 0) {
                        System.out.println(String.format("%.4f", solveNextCase(line)));
                    }
                }
            }
        }
    }

    private double solveNextCase(String line) {
        String[] parts = line.split(" ");

        return solveByLengths(Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Double.parseDouble(parts[4]),
                Double.parseDouble(parts[5]));
    }

    /**
     * Calcs radius of inscribed sphere into tetrahedron
     *
     * @param a length of wx
     * @param b length of wy
     * @param c length of wz
     * @param d length of xy
     * @param e length of xz
     * @param f length of yz
     * @return radius of inscribed sphere into tetrahedron
     */
    private double solveByLengths(double a, double b, double c, double d, double e, double f) {
        // calc areas of four faces
        double sxyw = calcArea(a, b, d);
        double syzw = calcArea(b, c, f);
        double sxyz = calcArea(d, e, f);
        double sxzw = calcArea(a, c, e);
        double area = sxyw + syzw + sxyz + sxzw;
        // calc volume of tetrahedron
        double a1 = b * b + c * c - f * f;
        double b1 = a * a + c * c - e * e;
        double c1 = a * a + b * b - d * d;
        double volume = Math.sqrt(4 * a * a * b * b * c * c - a * a * a1 * a1 - b * b * b1 * b1 - c * c * c1 * c1 + a1 * b1 * c1) / 12D;
        double radius = 3D * volume / area;

        return radius;
    }

    /**
     * Calculates area of a triangle by lengths.
     * <p>
     * Heron's formula
     */
    private double calcArea(double a, double b, double c) {
        // calc semiperimiter
        double sp = (a + b + c) / 2D;
        return Math.sqrt(sp * Math.abs(sp - a) * Math.abs(sp - b) * Math.abs(sp - c));
    }
}
