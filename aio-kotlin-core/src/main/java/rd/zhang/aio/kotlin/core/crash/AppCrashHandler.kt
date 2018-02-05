package rd.zhang.aio.kotlin.core.crash

import rd.zhang.aio.kotlin.core.function.logi

/**
 * Created by Richard on 2017/10/4.
 */
class AppCrashHandler : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        logi("exception from ${thread.id} message ${throwable.message}")
    }
}
