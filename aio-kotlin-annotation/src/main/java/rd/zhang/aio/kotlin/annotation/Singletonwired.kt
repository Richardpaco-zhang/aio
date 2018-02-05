package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 单例写入
 */
@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.FIELD)
annotation class Singletonwired
