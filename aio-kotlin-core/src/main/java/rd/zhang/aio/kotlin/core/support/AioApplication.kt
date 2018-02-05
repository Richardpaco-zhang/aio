package rd.zhang.aio.kotlin.core.support

import android.app.Application
import rd.zhang.aio.kotlin.core.base.BaseAppStartup
import rd.zhang.aio.kotlin.core.function.init

/**
 * Created by Richard on 2017/9/8.
 */
abstract class AioApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        init(getAppStartup())
    }

    abstract fun getAppStartup(): BaseAppStartup

}