package rd.zhang.aio.kotlin.core.function

import android.os.Looper
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.aio.kotlin.core.bean.PostingEvent
import rd.zhang.aio.kotlin.core.event.StickyModel

/**
 * Created by Richard on 2017/9/9.
 */
//粘性事件
fun sticky(arg: Any) {
    sticky(null, arg)
}

//粘性事件
fun sticky(appoint: String?, arg: Any) {
    DataProvider.stickyEvents?.put(arg.javaClass, StickyModel().setAppoint(appoint).setArg(arg))
    //post(appoint, arg)
}

//发送事件
fun post(arg: Any) {
    post(null, arg)
}

//发送事件
fun post(appoint: String?, arg: Any) {
    var postingState = DataProvider.postingThreadState.get()
    var listEvent = postingState.eventQueue
    listEvent.add(PostingEvent().setAppoint(appoint).setArg(arg))
    if (!postingState.isPosting) {
        postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper()
        postingState.isPosting = true
        try {
            while (!listEvent.isEmpty()) {
                var event = postingState.eventQueue.removeAt(0)
                for ((key, value) in DataProvider.eventTypes) {
                    for (item in value) {
                        if (item.paramType == event.arg.javaClass) {
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
                                DataProvider.registerObjs
                                        .filter { it.javaClass == key }
                                        .forEach {
                                            if (item.isMainThread) {
                                                if (postingState.isMainThread) {
                                                    item.method.invoke(it, event.arg)
                                                } else {
                                                    DataProvider.delivery.post { item.method.invoke(it, event.arg) }
                                                }
                                            } else {
                                                item.method.invoke(it, event.arg)
                                            }
                                        }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            postingState.isPosting = false
            postingState.isMainThread = false
        }
    }
}
