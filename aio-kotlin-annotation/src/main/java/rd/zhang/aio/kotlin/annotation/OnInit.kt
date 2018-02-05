package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class OnInit
