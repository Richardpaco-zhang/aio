package rd.zhang.android.aio.presenter

import rd.zhang.aio.kotlin.annotation.Analysis
import rd.zhang.aio.kotlin.annotation.Autowired
import rd.zhang.aio.kotlin.core.event.EmptyEvent
import rd.zhang.aio.kotlin.core.function.post
import rd.zhang.aio.kotlin.core.support.AioBusiness
import rd.zhang.android.aio.model.UserLoginModel
import rd.zhang.android.aio.view.MainContract

/**
 * Created by Richard on 2017/9/8.
 */
@Analysis
class MainBusiness : AioBusiness<MainContract>(), MainPresenter {

    @Autowired
    lateinit var loginModel: UserLoginModel

    override fun login() {
        post("F", EmptyEvent())
    }


}