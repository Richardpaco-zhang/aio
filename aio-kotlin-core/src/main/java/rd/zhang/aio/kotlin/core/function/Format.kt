package rd.zhang.aio.kotlin.core.function

import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import rd.zhang.aio.kotlin.core.base.DataProvider
import java.io.Reader
import java.lang.reflect.Type

/**
 * Created by Richard on 2017/8/25.
 */
//gson
fun toJson(json: Any): String? {
    return DataProvider.getGsonClient().toJson(json)
}

//gson
@Throws(JsonSyntaxException::class)
fun <T> fromJson(json: String, classOfT: Class<T>): T {
    return DataProvider.getGsonClient().fromJson(json, classOfT)
}

//gson
@Throws(JsonSyntaxException::class)
fun <T> fromJson(json: JsonElement, classOfT: Class<T>): T {
    return DataProvider.getGsonClient().fromJson(json, classOfT)
}

//gson
@Throws(JsonSyntaxException::class)
fun <T> fromJson(json: String, typeOfT: Type): T {
    return DataProvider.getGsonClient().fromJson(json, typeOfT)
}

//gson
@Throws(JsonIOException::class, JsonSyntaxException::class)
fun <T> fromJson(json: Reader, typeOfT: Type): T {
    return DataProvider.getGsonClient().fromJson(json, typeOfT)
}

//gson
@Throws(JsonSyntaxException::class, JsonIOException::class)
fun <T> fromJson(json: Reader, classOfT: Class<T>): T {
    return DataProvider.getGsonClient().fromJson(json, classOfT)
}

fun dip2px(dpValue: Float): Int {
    val scale = getBaseContext().resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun px2dip(pxValue: Float): Int {
    val scale = getBaseContext().resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}