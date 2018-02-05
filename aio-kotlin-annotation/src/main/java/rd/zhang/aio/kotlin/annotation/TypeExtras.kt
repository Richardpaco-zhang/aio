package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 获取intent值
 */
@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class TypeExtras(val key: String)