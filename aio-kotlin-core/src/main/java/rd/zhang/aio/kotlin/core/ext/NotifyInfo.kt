package rd.zhang.aio.kotlin.core.ext

import rd.zhang.aio.kotlin.core.listener.OnNotifyClickListener

/**
 * Created by Richard on 2017/10/18.
 */

class NotifyInfo {

    var backgroundColor: Int = 0
        private set
    var icon: Int = 0
        private set
    var iconColor: Int = 0
        private set
    var textColor: Int = 0
        private set
    var textRes: Int = 0
        private set
    var text: String? = null
        private set
    var time: Int = 0
        private set
    var listener: OnNotifyClickListener? = null
        private set
    var dismiss: (() -> Unit?)? = null
        private set
    var show: (() -> Unit?)? = null
        private set
    var showedTime: Long = 1000
        private set

    fun dismiss(dismiss: (() -> Unit)?): NotifyInfo {
        this.dismiss = dismiss
        return this
    }

    fun show(time: Long, show: (() -> Unit)?): NotifyInfo {
        this.show = show
        this.showedTime = time
        return this
    }

    fun listener(listener: OnNotifyClickListener): NotifyInfo {
        this.listener = listener
        return this
    }

    fun textColor(textColor: Int): NotifyInfo {
        this.textColor = textColor
        return this
    }

    fun backgroundColor(backgroundColor: Int): NotifyInfo {
        this.backgroundColor = backgroundColor
        return this
    }

    fun iconColor(iconColor: Int): NotifyInfo {
        this.iconColor = iconColor
        return this
    }

    fun icon(icon: Int): NotifyInfo {
        this.icon = icon
        return this
    }

    fun textRes(textRes: Int): NotifyInfo {
        this.textRes = textRes
        return this
    }

    fun text(text: String): NotifyInfo {
        this.text = text
        return this
    }

    fun time(time: Int): NotifyInfo {
        this.time = time
        return this
    }
}
