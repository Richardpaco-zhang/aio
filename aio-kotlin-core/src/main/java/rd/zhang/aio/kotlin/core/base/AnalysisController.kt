package rd.zhang.aio.kotlin.core.base

import android.support.annotation.IdRes

/**
 * Created by Richard on 2017/9/13.
 */
interface AnalysisController {

    fun unbind()

    fun launcher()

    fun setText(@IdRes id: Int, text: String)

}