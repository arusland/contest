package io.arusland.contest.spoj;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class JCROSS_test {
    @Test
    public void testJCross() throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream stream = classLoader.getResourceAsStream("io/arusland/contest/spoj/JCROSS.txt");

        new JCROSS().solve(stream);
    }
}
