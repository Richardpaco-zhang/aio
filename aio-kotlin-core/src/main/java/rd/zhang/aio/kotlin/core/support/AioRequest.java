package rd.zhang.aio.kotlin.core.support;


import rd.zhang.aio.kotlin.core.http.ports.OkHttpCall;
import rd.zhang.aio.kotlin.core.listener.OnFailedListener;
import rd.zhang.aio.kotlin.core.listener.OnOfflineListener;
import rd.zhang.aio.kotlin.core.listener.OnStartListener;
import rd.zhang.aio.kotlin.core.listener.OnSuccessListener;

/**
 * Created by Richard on 2017/8/22.
 */
public interface AioRequest<T, E> {

    OkHttpCall listener(OnStartListener onStartListener,
                        OnSuccessListener<T> onSuccessListener,
                        OnFailedListener<E> onFailedListener);

    /**
     * 如果offline listener != null的情况下
     * 那么在离线的时候不会调用onSuccess和onFailed！
     */
    OkHttpCall listener(OnStartListener onStartListener,
                        OnSuccessListener<T> onSuccessListener,
                        OnFailedListener<E> onFailedListener,
                        OnOfflineListener offlineListener);

}