package rd.zhang.aio.kotlin.core.function

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.RequiresApi
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import rd.zhang.aio.kotlin.core.base.DataProvider
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created by Richard on 2017/9/11.
 */

//延迟执行
fun wait(second: Int, status: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        Handler().postDelayed(status, second.toLong() * 1000)
    } else {
        Thread.sleep(second.toLong() * 1000)
    }
}

//延迟执行
fun waitMillis(millis: Long, status: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        Handler().postDelayed(status, millis)
    } else {
        Thread.sleep(millis)
    }
}

//背景透明
fun backgroundAlpha(activity: Activity?, float: Float) {
    if (activity != null) {
        var anim = ValueAnimator.ofFloat(activity.window.attributes.alpha, float)
        anim.addUpdateListener {
            val lp = activity.window.attributes
            lp.alpha = it.animatedValue as Float
            activity.window.attributes = lp
        }
        anim.duration = 170
        anim.start()
    }
}

//隐藏键盘
fun hideKeyBoard(activity: Activity) {
    @SuppressLint("ServiceCast")
    var input = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    input.hideSoftInputFromWindow(activity.currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

//判断是否在后台
fun isBackground(context: Context): Boolean {
    var activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    var appProcess = activityManager.runningAppProcesses
    appProcess.filter { it.processName.equals(context.packageName) }
            .forEach { return it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND }
    return false
}

//获取字符串
fun getString(id: Int): String = DataProvider.startup.application.getString(id)

//获取颜色
@RequiresApi(Build.VERSION_CODES.M)
fun getColor(id: Int): Int = DataProvider.startup.application.getColor(id)

//获取屏幕宽度
fun getScreenW(): Int {
    if (screenW == -1) {
        screenW = DataProvider.startup.application.applicationContext.resources.displayMetrics.widthPixels
    }
    return screenW
}

//获取屏幕高度
fun getScreenH(): Int {
    if (screenH == -1) {
        screenH = DataProvider.startup.application.applicationContext.resources.displayMetrics.heightPixels
    }
    return screenH
}

//获取状态栏高度
fun getStatusBarH(): Int {
    if (statusBarH == -1) {
        try {
            val clazz = Class.forName("com.android.internal.R\$dimen")
            val `object` = clazz.newInstance()
            val height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(`object`).toString())
            statusBarH = DataProvider.startup.application.applicationContext.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return statusBarH
}

//获取App名称
fun getAppName(): String {
    if (appName.equals("")) {
        var pm = getPackageManager()
        var info = pm.getApplicationInfo(DataProvider.startup.application.applicationContext.packageName, 0)
        appName = pm.getApplicationLabel(info).toString()
    }
    return appName
}

//获取版本号
fun getAppVersionCode(): Int {
    if (versionCode == -1) {
        versionCode = getPackageInfo().versionCode
    }
    return versionCode
}

//获取版本名
fun getAppVersionName(): String {
    if (versionName.equals("")) {
        versionName = getPackageInfo().versionName
    }
    return versionName
}

//md5加密
fun md5(str: String): String? {
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        val digest: ByteArray = instance.digest(str.toByteArray())
        var sb: StringBuffer = StringBuffer()
        for (b in digest) {
            var i: Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return null
}

//base64加密
fun base64Encode(str: String): String = Base64.encodeToString(str.toByteArray(), Base64.DEFAULT)

//base64解密
fun base64Decode(str: String): String = Base64.decode(str.toByteArray(), Base64.DEFAULT).toString()

fun base64ToBitmap(base64Data: String): Bitmap {
    val bytes = Base64.decode(base64Data, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun getBaseContext(): Context = DataProvider.startup.application.applicationContext

//------------------------------------------------------------------------------------

private fun getPackageManager(): PackageManager {
    var context = DataProvider.startup.application.applicationContext
    return context.packageManager
}

private fun getPackageInfo(): PackageInfo {
    return getPackageManager().getPackageInfo(DataProvider.startup.application.applicationContext.packageName,
            PackageManager.GET_CONFIGURATIONS)
}

private var appName: String = ""
private var screenH: Int = -1
private var screenW: Int = -1
private var statusBarH: Int = -1
private var versionCode: Int = -1
private var versionName: String = ""