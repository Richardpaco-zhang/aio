package rd.zhang.aio.kotlin.core.analysis

import android.app.Activity
import android.view.View
import rd.zhang.aio.kotlin.core.support.AioAppFragment
import rd.zhang.aio.kotlin.core.support.AioFragment
import rd.zhang.aio.kotlin.core.support.AioLazyAppFragment
import rd.zhang.aio.kotlin.core.support.AioLazyFragment

/**
 * Created by Richard on 2017/9/8.
 */
class ViewFinder<T>(private var target: T, private var view: View?) {

    private var viewMap: MutableMap<Int, View> = hashMapOf()

    private var hostView: View? = null

    fun findView(id: Int): View {
        if (hostView == null) {
            if (view == null) {
                when (target) {
                    is Activity -> {
                        hostView = (target as Activity).window.decorView.findViewById(android.R.id.content)
                    }
                    is AioFragment -> {
                        hostView = (target as AioFragment).getRootView()
                    }
                    is AioAppFragment -> {
                        hostView = (target as AioAppFragment).getRootView()
                    }
                    is AioLazyAppFragment -> {
                        hostView = (target as AioLazyAppFragment).getRootView()
                    }
                    is AioLazyFragment -> {
                        hostView = (target as AioLazyFragment).getRootView()
                    }
                }
            } else {
                hostView = view!!
            }
        }
        var find = viewMap[id]
        if (find == null) {
            find = hostView!!.findViewById(id)
            if (find == null) {
                throw IllegalArgumentException("No find view by $id")
            }
            viewMap.put(id, find)
        }
        return find!!
    }

    fun clear() {
        viewMap.clear()
    }

}