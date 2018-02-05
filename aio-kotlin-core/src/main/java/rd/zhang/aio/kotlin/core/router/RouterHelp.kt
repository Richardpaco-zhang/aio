package rd.zhang.aio.kotlin.core.router

import android.content.Intent

/**
 * Created by Richard on 2017/10/19.
 */
fun <T : Intent> T.fetchString(requestCode: Int, resultCode: Int, key: String, operate: (String) -> Unit) {
    if (requestCode == resultCode) {
        var str = this.getStringExtra(key)
        if (str != null) {
            operate.invoke(str)
        }
    }
}