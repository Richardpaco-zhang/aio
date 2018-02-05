package rd.zhang.aio.kotlin.core.router

import android.content.Context
import android.os.Bundle
import rd.zhang.aio.kotlin.core.listener.RouterListener
import rd.zhang.android.aio.core.bean.Router

/**
 * Created by Richard on 2017/8/22.
 */
interface RouterService {

    fun with(key: String, value: String): RouterService

    fun with(key: String, value: Int): RouterService

    fun with(key: String, value: Float): RouterService

    fun with(key: String, value: Double): RouterService

    fun with(key: String, value: Char): RouterService

    fun with(key: String, value: Boolean): RouterService

    fun with(key: String, value: Long): RouterService

    fun with(key: String, value: Bundle): RouterService

    fun replace(bundle: Bundle): RouterService

    fun delay(delay: Int): RouterService

    fun anim(enterAnim: Int, exitAnim: Int): RouterService

    fun flags(vararg flags: Int): RouterService

    fun request(requestCode: Int): RouterService

    fun green(): RouterService

    fun begin()

    fun begin(context: Context)

    fun begin(listener: RouterListener?)

    fun begin(context: Context, listener: RouterListener?)

    fun getRouter(): Router

}
