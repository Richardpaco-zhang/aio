package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class DELETE(//request url
        val url: String,
        val usingQi: Boolean, //request Interceptor
        val usingPi: Boolean) //response Interceptor
