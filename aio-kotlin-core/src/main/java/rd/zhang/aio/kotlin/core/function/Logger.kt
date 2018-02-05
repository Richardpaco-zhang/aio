package rd.zhang.aio.kotlin.core.function

/**
 * Created by Richard on 2017/9/9.
 */
fun logi(message: String) {
    println("Debug Info -> " + message)
}

fun loge(message: String) {
    println("Debug Error -> " + message)
}

fun logv(message: String) {
    println("Debug Ver -> " + message)
}

fun logj(message: Any) {
    println("Debug Json -> " + toJson(message))
}

fun logw(message: String) {
    println("Debug Warn -> " + message)
}
