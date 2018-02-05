package rd.zhang.aio.kotlin.compiler.handle

import rd.zhang.aio.kotlin.annotation.Interceptor
import rd.zhang.aio.kotlin.compiler.base.BaseElement
import rd.zhang.aio.kotlin.compiler.base.Tools
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement


/**
 * Created by Richard on 2017/8/18.
 */
class InterceptorElement(element: Element, tools: Tools) : BaseElement(element, tools) {

    val typeElement: TypeElement
    var priority: Int = 0
    var type: Int = 0

    init {
        val interceptor = element.getAnnotation(Interceptor::class.java)
        if (interceptor != null) {
            priority = interceptor.priority
        }

        this.typeElement = element as TypeElement
        val superless = tools.getTypeUtils()!!.directSupertypes(typeElement.asType())[1].toString()

        when {
            superless.startsWith("rd.zhang.aio.kotlin.core.aop.RouterInterceptor") -> this.type = 1
            superless.startsWith("rd.zhang.aio.kotlin.core.aop.RequestInterceptor") -> this.type = 2
            superless.startsWith("rd.zhang.aio.kotlin.core.aop.ResponseInterceptor") -> this.type = 3
        }
    }
}
