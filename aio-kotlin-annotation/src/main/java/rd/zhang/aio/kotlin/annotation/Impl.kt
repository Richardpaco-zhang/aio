package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

/**
 * Created by Richard on 2017/9/8.
 *
 * 从那个类初始化(必须要实现当前接口)
 *
 * Performs initialization operations from classes that are executed
 */
/**
 * 如果使用单例，instance为单例的方法
 * if you use singleton, so 'instance' is singleton class instance method name
 */
@Retention(RetentionPolicy.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Impl(val value: KClass<*>, val instance: String = "")

