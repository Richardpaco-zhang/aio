package rd.zhang.android.aio.core.bean

import android.content.Context
import android.os.Bundle

/**
 * Created by Richard on 2017/8/17.
 */

class Router {

    private var delay: Int = 0
    private var context: Context? = null

    /**
     * 是否使用拦截器
     */
    private var interceptor = true
    private var bundle: Bundle? = null
    private var address: String = ""
    private var flags: Int = 0

    private var enterAnim: Int = 0
    private var exitAnim: Int = 0
    private var requestCode: Int = 0

    fun context(context: Context): Router {
        this.context = context
        return this
    }

    fun context(): Context? {
        return context
    }

    fun replaceBundle(bundle: Bundle) {
        if (bundle == null) {
            throw NullPointerException("Bundle must not be null!")
        }
        this.bundle = bundle
    }

    fun bundle(): Bundle {
        if (bundle == null) {
            bundle = Bundle()
        }
        return bundle!!
    }

    fun delay(): Int {
        return delay
    }

    fun interceptor(): Boolean {
        return interceptor
    }

    fun address(): String {
        return address
    }

    fun flags(): Int {
        return flags
    }

    fun enterAnim(): Int {
        return enterAnim
    }

    fun exitAnim(): Int {
        return exitAnim
    }

    fun requestCode(): Int {
        return requestCode
    }

    fun delay(delay: Int): Router {
        this.delay = delay
        return this
    }

    fun request(requestCode: Int): Router {
        this.requestCode = requestCode
        return this
    }

    fun interceptor(interceptor: Boolean): Router {
        this.interceptor = interceptor
        return this
    }

    fun address(address: String): Router {
        this.address = address
        return this
    }

    fun flags(flag: Int): Router {
        this.flags = flag
        return this
    }

    fun enterAnim(enterAnim: Int): Router {
        this.enterAnim = enterAnim
        return this
    }

    fun exitAnim(exitAnim: Int): Router {
        this.exitAnim = exitAnim
        return this
    }
}
