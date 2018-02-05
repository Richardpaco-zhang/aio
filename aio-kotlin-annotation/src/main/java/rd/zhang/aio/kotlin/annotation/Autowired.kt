package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 自动装配(自动初始化)
 * auto init
 */
@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.FIELD)
annotation class Autowired