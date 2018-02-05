package rd.zhang.aio.kotlin.core.http.ports;

/**
 * Created by Richard on 2017/8/22.
 */
public interface OkHttpCall {

    void execute();

    void execute(Object tag);

}