package rd.zhang.aio.kotlin.core.listener;

/**
 * Created by Richard on 2017/8/22.
 */
public interface OnFailedListener<E> {

    void onFailed(Throwable throwable, E data, int code);

}