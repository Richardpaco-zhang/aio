package rd.zhang.aio.kotlin.core.support

import rd.zhang.aio.kotlin.core.base.BaseAnalysisModule

/**
 * Created by Richard on 2017/9/11.
 */
abstract class AioModuleLoader {

    abstract fun getModule(): BaseAnalysisModule

}