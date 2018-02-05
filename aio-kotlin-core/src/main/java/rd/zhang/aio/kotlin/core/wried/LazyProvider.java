package rd.zhang.aio.kotlin.core.wried;

/**
 * Created by Richard on 2017/7/20.
 */

public class LazyProvider<T> implements Lazy<T> {

    private boolean isLoaded = false;
    Factory<T> factory;
    T data;

    public LazyProvider(Factory<T> factory) {
        this.factory = factory;
    }

    @Override
    public boolean init() {
        return isLoaded;
    }

    @Override
    public T get() {
        if (data == null) {
            data = factory.get();
            isLoaded = true;
        }
        return data;
    }

    public static <T> Lazy<T> create(Factory<T> factory) {
        return new LazyProvider<>(factory);
    }
}
