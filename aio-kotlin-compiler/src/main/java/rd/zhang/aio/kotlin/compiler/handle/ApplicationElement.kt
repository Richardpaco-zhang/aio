package rd.zhang.aio.kotlin.compiler.handle

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import rd.zhang.aio.kotlin.annotation.AppFunction
import rd.zhang.aio.kotlin.compiler.base.BaseElement
import rd.zhang.aio.kotlin.compiler.base.Tools
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
 * Created by Richard on 2017/9/8.
 */

class ApplicationElement(element: Element, tools: Tools) : BaseElement(element, tools) {

    var normalElements: List<NormalElement>? = null
    var interceptorElement: List<InterceptorElement>? = null
    var typeElement: TypeElement = element as TypeElement
    var constructor: MethodSpec.Builder? = null

    init {
        var function = element.getAnnotation(AppFunction::class.java)
        if (function != null) {
            constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get("android.app", "Application"), "application")
                    .addStatement("super(application,\$L,\$L,\$L,\n\$L,\$L,\$L,\$L,\$L,\$L,\$L,\$L,\$L)",
                            getString(function.dbName),
                            function.dbVersion,
                            getString(function.dbPassphrase),
                            function.readTimeOut,
                            function.writeTimeOut,
                            function.connectTimeout,
                            function.retryCount,
                            function.debug,
                            function.maxPoolSize,
                            function.openService,
                            function.notifyWaiting,
                            getString(function.baseIp))
        } else {
            throw IllegalArgumentException("Your must set @AppFunction at your application")
        }
    }

    fun getString(name: String): String? {
        return if (name == "") {
            null
        } else {
           "\"" + name + "\""
        }
    }

    fun putNormalElements(array: List<NormalElement>) {
        this.normalElements = array
    }

    fun putInterceptorElement(array: List<InterceptorElement>) {
        this.interceptorElement = array
    }

}
