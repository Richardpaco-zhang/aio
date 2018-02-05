package rd.zhang.aio.kotlin.core.function

import rd.zhang.aio.kotlin.core.R
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.aio.kotlin.core.callback.SingleTransport
import rd.zhang.aio.kotlin.core.ext.NotifyInfo
import rd.zhang.aio.kotlin.core.listener.OnNotifyClickListener

/**
 * Created by Richard on 2017/9/24.
 */

fun notify(): Builder = Builder()

fun notifyInfo(message: String): Builder = Builder().bgColor(R.color.notify_info).icon(R.drawable.ic_notify_message)
        .text(message).textColor(R.color.notify_white).time(3000)

fun notifyError(message: String): Builder = Builder().bgColor(R.color.notify_error).icon(R.drawable.ic_notify_error)
        .text(message).textColor(R.color.notify_white).time(3000)

fun notifySuccess(message: String): Builder = Builder().bgColor(R.color.notify_success).icon(R.drawable.ic_notify_finish)
        .text(message).textColor(R.color.notify_white).time(3000)

fun notifyWarn(message: String): Builder = Builder().bgColor(R.color.notify_warn).icon(R.drawable.ic_notify_warn)
        .text(message).textColor(R.color.notify_white).time(3000)

//显示notify
fun <T : Builder> T.show() {
    DataProvider.getServiceBinder(SingleTransport {
        it.showMessage(info)
    })
}

fun <T : Builder> T.onShowed(time: Long, operate: () -> Unit): Builder {
    info.show(time, operate)
    return this
}

//当前notify退出时
fun <T : Builder> T.onDismiss(operate: () -> Unit): Builder {
    info.dismiss(operate)
    return this
}

//用户点击notify时
fun <T : Builder> T.onClick(operate: () -> Unit): Builder {
    info.listener(OnNotifyClickListener {
        operate.invoke()
    })
    return this
}

class Builder {

    var info: NotifyInfo = NotifyInfo()

    fun textColor(textColor: Int): Builder {
        info.textColor(textColor)
        return this
    }

    fun bgColor(backgroundColor: Int): Builder {
        info.backgroundColor(backgroundColor)
        return this
    }

    fun iconColor(iconColor: Int): Builder {
        info.iconColor(iconColor)
        return this
    }

    fun icon(icon: Int): Builder {
        info.icon(icon)
        return this
    }

    fun textRes(textRes: Int): Builder {
        info.textRes(textRes)
        return this
    }

    fun text(text: String): Builder {
        info.text(text)
        return this
    }

    fun time(time: Int): Builder {
        info.time(time)
        return this
    }

}