package rd.zhang.android.aio.core.http.impl

import rd.zhang.aio.kotlin.core.function.logi
import rd.zhang.aio.kotlin.core.http.ports.OkHttpCall
import rd.zhang.aio.kotlin.core.http.ports.OkHttpNextCall
import rd.zhang.aio.kotlin.core.http.ports.OrderCall
import rd.zhang.android.aio.core.listener.OnHttpExecuteListener

/**
 * Created by Richard on 2017/8/22.
 */
class OkHttpNextCallImpl(private var okhttpCall: () -> OkHttpCall) : OkHttpNextCall {

    private var orderArray: MutableList<OrderCall> = arrayListOf()

    private var tag: Any? = null

    private var index: Int = 0

    private var begin: Boolean = true

    override fun next(orderCall: OrderCall): OkHttpNextCall? {
        orderArray.add(orderCall)
        return this
    }

    override fun execute() {
        execute(false, null)
    }

    override fun execute(tag: Any?) {
        execute(false, tag)
    }

    override fun execute(stop: Boolean) {
        execute(stop, null)
    }


    override fun execute(stop: Boolean, tag: Any?) {
        if (orderArray.size > 0) {
            this.tag = tag
            startRequest(stop)
        } else {
            throw IllegalArgumentException("No OkHttpCall!")
        }
    }

    private fun startRequest(stop: Boolean) {
        var http: OkHttpCallImpl<*, *>? = null
        if (!begin) {
            var i = orderArray[index]
            if (i != null) {
                http = i.call() as OkHttpCallImpl<*, *>
            }
        } else {
            begin = false
            http = okhttpCall.invoke() as OkHttpCallImpl<*, *>
        }
        http?.executeByListener(object : OnHttpExecuteListener {
            override fun onInterrupt() {
                logi("Http order call interrupt!")
            }

            override fun onSuccess() {
                moveToIndex(stop)
            }

            override fun onFailed() {
                if (!stop) {
                    moveToIndex(stop)
                } else {
                    logi("Http order call finish!")
                }
            }
        })
    }

    private fun moveToIndex(stop: Boolean) {
        if (index != orderArray.size) {
            startRequest(stop)
        }

        if (index == orderArray.size) {
            logi("Http order call finish!")
        }

        index++
    }

}