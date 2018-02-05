package rd.zhang.aio.kotlin.core.listener;

/**
 * Created by Richard on 2017/8/22.
 */
public interface RouterListener {

    void onError(Throwable throwable, int customerCode);

}