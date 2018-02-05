package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Activity功能
 */
@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class Function(

        /**
         * router 地址
         *
         * @return
         */
        val router: String,
        /**
         * 滑动退出当前activity
         *
         * @return
         */
        val swipeBack: Boolean = false,
        /**
         * 返回键直接退回到手机主页
         *
         * @return
         */
        val backKeyToPhone: Boolean = false,
        /**
         * if(!isTaskRoot) finish();
         *
         * @return
         */
        val taskRootFinish: Boolean = false
        )