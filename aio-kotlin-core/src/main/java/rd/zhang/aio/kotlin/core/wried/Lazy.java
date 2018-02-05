package rd.zhang.aio.kotlin.core.wried;

/**
 * Created by Richard on 2017/8/20.
 */

public interface Lazy<T> {

    boolean init();

    T get();
}
