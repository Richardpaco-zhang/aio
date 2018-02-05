package rd.zhang.aio.kotlin.core.support

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import rd.zhang.aio.kotlin.core.base.AnalysisController
import rd.zhang.aio.kotlin.core.permission.OnPermissionResultListener
import rd.zhang.aio.kotlin.core.service.RouterActivityManager

/**
 * Created by Richard on 2017/9/8.
 */
open class AioActivity : Activity() {

    private var bind: AnalysisController? = null

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

    fun setLockReturnKeyToPhonePage(lock: Boolean) {
        this.lockReturnKeyToPhonePage = lock
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}