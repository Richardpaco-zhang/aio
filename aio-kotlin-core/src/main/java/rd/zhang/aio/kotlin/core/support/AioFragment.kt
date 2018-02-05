package rd.zhang.aio.kotlin.core.support

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rd.zhang.aio.kotlin.core.base.AnalysisController
import rd.zhang.aio.kotlin.core.function.bind

/**
 * Created by Richard on 2017/9/8.
 */

open class AioFragment : Fragment() {
    private var inflater: LayoutInflater? = null
    private var container: ViewGroup? = null
    private var savedInstanceState: Bundle? = null
    private var rootView: View? = null
    private var baseLayoutId: Int = -1
    private var unbind: AnalysisController? = null

    fun setContentView(id: Int) {
        if (rootView == null) {
            this.baseLayoutId = id
            rootView = inflater!!.inflate(id, container, false)
        }
    }

    fun getRootView(): View = rootView!!

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (unbind == null) {
            this.inflater = inflater
            this.container = container
            this.savedInstanceState = savedInstanceState
            unbind = bind(this)
        }
        if (baseLayoutId == -1) {
            throw IllegalArgumentException("You must set fragment layout!")
        }
        return rootView!!
    }

    override fun onDestroy() {
        super.onDestroy()
        if (unbind != null) {
            unbind!!.unbind()
        }
    }
}
