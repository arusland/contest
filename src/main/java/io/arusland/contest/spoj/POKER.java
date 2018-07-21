package io.arusland.contest.spoj;

import java.io.*;
import java.util.*;

/**
 * http://www.spoj.com/problems/POKER
 *
 * @author Ruslan Absalyamov
 * @since 2018-07-21
 */
public class POKER {
    private static final int CARD_MAX = 5;
    private static final int RANK_ACE = getRankValue('A');

    public static void main(String[] args) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getInput(args)))) {
            String line = br.readLine();

            if (line != null) {
                int count = Integer.parseInt(line);

                while (--count >= 0 && (line = br.readLine()) != null) {
                    System.out.println(calcPokerHand(line));
                }
            }
        }
    }

    private static InputStream getInput(String[] args) throws FileNotFoundException {
        return args.length > 0 ? new FileInputStream(args[0]) : System.in;
    }

    private static String calcPokerHand(String line) {
        String parts[] = line.split(" ");
        int values[] = getOrderedValues(parts);
        Set<Character> suits = getSuits(parts);
        int min = values[0];
        int max = values[CARD_MAX - 1];
        boolean sameSuit = suits.size() == 1;

        // ace, king, queen, jack and ten, all in the same suit
        if (min == 10 && sameSuit) {
            return "royal flush";
        }

        // five cards (of the same suit) in sequence, such as 10,9,8,7,6 of clubs
        if (max == (min + CARD_MAX - 1)) {
            return sameSuit ? "straight flush" : "straight";
        }

        // ace can be counted both as the highest card or as the lowest card - A,2,3,4,5 of hearts
        // is a straight flush. But 4,3,2,A,K of hearts is not a straight flush - it's just a flush
        if (max == RANK_ACE && values[CARD_MAX - 2] == 5) {
            return sameSuit ? "straight flush" : "straight";
        }

        if (sameSuit) {
            // five cards of the same suit (but not a straight flush)
            return "flush";
        } else {
            // four cards of the same rank, such as four kings
            if (min == values[CARD_MAX - 2] || max == values[1]) {
                return "four of a kind";
            }

            //  three cards of one rank plus two cards of another rank
            if (min == values[1] && max == values[CARD_MAX - 3]
                    || min == values[2] && max == values[CARD_MAX - 2]) {
                return "full house";
            }

            // three cards of one rank and two other cards
            for (int i = 0; i < 3; i++) {
                if (values[i] == values[i + 2]) {
                    return "three of a kind";
                }
            }

            // two cards of one rank, two cards of another rank, and one more card
            int pairs = 0;
            for (int i = 0; i < 4; i++) {
                if (values[i] == values[i + 1]) {
                    if (++pairs == 2) {
                        return "two pairs";
                    }
                }
            }

            // two cards of the same rank
            if (pairs == 1) {
                return "pair";
            }
        }

        // none of the above
        return "high card";
    }

    private static Set<Character> getSuits(String[] parts) {
        Set<Character> suits = new HashSet<>(4);

        for (int i = 0; i < CARD_MAX; i++) {
            suits.add(parts[i].charAt(1));
        }

        return suits;
    }

    private static int[] getOrderedValues(String[] parts) {
        int[] ranks = new int[CARD_MAX];

        for (int i = 0; i < CARD_MAX; i++) {
            ranks[i] = getRankValue(parts[i].charAt(0));
        }

        Arrays.sort(ranks);

        return ranks;
    }

    private static int getRankValue(char ch) {
        switch (ch) {
            case 'A':
                return 14;
            case 'K':
                return 13;
            case 'Q':
                return 12;
            case 'J':
                return 11;
            case 'T':
                return 10;
            case '9':
                return 9;
            case '8':
                return 8;
            case '7':
                return 7;
            case '6':
                return 6;
            case '5':
                return 5;
            case '4':
                return 4;
            case '3':
                return 3;
            case '2':
                return 2;
            default:
                throw new RuntimeException("Unsupported rank: " + ch);
        }
    }
}
