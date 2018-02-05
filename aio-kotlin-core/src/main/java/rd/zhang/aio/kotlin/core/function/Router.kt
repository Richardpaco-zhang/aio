package rd.zhang.aio.kotlin.core.function

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import rd.zhang.aio.kotlin.core.aop.InterceptorCallback
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.aio.kotlin.core.listener.RouterListener
import rd.zhang.aio.kotlin.core.router.RouterService
import rd.zhang.android.aio.core.bean.Router
import rd.zhang.android.aio.core.router.InterceptorService

/**
 * Created by Richard on 2017/9/10.
 */
fun router(router: String): RouterService = rd.zhang.aio.kotlin.core.function.RouterImpl(router)

private class RouterImpl : RouterService {

    constructor(address: String) {
        routerModel.address(address)
    }

    private var routerModel: Router = Router()

    override fun getRouter(): Router = routerModel

    override fun replace(bundle: Bundle): RouterService {
        routerModel.replaceBundle(bundle)
        return this
    }

    override fun with(key: String, value: Bundle): RouterService {
        routerModel.bundle().putBundle(key, value)
        return this
    }

    override fun with(key: String, value: String): RouterService {
        routerModel.bundle().putString(key, value)
        return this
    }

    override fun with(key: String, value: Int): RouterService {
        routerModel.bundle().putInt(key, value)
        return this
    }

    override fun with(key: String, value: Float): RouterService {
        routerModel.bundle().putFloat(key, value)
        return this
    }

    override fun with(key: String, value: Double): RouterService {
        routerModel.bundle().putDouble(key, value)
        return this
    }

    override fun with(key: String, value: Char): RouterService {
        routerModel.bundle().putChar(key, value)
        return this
    }

    override fun with(key: String, value: Boolean): RouterService {
        routerModel.bundle().putBoolean(key, value)
        return this
    }

    override fun with(key: String, value: Long): RouterService {
        routerModel.bundle().putLong(key, value)
        return this
    }

    override fun delay(delay: Int): RouterService {
        routerModel.delay(delay)
        return this
    }

    override fun anim(enterAnim: Int, exitAnim: Int): RouterService {
        routerModel.enterAnim(enterAnim).exitAnim(exitAnim)
        return this
    }

    override fun flags(vararg flags: Int): RouterService {
        return this
    }

    override fun request(requestCode: Int): RouterService {
        routerModel.request(requestCode)
        return this
    }

    override fun green(): RouterService {
        routerModel.interceptor(false)
        return this
    }

    override fun begin() {
        begin(DataProvider.startup.application.applicationContext, null)
    }

    override fun begin(listener: RouterListener?) {
        @Suppress
        begin(DataProvider.startup.application.applicationContext, listener)
    }

    override fun begin(context: Context) {
        begin(context, null)
    }

    override fun begin(context: Context, listener: RouterListener?) {
        if (routerModel.address() != null) {
            if (routerModel.address()!!.isNotEmpty()) {
                routerModel.context(context)
                routerModel.address(routerModel.address())
                execute(routerModel, listener)
            } else {
                throw NullPointerException("Router Address must not be null and length > 0!")
            }
        } else {
            throw NullPointerException("Router Address must not be null and length > 0!")
        }
    }

    private fun execute(router: Router, listener: RouterListener?) {
        //execute by interceptor
        if (router.interceptor()) {
            InterceptorService().execute(router, object : InterceptorCallback<Router> {
                override fun next(t: Router) {
                    beginRouter(t)
                }

                override fun interrupt(throwable: Throwable, customerCode: Int) {
                    listener?.onError(throwable, customerCode)
                }
            })
        } else {
            //execute normal
            beginRouter(router)
        }
    }

    private fun beginRouter(router: Router) {
        val address = DataProvider.startup.getRouterAddress()[router.address()]
        if (address != null) {
            val context = router.context()
            val intent = Intent(context, address)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtras(router.bundle())
            if (router.requestCode() != 0) {
                if (context is Activity) {
                    ActivityCompat.startActivityForResult(context, intent, router.requestCode(), null)
                } else {
                    throw IllegalArgumentException("Router must use begin(activity)")
                }
            } else {
                ActivityCompat.startActivity(context, intent, router.bundle())
            }

        }
    }
}