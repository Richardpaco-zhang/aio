package rd.zhang.aio.kotlin.core.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import rd.zhang.aio.kotlin.core.R
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.aio.kotlin.core.ext.NotifyInfo
import rd.zhang.aio.kotlin.core.function.getStatusBarH
import java.util.*

/**
 * Created by Richard on 2017/10/18.
 */
class AioCoreService : Service() {

    private var serviceBinder: ServiceBinder? = null
    private var notifyMessageEvents: MutableList<NotifyInfo>? = null
    private var mWindowManager: WindowManager? = null
    private var wmParams: WindowManager.LayoutParams? = null
    private var notifyView: View? = null
    private var notifyContentPanel: View? = null
    private var notifyContentView: View? = null
    private var contentParams: LinearLayout.LayoutParams? = null
    private var notifyShowText: TextView? = null
    private var icon: ImageView? = null
    private var indexOfNotify: NotifyInfo? = null
    private var isPosting = false

    private val notifyRunnable = Runnable {
        try {
            mWindowManager!!.removeView(notifyView)
            try {
                if (indexOfNotify!!.dismiss != null) {
                    indexOfNotify!!.dismiss!!.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                indexOfNotify!!.dismiss(null)
            }
        } finally {
            if (notifyMessageEvents!!.size == 0) {
                isPosting = false
            } else {
                startNotifyHandlerProcess()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notifyMessageEvents = ArrayList()
        initWindowManager()
    }

    private fun initWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
            wmParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)
            wmParams!!.gravity = Gravity.LEFT or Gravity.TOP
            wmParams!!.x = 0
            wmParams!!.y = 0
            wmParams!!.windowAnimations = R.style.aio_notify_anim
            notifyView = LayoutInflater.from(this).inflate(R.layout.aio_notify_layout, null)
            icon = notifyView!!.findViewById(R.id.image)
            notifyContentPanel = notifyView!!.findViewById(R.id.contentPanel)
            notifyShowText = notifyView!!.findViewById(R.id.textView)
            notifyContentView = notifyView!!.findViewById(R.id.content)
            contentParams = notifyContentView!!.layoutParams as LinearLayout.LayoutParams
            val height = getStatusBarH()
            contentParams!!.topMargin = height
            notifyContentView!!.layoutParams = contentParams
            addViewClickListener()
        }
    }

    private fun startNotifyHandlerProcess() {
        isPosting = true
        indexOfNotify = notifyMessageEvents!!.removeAt(0)
        if (indexOfNotify!!.text != null) {
            notifyShowText!!.text = indexOfNotify!!.text + ""
        } else {
            notifyShowText!!.text = getString(indexOfNotify!!.textRes) + ""
        }
        changeNotifyBackgroundAndIcon(indexOfNotify!!)
        initWindowManager()
        mWindowManager!!.addView(notifyView, wmParams)
        if (indexOfNotify!!.show != null) {
            DataProvider.delivery.postDelayed({
                indexOfNotify!!.show!!.invoke()
            }, indexOfNotify!!.showedTime)
        }
        DataProvider.delivery.postDelayed(notifyRunnable, indexOfNotify!!.time.toLong())
    }

    private fun changeNotifyBackgroundAndIcon(info: NotifyInfo) {
        if (info.backgroundColor != 0) {
            notifyContentPanel!!.setBackgroundColor(this.resources.getColor(info.backgroundColor))
        }
        if (info.icon != 0) {
            icon!!.setImageResource(info.icon)
        }
        if (info.iconColor != 0) {
            icon!!.setColorFilter(resources.getColor(info.iconColor))
        }
        if (info.textColor != 0) {
            notifyShowText!!.setTextColor(resources.getColor(info.textColor))
        }
    }

    private fun addViewClickListener() {
        notifyContentPanel!!.setOnClickListener {
            if (indexOfNotify != null) {
                DataProvider.delivery.removeCallbacks(notifyRunnable)
                try {
                    mWindowManager!!.removeView(notifyView)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (indexOfNotify!!.listener != null) {
                        indexOfNotify!!.listener!!.onClick()
                        try {
                            if (indexOfNotify!!.dismiss != null) {
                                indexOfNotify!!.dismiss!!.invoke()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            indexOfNotify!!.dismiss(null)
                        }
                    }
                    if (notifyMessageEvents!!.size == 0) {
                        isPosting = false
                    } else {
                        startNotifyHandlerProcess()
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        if (serviceBinder == null) {
            serviceBinder = ServiceBinder()
        }
        return serviceBinder
    }

    inner class ServiceBinder : Binder() {

        fun showMessage(info: NotifyInfo) {
            this@AioCoreService.showNotifyMessage(info)
        }

    }

    private fun showNotifyMessage(info: NotifyInfo) {
        if (notifyView != null) {
            notifyMessageEvents!!.add(info)
            if (!isPosting) {
                startNotifyHandlerProcess()
            }
        }
    }
}
