package rd.zhang.android.aio.core.router

import rd.zhang.aio.kotlin.core.aop.InterceptorCallback
import rd.zhang.aio.kotlin.core.aop.RouterInterceptor
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.android.aio.core.bean.Router

/**
 * Created by Richard on 2017/8/22.
 */
class InterceptorService {

    /**
     * -1 code -> cannot execute

     * @param router
     * *
     * @param callback
     */
    fun execute(router: Router, callback: InterceptorCallback<Router>) {
        val interceptors = DataProvider.startup.getRouterIt()
        if (interceptors.isNotEmpty()) {
            val index = intArrayOf(0)
            routerInterceptorInstance(interceptors.get(index[0]))?.process(router, object : InterceptorCallback<Router> {
                override fun next(t: Router) {
                    index[0]++
                    if (index[0] < interceptors.size) {
                        try {
                            routerInterceptorInstance(interceptors.get(index[0]))?.process(t, this)
                        } catch (e: InstantiationException) {
                            callback.interrupt(e, -1)
                        } catch (e: IllegalAccessException) {
                            callback.interrupt(e, -1)
                        }

                    } else {
                        callback.next(t)
                    }
                }

                override fun interrupt(throwable: Throwable, customerCode: Int) {
                    callback.interrupt(throwable, customerCode)
                }
            })
        } else {
            callback.next(router)
        }
    }

    private fun routerInterceptorInstance(clazz: Class<*>): RouterInterceptor? {
        var instance = clazz.newInstance()
        if (instance != null) {
            if (instance is RouterInterceptor) {
                return instance
            }
        }
        return null
    }


}