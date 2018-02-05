package rd.zhang.aio.kotlin.core.base

/**
 * Created by Richard on 2017/9/8.
 */
abstract class BaseAnalysisModel {

    /**
     * - onInit(layout,fullscreen,router data...)
     *
     * - onCreate(init views,register view event,request Permission)
     *
     * - onWired(init @Autowired filed)
     *
     * - onFinish(register event method & check sticky event & attach presenter View & execute @OnInit methods)
     *
     * - onExecute(execute @Launcher methods)
     *
     */

    abstract fun onInit()

    abstract fun onCreate()

    abstract fun onWired()

    abstract fun onFinish()

    abstract fun onExecute()

    abstract fun onUnbind()
}