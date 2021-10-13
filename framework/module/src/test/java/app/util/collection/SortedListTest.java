package app.util.collection;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author chi
 */
public class SortedListTest {
    @Test
    public void add() {
        SortedList<Integer> list = new SortedList<>(Comparator.comparingInt(o -> o));
        list.add(2);
        list.add(1);
        assertEquals(1, (int) list.get(0));
    }

    @Test
    public void addAll() {
        SortedList<Integer> list = new SortedList<>(Comparator.comparingInt(o -> o));
        list.add(2);
        list.addAll(Arrays.asList(1, 3));
        assertEquals(1, (int) list.get(0));
    }
}