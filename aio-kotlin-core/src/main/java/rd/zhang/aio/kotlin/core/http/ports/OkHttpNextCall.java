package rd.zhang.aio.kotlin.core.http.ports;

/**
 * Created by Richard on 2017/8/22.
 */
public interface OkHttpNextCall {

    OkHttpNextCall next(OrderCall orderCall);

    void execute();

    void execute(boolean stop);

    void execute(Object tag);

    void execute(boolean stop, Object tag);
}