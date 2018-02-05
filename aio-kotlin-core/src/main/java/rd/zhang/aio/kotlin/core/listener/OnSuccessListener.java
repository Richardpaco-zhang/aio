package rd.zhang.aio.kotlin.core.listener;

/**
 * Created by Richard on 2017/8/22.
 */
public interface OnSuccessListener<T> {

    void onSuccess(T data, boolean done);

}