package rd.zhang.aio.kotlin.core.support

/**
 * Created by Richard on 2017/9/8.
 */
interface AioPresenter {

    //bind this
    fun attachView(host: Any)

    //unbind this
    fun detachView()
}