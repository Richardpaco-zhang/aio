package rd.zhang.aio.kotlin.core.function

import android.view.View
import rd.zhang.aio.kotlin.core.analysis.AbstractAnalysisModel
import rd.zhang.aio.kotlin.core.base.AnalysisController
import rd.zhang.aio.kotlin.core.base.DataProvider

/**
 * Created by Richard on 2017/9/8.
 */
fun bind(obj: Any): AnalysisController? = bind(obj, null, true)

fun bind(obj: Any, view: View?) = bind(obj, view, true)

fun manual(obj: Any, view: View?): AnalysisController? = bind(obj, view, false)

fun manual(obj: Any): AnalysisController? = bind(obj, null, false)

private fun bind(obj: Any, view: View?, isLauncher: Boolean): AnalysisController? {
    var clazz = DataProvider.startup.getAMapping()[obj.javaClass]
    if (clazz != null) {
        var injector = clazz.newInstance()
        if (injector != null) {
            injector = injector as? AbstractAnalysisModel<Any>
            if (injector != null) {
                return injector.bind(obj, view, isLauncher)
            }
        }
    } else {
        throw IllegalArgumentException("Please join @Analysis at your class!")
    }
    return null
}