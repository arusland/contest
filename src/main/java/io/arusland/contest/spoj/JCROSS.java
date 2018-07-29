package io.arusland.contest.spoj;

import java.io.*;

/**
 * http://www.spoj.com/problems/JCROSS
 *
 * @author Ruslan Absalyamov
 * @since 2018-07-29
 */
public class JCROSS {
    private final static int CELL_BLANK = 0;
    private final static int CELL_FILLED = 1;
    private final static int CELL_CROSS = 2;
    private static final int CELL_DEBUG = 42;

    public static void main(String[] args) throws Exception {
        mainInternal(getInput(args));
    }

    protected static void mainInternal(InputStream stream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line = br.readLine();

            if (line != null) {
                int count = Integer.parseInt(line);

                while (--count >= 0) {
                    calcNextCase(br);
                }
            }
        }
    }

    private static void calcNextCase(BufferedReader br) throws IOException {
        String[] size = br.readLine().split(" ");
        final int rowsCount = Integer.parseInt(size[0]);
        final int colsCount = Integer.parseInt(size[1]);
        int[][] rows = readLines(rowsCount, br);
        int[][] cols = readLines(colsCount, br);
        int[][] matrix = new int[rowsCount][colsCount];

        int[] rowsMaxEdges = calcMaxEdges(rows, colsCount);
        int[] colsMaxEdges = calcMaxEdges(cols, rowsCount);

        fillMatrixViaMaxEdgesHorizontal(matrix, rows, rowsMaxEdges);
        fillMatrixViaMaxEdgesVertical(matrix, cols, colsMaxEdges);

        printMatrix(matrix);

        int[][] rowsMin = calcMins(rows);
        int[][] rowsMax = calcMaxes(rows, rowsMaxEdges);

        // int[][] colsMin = calcMins(cols);
        //int[][] colsMax = calcMaxes(cols, colsMaxEdges);

        fillViaMinAndMaxHorizontal(matrix, rowsMin, rowsMax);

        System.out.println();

        printMatrix(matrix);
    }

    private static void fillViaMinAndMaxHorizontal(int[][] matrix, int[][] rowsMin, int[][] rowsMax) {
        for (int i = 0; i < matrix.length; i++) {
            int[] row = matrix[i];
            int[] rowMin = rowsMin[i];
            int[] rowMax = rowsMax[i];

            for (int j = 0; j < rowMin.length; j++) {
                int right = rowMin[j];

                for (int left = rowMax[j]; left <= right; left++) {
                    row[left - 1] = CELL_DEBUG;
                }
            }
        }
    }

    private static void printMatrix(int[][] matrix) {
        System.out.print(" ");
        for (int i = 0; i < matrix[0].length; i++) {
            System.out.print((i + 1) % 10);
        }

        System.out.println();

        for (int i = 0; i < matrix.length; i++) {
            int[] row = matrix[i];
            System.out.print((i + 1) % 10);

            for (int j = 0; j < row.length; j++) {
                switch (row[j]) {
                    case CELL_BLANK:
                        System.out.print(" ");
                        break;
                    case CELL_CROSS:
                        System.out.print(".");
                        break;
                    case CELL_FILLED:
                        System.out.print("#");
                        break;
                    case CELL_DEBUG:
                        System.out.print("?");
                        break;
                    default:
                        throw new RuntimeException("Unsupported cell value: " + row[j]);
                }
            }

            System.out.println();
        }
    }

    private static void fillMatrixViaMaxEdgesHorizontal(int[][] matrix, int[][] rows, int[] rowsMaxEdges) {
        for (int i = 0; i < rowsMaxEdges.length; i++) {
            if (rowsMaxEdges[i] == 0) {
                int[] row = rows[i];
                int[] rowMatrix = matrix[i];
                int left = 0;

                for (int j = 0; j < row.length; j++) {
                    for (int k = 0; k < row[j]; k++) {
                        rowMatrix[left + k] = CELL_FILLED;
                    }

                    left += row[j];

                    if (left < rowMatrix.length) {
                        rowMatrix[left] = CELL_CROSS;
                        // between filled blocks must be cross
                        left += 1;
                    }
                }
            }
        }
    }

    private static void fillMatrixViaMaxEdgesVertical(int[][] matrix, int[][] cols, int[] colsMaxEdges) {
        for (int i = 0; i < colsMaxEdges.length; i++) {
            if (colsMaxEdges[i] == 0) {
                int[] col = cols[i];
                int top = 0;

                for (int j = 0; j < col.length; j++) {
                    for (int k = 0; k < col[j]; k++) {
                        matrix[top + k][i] = CELL_FILLED;
                    }

                    top += col[j];

                    if (top < matrix.length) {
                        matrix[top][i] = CELL_CROSS;
                        // between filled blocks must be cross
                        top += 1;
                    }
                }
            }
        }
    }

    /**
     * Calculates maximum size of right/left side
     */
    private static int[] calcMaxEdges(int[][] rows, int cols) {
        int[] maxEdges = new int[rows.length];

        for (int i = 0; i < maxEdges.length; i++) {
            int[] row = rows[i];
            int sum = 0;

            for (int j = 0; j < row.length; j++) {
                sum += row[j];
            }

            int empty = cols - sum;
            int minEmptyBlock = row.length - 1;

            maxEdges[i] = empty > minEmptyBlock ? empty - minEmptyBlock : 0;
        }

        return maxEdges;
    }

    /**
     * Calculates right/bottom edges of filled blocks shifted to the left/top
     */
    private static int[][] calcMins(int[][] rows) {
        int[][] rowsMin = new int[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            int[] row = rows[i];
            int[] rowMin = new int[row.length];
            int right = 0;

            for (int j = 0; j < row.length; j++) {
                rowMin[j] = right + row[j];
                right = rowMin[j] + 1;
            }

            rowsMin[i] = rowMin;
        }

        return rowsMin;
    }

    /**
     * Calculates left/top edges of filled blocks shifted to the right/bottom
     */
    private static int[][] calcMaxes(int[][] rows, int[] edgeMax) {
        int[][] rowsMax = new int[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            int[] row = rows[i];
            int[] rowMax = new int[row.length];
            int left = edgeMax[i];

            for (int j = 0; j < row.length; j++) {
                rowMax[j] = left + 1;
                left = rowMax[j] + row[j];
            }

            rowsMax[i] = rowMax;
        }

        return rowsMax;
    }

    private static int[][] readLines(int count, BufferedReader br) throws IOException {
        int[][] data = new int[count][];

        for (int i = 0; i < count; i++) {
            String[] line = br.readLine().split(" ");
            int[] values = new int[line.length - 1];

            for (int j = 0; j < values.length; j++) {
                values[j] = Integer.parseInt(line[j]);
            }
            data[i] = values;
        }

        return data;
    }

    private static InputStream getInput(String[] args) throws FileNotFoundException {
        return args.length > 0 ? new FileInputStream(args[0]) : System.in;
    }
}
