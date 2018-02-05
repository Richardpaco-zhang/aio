package rd.zhang.aio.kotlin.core.aop;

/**
 * Created by Richard on 2017/8/18.
 */

public interface InterceptorCallback<T> {

    void next(T t);

    void interrupt(Throwable throwable, int customerCode);
}
