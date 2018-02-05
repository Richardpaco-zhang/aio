package rd.zhang.aio.kotlin.core.function

import rd.zhang.aio.kotlin.core.base.BaseAppStartup
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.aio.kotlin.core.service.RouterActivityManager
import rd.zhang.aio.kotlin.core.support.AioActivity
import rd.zhang.aio.kotlin.core.support.AioAppActivity

/**
 * Created by Richard on 2017/9/10.
 */
fun init(startup: BaseAppStartup) {
    if (startup.dbName == null) {
        throw IllegalArgumentException("please set dbName!")
    } else {
        if (startup.dbName?.equals("")!!) {
            throw IllegalArgumentException("please set dbName!")
        }
    }
    if (startup.dbPassphrase == null) {
        throw IllegalArgumentException("please set dbPassphrase!")
    } else {
        if (startup.dbPassphrase?.equals("")!!) {
            throw IllegalArgumentException("please set dbPassphrase!")
        }
    }
    DataProvider.init(startup)
}

fun <T : AioActivity> T.finishAllActivityExceptThis() {
    RouterActivityManager.getActivityManager().popAllActivityExceptOne(this)
}

fun <T : AioAppActivity> T.finishAllActivityExceptThis() {
    RouterActivityManager.getActivityManager().popAllActivityExceptOne(this)
}

fun putAppInt(key: String, value: Int) {
    var editor = DataProvider.getSharedEditor()
    editor.putInt(key, value)
    editor.commit()
}

fun getAppInt(key: String): Int = DataProvider.getShared().getInt(key, -1)

fun putAppString(key: String, value: String) {
    var editor = DataProvider.getSharedEditor()
    editor.putString(key, value)
    editor.commit()
}

fun getAppString(key: String): String = DataProvider.getShared().getString(key, "")

fun putAppFloat(key: String, value: Float) {
    var editor = DataProvider.getSharedEditor()
    editor.putFloat(key, value)
    editor.commit()
}

fun getAppFloat(key: String): Float = DataProvider.getShared().getFloat(key, (-1).toFloat())

