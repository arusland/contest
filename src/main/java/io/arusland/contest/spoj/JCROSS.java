package io.arusland.contest.spoj;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * http://www.spoj.com/problems/JCROSS
 *
 * @author Ruslan Absalyamov
 * @since 2018-07-29
 */
public class JCROSS {
    private final static int BLOCK_SOLID = 1;
    private final static int BLOCK_FREE = 2;
    private final static int BLOCK_BLANK = 3;

    public static void main(String[] args) throws Exception {
        new JCROSS().solve(getInput(args));
    }

    protected void solve(InputStream stream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line = br.readLine();

            if (line != null) {
                int count = Integer.parseInt(line);

                while (--count >= 0) {
                    solveNextCase(br);
                }
            }
        }
    }

    private void solveNextCase(BufferedReader br) throws IOException {
        String[] size = br.readLine().split(" ");
        final int rowsCount = Integer.parseInt(size[0]);
        final int colsCount = Integer.parseInt(size[1]);
        BlocksLine[] rows = readBlocks(rowsCount, br);
        BlocksLine[] cols = readBlocks(colsCount, br);

        new SingleCross(rows, cols).solve();
    }

    private static class SingleCross {
        private final BlocksLine[] rows;
        private final BlocksLine[] cols;
        private final int rowsCount;
        private final int colsCount;

        public SingleCross(BlocksLine[] rows, BlocksLine[] cols) {
            this.rows = rows;
            this.cols = cols;
            this.rowsCount = rows.length;
            this.colsCount = cols.length;
        }

        public void solve() {
            calcMaxEdges(rows, colsCount);
            calcMaxEdges(cols, rowsCount);

            fillMatrixViaMaxEdges(rows, cols);
            printMatrix(rows, colsCount);

            fillMatrixViaMaxEdges(cols, rows);
            printMatrix(rows, colsCount);

            calcBlockCertainEdges(rows, cols);
            printMatrix(rows, colsCount);

            calcBlockCertainEdges(cols, rows);
            printMatrix(rows, colsCount);

            fillBlankCells(rows, cols);
            printMatrix(rows, colsCount);

            fillBlankCells(cols, rows);
            printMatrix(rows, colsCount);
        }

        /**
         * Calculates certain cells of the blocks which will be filled exactly
         */
        private static void calcBlockCertainEdges(BlocksLine[] lines, BlocksLine[] another) {
            for (int i = 0; i < lines.length; i++) {
                BlocksLine line = lines[i];

                if (!line.isDone) {
                    Block[] blocks = line.blocks;
                    int right = 0;
                    int left = line.maxEdge;

                    for (int j = 0; j < blocks.length; j++) {
                        Block block = blocks[j];

                        if (block.type == BLOCK_SOLID) {
                            right += block.size;

                            if (left < right) {
                                block.fill(left, right - 1);

                                fillSingleBlocks(another, left, right - 1, i, BLOCK_SOLID);
                            }

                            left += block.size + 1;
                            right += 1;
                        }
                    }
                }
            }
        }

        private static void fillMatrixViaMaxEdges(BlocksLine[] lines, BlocksLine[] another) {
            for (int i = 0; i < lines.length; i++) {
                BlocksLine line = lines[i];
                // when no max edges found
                if (line.maxEdge == 0) {
                    // this row is done and must be skipped further
                    line.isDone = true;
                    // we must remove edge blank blocks
                    Block[] blocks = Arrays.copyOfRange(line.blocks, 1, line.blocks.length - 1);
                    line.blocks = blocks;
                    int left = 0;

                    for (int j = 0; j < blocks.length; j++) {
                        Block block = blocks[j];

                        if (block.type == BLOCK_SOLID) {
                            block.fill(left, left + block.size - 1);
                            fillSingleBlocks(another, left, left + block.size - 1, i, BLOCK_SOLID);

                            left += block.size;
                        } else {
                            block.fill(left, left);
                            another[left].fillSingle(i, BLOCK_BLANK);

                            left += 1;
                        }
                    }
                }
            }
        }

        private static void fillSingleBlocks(BlocksLine[] lines, int start, int end, int singleIndex, int type) {
            for (int i = start; i <= end; i++) {
                BlocksLine line = lines[i];
                line.fillSingle(singleIndex, type);
            }
        }

        /**
         * Calculates maximum size of right/left blank block
         */
        private static void calcMaxEdges(BlocksLine[] lines, int maxSize) {
            for (int i = 0; i < lines.length; i++) {
                BlocksLine line = lines[i];
                Block[] blocks = lines[i].blocks;
                int sum = 0;

                for (int j = 0; j < blocks.length; j++) {
                    Block block = blocks[j];

                    if (block.type == BLOCK_SOLID) {
                        sum += block.size;
                    }
                }

                int emptySize = maxSize - sum;
                int minEmptyBlock = line.solidCount - 1;

                line.maxEdge = emptySize > minEmptyBlock ? emptySize - minEmptyBlock : 0;
            }
        }

        /**
         * Finds and fills blank blocks fully or partly.
         */
        private static void fillBlankCells(BlocksLine[] lines, BlocksLine[] another) {
            for (int i = 0; i < lines.length; i++) {
                BlocksLine line = lines[i];

                if (!line.isDone) {
                    Block[] blocks = line.blocks;
                    // current prev blank start index
                    int blankLeft = -1;
                    boolean removeLeftEdgeBlock = false;
                    boolean removeRightEdgeBlock = false;
                    int finishedSolidBlocks = 0;

                    makeBlocksFromSingles(line);

                    for (int j = 0; j < blocks.length; j++) {
                        Block block = blocks[j];

                        if (block.type == BLOCK_SOLID) {
                            if (block.isDone()) {
                                finishedSolidBlocks++;

                                if (block.start > 0) {
                                    if (blankLeft < 0) {
                                        // prev solid block is not done yet
                                        // but we can assign last cell of prev blank block
                                        // and if it first block it must start from 0
                                        blankLeft = j == 1 ? 0 : block.start - 1;
                                    }

                                    blocks[j - 1].fill(blankLeft, block.start - 1);
                                    fillSingleBlocks(another, blankLeft, block.start - 1, i, BLOCK_BLANK);
                                } else {
                                    // remove left edge blank block if exists
                                    removeLeftEdgeBlock = j > 0;
                                }

                                blankLeft = block.end + 1;
                            } else {
                                // we can't assign blank left start index
                                // so reset it
                                blankLeft = -1;
                            }
                        } else if (block.type == BLOCK_BLANK && (j + 1) == blocks.length) {
                            // if it last blank block
                            Block prevBlock = blocks[j - 1];

                            if (prevBlock.isDone()) {
                                if ((prevBlock.end + 1) == another.length) {
                                    // prev solid block is real last block of the line
                                    removeRightEdgeBlock = true;
                                } else {
                                    block.fill(prevBlock.end + 1, another.length - 1);
                                    fillSingleBlocks(another, prevBlock.end + 1, another.length - 1,
                                            i, BLOCK_BLANK);
                                }
                            }
                        }
                    }

                    if (removeLeftEdgeBlock || removeRightEdgeBlock) {
                        // remove edge blank blocks if need
                        int startIndex = removeLeftEdgeBlock ? 1 : 0;
                        int endIndex = removeRightEdgeBlock ? line.blocks.length - 1 : line.blocks.length;

                        line.blocks = Arrays.copyOfRange(line.blocks, startIndex, endIndex);
                    }

                    if (finishedSolidBlocks == line.solidCount) {
                        // all solid blocks fixed on their places
                        line.isDone = true;
                    }
                }
            }
        }

        /**
         * Transform single blocks into solid blocks.
         */
        private static void makeBlocksFromSingles(BlocksLine line) {
            // TODO: support solidCount > 1
            Integer solidBlock = Integer.valueOf(BLOCK_SOLID);

            if (line.singles.values().contains(solidBlock) && line.solidCount == 1) {
                Block[] blocks = line.blocks;
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                Map<Integer, Integer> blankSingles = new HashMap<>();

                for (int index : line.singles.keySet()) {
                    int type = line.singles.get(index);

                    if (type == BLOCK_SOLID) {
                        min = Math.min(min, index);
                        max = Math.max(max, index);
                    } else {
                        blankSingles.put(index, type);
                    }
                }

                if (min <= max) {
                    for (int i = 0; i < blocks.length; i++) {
                        if (blocks[i].type == BLOCK_SOLID) {
                            blocks[i].fill(min, max);
                        }
                    }

                    line.singles.clear();

                    if (!blankSingles.isEmpty()) {
                        blankSingles.keySet().forEach(p -> line.singles.put(p, blankSingles.get(p)));
                    }
                }
            }
        }
    }

    private static BlocksLine[] readBlocks(int count, BufferedReader br) throws IOException {
        BlocksLine[] data = new BlocksLine[count];

        for (int i = 0; i < count; i++) {
            String[] line = br.readLine().split(" ");
            int solidCount = line.length - 1;
            Block[] blocks = new Block[solidCount * 2 + 1];
            int idx = 0;
            blocks[idx++] = Block.ofBlank();

            for (int j = 0; j < solidCount; j++) {
                blocks[idx++] = Block.ofSolid(Integer.parseInt(line[j]));
                blocks[idx++] = Block.ofBlank();
            }

            data[i] = new BlocksLine(blocks, solidCount);
        }

        return data;
    }

    private static InputStream getInput(String[] args) throws FileNotFoundException {
        return args.length > 0 ? new FileInputStream(args[0]) : System.in;
    }

    private static void printMatrix(BlocksLine[] lines, int lineSize) {
        System.out.print("  ");
        for (int i = 0; i < lineSize; i++) {
            System.out.print((i + 1) % 10);
        }

        System.out.println();

        for (int i = 0; i < lines.length; i++) {
            BlocksLine line = lines[i];
            System.out.print((i + 1) % 10);
            System.out.print('|');

            for (int j = 0; j < lineSize; j++) {
                Block block = line.findBlock(j);

                if (block != null) {
                    System.out.print(getBlockChar(block.type));
                } else {
                    Integer type = line.singles.get(j);

                    if (type != null) {
                        System.out.print(getBlockCharSingle(type));
                    } else {
                        System.out.print('?');
                    }
                }
            }
            System.out.print(line.isDone ? '!' : '|');

            System.out.println(Arrays.stream(line.blocks)
                    .filter(p -> p.type == BLOCK_SOLID)
                    .map(p -> Integer.toString(p.size))
                    .collect(Collectors.joining(",")));
        }

        System.out.println();
    }

    private static Character getBlockChar(int type) {
        switch (type) {
            case BLOCK_BLANK:
                return '.';
            case BLOCK_SOLID:
                return '#';
            case BLOCK_FREE:
                return '$';
            default:
                throw new RuntimeException("Unsupported block type: " + type);
        }
    }

    private static Character getBlockCharSingle(int type) {
        switch (type) {
            case BLOCK_BLANK:
                return '.';
            case BLOCK_SOLID:
                return '#';
            case BLOCK_FREE:
                return '%';
            default:
                throw new RuntimeException("Unsupported block type: " + type);
        }
    }

    /**
     * Separate block.
     * <p>
     * BLANK - not filled block
     * SOLID - filled block
     * FREE - filled block, but it's unknown
     * to which solid block belongs (left or right)
     */
    static class Block {
        int type;
        int start;
        int end;
        int size;

        Block(int type, int start, int end, int size) {
            this.type = type;
            this.start = start;
            this.end = end;
            this.size = size;
        }

        static Block ofBlank() {
            return new Block(BLOCK_BLANK, -1, -1, 1);
        }

        static Block ofSolid(int size) {
            return new Block(BLOCK_SOLID, -1, -1, size);
        }

        static Block ofFree(int size) {
            return new Block(BLOCK_FREE, -1, -1, size);
        }

        public void fill(int start, int end) {
            this.start = start;
            this.end = end;

            if (BLOCK_BLANK == type) {
                // TODO: maybe it is not need?
                this.size = this.end - this.start + 1;
            }
        }

        @Override
        public String toString() {
            return "Block{" +
                    "type=" + type +
                    ", start=" + start +
                    ", end=" + end +
                    ", size=" + size +
                    '}';
        }

        // block found its place
        public boolean isDone() {
            assert type == BLOCK_SOLID;

            // no need to write "&& end >= 0"
            return start >= 0 && (end - start + 1) == size;
        }
    }

    static class BlocksLine {
        public int maxEdge;
        Block[] blocks;
        final int solidCount;
        boolean isDone;
        final Map<Integer, Integer> singles = new HashMap<>();

        public BlocksLine(Block[] blocks, int solidCount) {
            this.blocks = blocks;
            this.solidCount = solidCount;
        }

        Block findBlock(int index) {
            return Arrays.stream(blocks)
                    .filter(p -> index >= p.start && index <= p.end)
                    .findAny().orElse(null);
        }

        public void fillSingle(int singleIndex, int type) {
            singles.put(singleIndex, type);
        }
    }
}
