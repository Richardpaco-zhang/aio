package rd.zhang.aio.kotlin.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.CLASS)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class AppFunction(val dbName: String = "",
                             val dbVersion: Long = 0,
                             val dbPassphrase: String = "",
                             val readTimeOut: Long = 60L,
                             val writeTimeOut: Long = 60L,
                             val connectTimeout: Long = 60L,
                             val retryCount: Int = 5,
                             val debug: Boolean = false,
                             val maxPoolSize: Int = 3,
                             val openService: Boolean = false,
                             val notifyWaiting: Int = 3000,
                             val largeImageCachePath: String = "/files/large/",
                             val smallImageCachePath: String = "/files/small/",
                             val baseIp: String)