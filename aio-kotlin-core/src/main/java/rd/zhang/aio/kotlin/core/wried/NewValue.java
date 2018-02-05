package rd.zhang.aio.kotlin.core.wried;

public abstract class NewValue<T> implements Factory<T> {

    public abstract T onNull();

    T data;

    @Override
    public T get() {
        if (data == null) {
            this.data = onNull();
        }
        return data;
    }
}