package app.util.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * @author chi
 */
public class SortedList<E> extends LinkedList<E> {
    private final Comparator<E> comparator;

    public SortedList(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean add(E e) {
        for (int i = 0; i < size(); i++) {
            E current = get(i);
            if (comparator.compare(current, e) > 0) {
                add(i, e);
                return true;
            }
        }
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }
}
