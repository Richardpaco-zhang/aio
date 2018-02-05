package rd.zhang.android.aio.view

import android.widget.Button
import rd.zhang.aio.kotlin.annotation.*
import rd.zhang.aio.kotlin.annotation.Function
import rd.zhang.aio.kotlin.core.event.EmptyEvent
import rd.zhang.aio.kotlin.core.function.logi
import rd.zhang.aio.kotlin.core.function.notifySuccess
import rd.zhang.aio.kotlin.core.function.show
import rd.zhang.aio.kotlin.core.support.AioAppActivity
import rd.zhang.aio.kotlin.core.wried.Lazy
import rd.zhang.android.aio.R
import rd.zhang.android.aio.presenter.MainPresenter

@Analysis
@Function(router = "aio://main/act")
@Layout(R.layout.activity_main)
@NeedPermission(Danger.CAMERA, Danger.READ_EXTERNAL_STORAGE, Danger.WRITE_EXTERNAL_STORAGE)
class MainActivity : AioAppActivity(), MainContract {

    @Autowired
    lateinit var presenter: Lazy<MainPresenter>

    @FindView(R.id.button)
    lateinit var button: Button

    @TypeExtras("")
    var data: Int = 0

    @OnBegin
    fun begin() {

    }

    @OnClick(R.id.button)
    fun onClick() {
        notifySuccess("测试错误信息").time(2000).show()
    }

    override fun loginFinish() {

    }

    @OnEvent
    fun onEmptyEvent(@Appoint("F") event: EmptyEvent) {
        logi("receiver")
    }

    @OnFinish
    fun onFinish() {

    }

}
