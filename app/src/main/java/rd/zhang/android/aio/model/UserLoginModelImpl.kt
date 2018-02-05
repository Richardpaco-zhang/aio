package rd.zhang.android.aio.model

import rd.zhang.aio.kotlin.annotation.EmployHost
import rd.zhang.aio.kotlin.core.function.logi

/**
 * Created by Richard on 2017/9/8.
 */
@EmployHost
class UserLoginModelImpl(var host: Any) : UserLoginModel {
    override fun startlogin() {
        logi("startlogin")
    }
}