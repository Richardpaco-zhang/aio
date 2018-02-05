package rd.zhang.android.aio.core.http.impl

import rd.zhang.aio.kotlin.core.http.build.RequestImpl
import rd.zhang.aio.kotlin.core.http.ports.OkHttpCall
import rd.zhang.aio.kotlin.core.listener.OnFailedListener
import rd.zhang.aio.kotlin.core.listener.OnOfflineListener
import rd.zhang.aio.kotlin.core.listener.OnStartListener
import rd.zhang.aio.kotlin.core.listener.OnSuccessListener
import rd.zhang.android.aio.core.http.process.OkHttpProcess
import rd.zhang.android.aio.core.listener.OnHttpExecuteListener


/**
 * Created by Richard on 2017/8/23.
 */
class OkHttpCallImpl<T, E> : OkHttpCall {

    var build: RequestImpl.Build? = null
    private var onStartListener: OnStartListener? = null
    private var onSuccessListener: OnSuccessListener<T>? = null
    private var onFailedListener: OnFailedListener<E>? = null
    private var onOfflineListener: OnOfflineListener? = null

    fun setRequestInfo(build: RequestImpl.Build, onStartListener: OnStartListener,
                       onSuccessListener: OnSuccessListener<T>, onFailedListener: OnFailedListener<E>,
                       offlineListener: OnOfflineListener?): OkHttpCall {
        this.build = build
        this.onStartListener = onStartListener
        this.onSuccessListener = onSuccessListener
        this.onFailedListener = onFailedListener
        this.onOfflineListener = offlineListener
        return this
    }

    override fun execute() {
        execute(null)
    }

    fun executeByListener(onHttpExecuteListener: OnHttpExecuteListener?) {
        OkHttpProcess(build!!, onStartListener!!,
                onSuccessListener!!, onFailedListener!!,
                onOfflineListener).setExecuteListener(onHttpExecuteListener).start()
    }

    override fun execute(tag: Any?) {
        if (tag != null) {
            this.build?.tag(tag)
        }
        executeByListener(null)
    }
}