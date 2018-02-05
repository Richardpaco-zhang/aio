package rd.zhang.aio.kotlin.core.support

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import rd.zhang.aio.kotlin.core.base.AnalysisController
import rd.zhang.aio.kotlin.core.permission.OnPermissionResultListener
import rd.zhang.aio.kotlin.core.service.RouterActivityManager


/**
 * Created by Richard on 2017/9/8.
 */
open class AioAppActivity : AppCompatActivity() {

    private var bind: AnalysisController? = null

    fun setText(@IdRes id: Int, text: String) {
        if (bind != null) {
            bind!!.setText(id, text)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bind == null) {
            bind = rd.zhang.aio.kotlin.core.function.bind(this)
            RouterActivityManager.getActivityManager().pushActivity(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bind != null) {
            bind!!.unbind()
            RouterActivityManager.getActivityManager().destroyActivity(this)
        }
    }

    private var lockReturnKeyToPhonePage: Boolean = false

    fun setLockReturnKeyToPhonePage(lock: Boolean) {
        this.lockReturnKeyToPhonePage = lock
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (lockReturnKeyToPhonePage) {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private lateinit var resultListener: OnPermissionResultListener

    fun setPermissionResultListener(resultListener: OnPermissionResultListener) {
        this.resultListener = resultListener
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (resultListener != null) {
            resultListener.onResult(requestCode, permissions, grantResults)
        }
    }

}