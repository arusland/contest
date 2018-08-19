package io.arusland.contest.spoj;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class TETRA_Test {
    @Test
    public void testSolveTetra() throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream stream = classLoader.getResourceAsStream("io/arusland/contest/spoj/TETRA.txt");

        new TETRA().solve(stream);
    }
}
