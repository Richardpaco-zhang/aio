package rd.zhang.android.aio.model

import rd.zhang.aio.kotlin.annotation.Impl

/**
 * Created by Richard on 2017/9/8.
 */
@Impl(UserLoginModelImpl::class)
interface UserLoginModel {

    fun startlogin()

}