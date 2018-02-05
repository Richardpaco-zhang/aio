package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.FIELD)
annotation class FindView(val value: Int)
