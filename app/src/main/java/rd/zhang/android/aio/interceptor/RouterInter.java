package rd.zhang.android.aio.interceptor;

import org.jetbrains.annotations.NotNull;

import rd.zhang.aio.kotlin.annotation.Interceptor;
import rd.zhang.aio.kotlin.core.aop.InterceptorCallback;
import rd.zhang.aio.kotlin.core.aop.RouterInterceptor;
import rd.zhang.android.aio.core.bean.Router;

/**
 * Created by Richard on 2017/9/10.
 */
@Interceptor(priority = 100)
public class RouterInter implements RouterInterceptor {
    @Override
    public void process(Router data, @NotNull InterceptorCallback<Router> callback) {

    }
}
