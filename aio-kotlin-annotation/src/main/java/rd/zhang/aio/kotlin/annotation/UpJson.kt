package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class UpJson
