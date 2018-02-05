package rd.zhang.android.aio.presenter

import rd.zhang.aio.kotlin.annotation.Impl
import rd.zhang.aio.kotlin.core.support.AioPresenter

/**
 * Created by Richard on 2017/9/8.
 */
@Impl(MainBusiness::class)
interface MainPresenter : AioPresenter {

    fun login( )

}