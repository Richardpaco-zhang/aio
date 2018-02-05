package rd.zhang.aio.kotlin.core.function

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import rd.zhang.aio.kotlin.core.*
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.aio.kotlin.core.http.ports.OkHttpCall
import rd.zhang.aio.kotlin.core.http.ports.OkHttpNextCall
import rd.zhang.android.aio.core.http.impl.OkHttpNextCallImpl

/**
 * Created by Richard on 2017/8/25.
 */

//请求顺序执行
fun order(httpCall: () -> OkHttpCall): OkHttpNextCall {
    return OkHttpNextCallImpl(httpCall)
}

//停止所有的网络访问
fun stopAllRequest() {
    var dispatcher = DataProvider.getOkHttp().dispatcher()
    for (call in dispatcher.queuedCalls()) {
        call.cancel()
    }
    for (call in dispatcher.runningCalls()) {
        call.cancel()
    }
}

//停止网络访问
fun stopRequestByTag(arg: Any) {
    var dispatcher = DataProvider.getOkHttp().dispatcher()
    dispatcher.queuedCalls()
            .filter { arg == it.request().tag() }
            .forEach { it.cancel() }
    dispatcher.runningCalls()
            .filter { arg == it.request().tag() }
            .forEach { it.cancel() }
}

@SuppressLint("MissingPermission")
fun isConnectWeb(): Boolean {
    if (DataProvider.startup.application.applicationContext != null) {
        val mConnectivityManager = DataProvider.startup.application.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable
        }
    }
    return false
}

@SuppressLint("MissingPermission")
        //获取当前网络类型，如果为空，返回无网络
fun getNetworkState(): Int {
    var context = getBaseContext()
    val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetInfo = connManager.activeNetworkInfo
    if (activeNetInfo == null || !activeNetInfo.isAvailable) {
        return NETWORK_NONE
    }
    val wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    if (null != wifiInfo) {
        val state = wifiInfo.state
        if (null != state)
            if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                return NETWORK_WIFI
            }
    }
    val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    if (null != networkInfo) {
        val state = networkInfo.state
        val strSubTypeName = networkInfo.subtypeName
        if (null != state)
            if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                when (activeNetInfo.subtype) {
                //2g
                    TelephonyManager.NETWORK_TYPE_GPRS // 联通2g
                        , TelephonyManager.NETWORK_TYPE_CDMA // 电信2g
                        , TelephonyManager.NETWORK_TYPE_EDGE // 移动2g
                        , TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> return NETWORK_2G
                //3g
                    TelephonyManager.NETWORK_TYPE_EVDO_A // 电信3g
                        , TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> return NETWORK_3G
                //4g
                    TelephonyManager.NETWORK_TYPE_LTE -> return NETWORK_4G
                    else ->
                        //中国移动 联通 电信 三种3G制式
                        if (strSubTypeName.equals("TD-SCDMA", ignoreCase = true)
                                || strSubTypeName.equals("WCDMA", ignoreCase = true)
                                || strSubTypeName.equals("CDMA2000", ignoreCase = true)) {
                            return NETWORK_3G
                        } else {
                            return NETWORK_MOBILE
                        }
                }
            }
    }
    return NETWORK_NONE
}