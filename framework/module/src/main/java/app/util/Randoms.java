package app.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author chi
 */
public final class Randoms {
    private static final String SEED = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        + "0123456789"
        + "abcdefghijklmnopqrstuvxyz";

    public static String random(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be greater than 0");
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++) {
            b.append(SEED.charAt(Math.abs(random.nextInt()) % SEED.length()));
        }
        return b.toString();
    }
}
