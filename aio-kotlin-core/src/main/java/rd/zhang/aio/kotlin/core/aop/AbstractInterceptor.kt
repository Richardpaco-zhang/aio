package rd.zhang.aio.kotlin.core.aop

/**
 * Created by Richard on 2017/8/22.
 */
interface AbstractInterceptor<T> {

    fun process(data: T, callback: InterceptorCallback<T>)

}