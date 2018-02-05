package rd.zhang.aio.kotlin.core.analysis

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import rd.zhang.aio.kotlin.annotation.Danger
import rd.zhang.aio.kotlin.core.base.AnalysisController
import rd.zhang.aio.kotlin.core.base.BaseAnalysisModel
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.aio.kotlin.core.event.EventModel
import rd.zhang.aio.kotlin.core.listener.TextChangeListener
import rd.zhang.aio.kotlin.core.listener.ViewClickListener
import rd.zhang.aio.kotlin.core.permission.OnPermissionResultListener
import rd.zhang.aio.kotlin.core.permission.PermissionUtils
import rd.zhang.aio.kotlin.core.support.*
import rd.zhang.aio.kotlin.core.views._SwipeBackLayout

/**
 * Created by Richard on 2017/9/8.
 */
abstract class AbstractAnalysisModel<T> : BaseAnalysisModel(), AnalysisController {

    private var target: T? = null
    private var view: View? = null
    private lateinit var viewFinder: ViewFinder<T>
    private var eventMethods: MutableList<EventModel> = arrayListOf()
    private var eventHost: Any? = null
    private var isLauncher: Boolean = true

    override fun launcher() {
        onExecute()
    }

    override fun unbind() {
        onUnbind()
        viewFinder.clear()
    }

    override fun setText(id: Int, text: String) {
        var view: View? = findView(id)
        if (view != null) {
            (view as TextView).text = text
        }
    }

    fun asWired() {
        onWired()
    }

    fun asContinue() {
        onFinish()
    }

    private fun sticky() {
        if (eventHost != null) {
            var methods = DataProvider.eventTypes[eventHost!!.javaClass]
            if (methods != null) {
                for (item in methods) {
                    var event = DataProvider.stickyEvents!![item.paramType]
                    if (event != null) {
                        try {
                            var isEvent = false
                            if (item.appoint == null) {
                                if (event.appoint == null) {
                                    isEvent = true
                                }
                            }
                            if (!isEvent && item.appoint == event.appoint) {
                                isEvent = true
                            }
                            if (isEvent) {
                                item.method.invoke(eventHost, event.arg)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            DataProvider.stickyEvents?.remove(item.paramType)
                        }
                    }
                }
            }
        }
    }

    fun asLauncher() {
        sticky()
        if (isLauncher) {
            launcher()
        }
    }

    fun bind(target: T, view: View?, isLauncher: Boolean): AnalysisController {
        this.target = target
        this.view = view
        this.isLauncher = isLauncher
        viewFinder = ViewFinder(target, view)
        onInit()
        onCreate()
        return this
    }

    fun findView(id: Int): View {
        return viewFinder.findView(id)
    }

    fun host(): T = target!!

    fun isEventMethodNull(clazz: Class<*>): Boolean = DataProvider.eventTypes[clazz] == null

    fun layout(id: Int) {
        when {
            host() is Activity -> (host() as Activity).setContentView(id)
            host() is AioAppFragment -> (host() as AioAppFragment).setContentView(id)
            host() is AioFragment -> (host() as AioFragment).setContentView(id)
            host() is AioLazyFragment -> (host() as AioLazyFragment).setContentView(id)
            host() is AioLazyAppFragment -> (host() as AioLazyAppFragment).setContentView(id)
        }
    }

    fun taskRootFinish() {
        if (host() is Activity) {
            if (!(host() as Activity).isTaskRoot) {
                (host() as Activity).finish()
            }
        }
    }

    fun openSwipeBack() {
        if (host() is Activity) {
            _SwipeBackLayout(host() as Activity).bindActivity(host() as Activity)
        }
    }

    fun backKeyToPhone() {
        if (host() is AioActivity) {
            (host() as AioActivity).setLockReturnKeyToPhonePage(true)
        } else if (host() is AioAppActivity) {
            (host() as AioAppActivity).setLockReturnKeyToPhonePage(true)
        }
    }

    fun windowFeatureNoTitle() {
        if (host() is Activity) {
            (host() as Activity).requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }

    fun fullscreen() {
        if (host() is Activity) {
            (host() as Activity).window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun clicks(viewClickListener: ViewClickListener, vararg ids: Int) {
        ids.forEach {
            findView(it)?.setOnClickListener {
                viewClickListener.onClick()
            }
        }
    }

    fun changes(textChangeListener: TextChangeListener, vararg ids: Int) {
        ids.forEach {
            (findView(it) as EditText).addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textChangeListener.onTextChange()
                }
            })
        }
    }

    fun permissions(listener: PermissionUtils.OnPermissionRequestResponseListener, vararg dangers: Danger) {
        if (host() is AioActivity) {
            val permissionUtils = PermissionUtils(host() as AioActivity)

            (host() as AioActivity).setPermissionResultListener(OnPermissionResultListener { requestCode, permissions, grantResults ->
                permissionUtils.requestPermissionsResult(requestCode, permissions, grantResults, listener)
            })

            permissionUtils.requestPermissions(dangers, listener)

        } else if (host() is AioAppActivity) {
            val permissionUtils = PermissionUtils(host() as AioAppActivity)

            (host() as AioAppActivity).setPermissionResultListener(OnPermissionResultListener { requestCode, permissions, grantResults ->
                permissionUtils.requestPermissionsResult(requestCode, permissions, grantResults, listener)
            })

            permissionUtils.requestPermissions(dangers, listener)


        }
    }

    fun addEventMethods(eventModel: EventModel) {
        eventMethods.add(eventModel)
    }

    fun enabledEventBus(host: Any) {
        if (isEventMethodNull(host.javaClass)) {
            DataProvider.eventTypes.put(host.javaClass, eventMethods)
        }
        eventHost = host
        DataProvider.registerObjs.add(eventHost!!)
    }

    fun disableEventBus(host: Any) {
        DataProvider.registerObjs.remove(host)
    }
}