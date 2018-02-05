package rd.zhang.android.aio.app

import rd.zhang.aio.kotlin.annotation.Analysis
import rd.zhang.aio.kotlin.annotation.AppFunction
import rd.zhang.aio.kotlin.core.base.BaseAppStartup
import rd.zhang.aio.kotlin.core.support.AioApplication

/**
 * Created by Richard on 2017/9/8.
 */
@Analysis
@AppFunction(baseIp = "http://106.15.46.36:9080", dbName = "bim", dbVersion = 1, maxPoolSize = 2,
        debug = true,openService = true,
        dbPassphrase = "amFzby1iaW0tYnktcmljaGFyZC1rYW5nLWRldi1hdC1kYXRlLTIwMTcuOC4yNw==")
class App : AioApplication() {

    override fun getAppStartup(): BaseAppStartup {


        return AppStartup(this)
    }


}
