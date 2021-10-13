import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task Description
 * A precedence rule is given as "P>E", which means that letter "P" is followed directly by the letter "E". Write a function, given an array of precedence rules, that finds the word represented by the given rules.
 * <p>
 * Note: Each represented word contains a set of unique characters, i.e. the word does not contain duplicate letters.
 * <p>
 * Examples:
 * findWord(["P>E","E>R","R>U"]) // PERU
 * findWord(["I>N","A>I","P>A","S>P"]) // SPAIN
 * <p>
 * <p>
 * <p>
 * For example:
 * <p>
 * X O O      X 1 0
 * O O O  ->  3 3 1
 * X X O      X X 1
 * <p>
 * <p>
 * The Moore neighborhood is defined by the eight cells surrounding the cell, the four directly next to it and four diagonal to it.
 * <p>
 * The input is a an array of strings, with each element representing a row in the matrix.
 * <p>
 * Example:
 * minesweeper(["XOO", "OOO", "XXO"]) // should print
 * <p>
 * X 1 0
 * 3 3 1
 * X X 1
 */

public class Main {
    public static void main(String[] args) {
        minesweeper(new String[]{"XOO", "OOO", "XXO"});
    }

    public static void minesweeper(String[] array) {
        if (array == null || array.length == 0) {
            return;
        }
        char[][] matrix = new char[array.length][array[0].length()];
        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            for (int j = 0; j < s.length(); j++) {
                char c = s.charAt(j);
                matrix[i][j] = c;
            }
        }

        char[][] transformed = new char[array.length][array[0].length()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                char c = matrix[i][j];
                if (c == 'X') {
                    transformed[i][j] = 'X';
                } else {
                    int total = 0;
                    for (int m = i - 1; m <= i + 1; m++) {
                        for (int n = j - 1; n <= j + 1; n++) {
                            if (m >= 0 && m < matrix.length && n >= 0 && n < matrix[i].length) {
                                char next = matrix[m][n];
                                if (next == 'X') {
                                    total++;
                                }
                            }
                        }
                    }
                    transformed[i][j] = String.valueOf(total).charAt(0);
                }
            }
        }

        System.out.println("-----");
        for (int i = 0; i < transformed.length; i++) {
            for (int j = 0; j < transformed[i].length; j++) {
                System.out.print(transformed[i][j]);
                System.out.print(' ');
            }
            System.out.println("");
        }

    }

    public static String findWord(String[] mappings) {
        if (mappings == null || mappings.length == 0) {
            return "";
        }
        Map<String, String> chain = new HashMap<>();
        List<String> roots = new ArrayList();
        for (String mapping : mappings) {
            String[] parts = mapping.split(">");
            chain.put(parts[0], parts[1]);
            roots.add(parts[0]);
        }

        for (String root : roots) {
            String next = root;
            String word = root;

            while (next != null) {
                next = chain.get(next);
                if (next != null) {
                    word += next;
                } else {
                    break;
                }
            }

            if (word.length() == mappings.length + 1) {
                return word;
            }
        }
        return null;
    }
}
