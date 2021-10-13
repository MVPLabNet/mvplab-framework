package app.util.collection;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chi
 */
public class QueryResponse<T> implements Iterable<T> {
    public Long total;
    public List<T> items;
    public Integer page;
    public Integer limit;

    public <K> QueryResponse<K> map(Function<T, K> mapper) {
        QueryResponse<K> target = new QueryResponse<>();
        target.total = total;
        target.items = items.stream().map(mapper).collect(Collectors.toList());
        target.page = page;
        target.limit = limit;
        return target;
    }

    @Override
    public Iterator<T> iterator() {
        if (items == null) {
            return ImmutableList.<T>of().iterator();
        }
        return items.iterator();
    }
}
