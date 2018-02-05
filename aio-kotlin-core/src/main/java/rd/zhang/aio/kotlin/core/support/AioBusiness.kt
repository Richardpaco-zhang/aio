package rd.zhang.aio.kotlin.core.support

import rd.zhang.aio.kotlin.core.base.AnalysisController

/**
 * Created by Richard on 2017/9/8.
 */
open class AioBusiness<T> : AioPresenter {

    private var iView: T? = null
    private var bind: AnalysisController? = null

    fun iView(): T? = iView

    override fun attachView(host: Any) {
        bind = rd.zhang.aio.kotlin.core.function.bind(this)
        this.iView = host as T
    }

    override fun detachView() {
        if (bind != null) {
            bind!!.unbind()
        }
        iView = null
    }

    fun getTag(): Any = this
}