package rd.zhang.aio.kotlin.compiler.process

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import rd.zhang.aio.kotlin.compiler.base.CompileUtils
import rd.zhang.aio.kotlin.compiler.base.ElementProcess
import rd.zhang.aio.kotlin.compiler.base.Tools
import rd.zhang.aio.kotlin.compiler.handle.ApplicationElement
import rd.zhang.aio.kotlin.compiler.handle.InterceptorElement
import java.util.*
import javax.lang.model.element.Modifier

/**
 * Created by Richard on 2017/9/9.
 */
class ApplicationElementProcess(private var element: ApplicationElement,
                                methodSpecs: MutableList<MethodSpec>,
                                fieldSpecs: MutableList<FieldSpec>, tools: Tools) : ElementProcess(methodSpecs, fieldSpecs, tools) {

    private var router: MethodSpec.Builder? = null
    private var mapping: MethodSpec.Builder? = null
    private var responseIt: MethodSpec.Builder? = null
    private var requestIt: MethodSpec.Builder? = null
    private var routerIt: MethodSpec.Builder? = null

    override fun method() {
        router = MethodSpec.methodBuilder("router")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        mapping = MethodSpec.methodBuilder("mapping")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        responseIt = MethodSpec.methodBuilder("responseInter")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        requestIt = MethodSpec.methodBuilder("requestInter")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        routerIt = MethodSpec.methodBuilder("routerInter")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)
    }

    override fun process() {
        if (element.normalElements != null) {
            for (normal in element.normalElements!!) {
                if (normal.router != "") {
                    router!!.addStatement("router(\"\$L\",\$L.class)", normal.router, normal.typeElement!!.asType().toString())
                }
                if (normal.typeElement != null) {
                    mapping!!.addStatement("mapping(\$L.class,\$L.class)", normal.typeElement!!.asType().toString(), CompileUtils.getInjectorPath(tools, normal.typeElement!!))
                }
            }
        }
        if (element.interceptorElement != null) {
            Collections.sort<InterceptorElement>(element.interceptorElement) { interceptorElement, t1 -> t1.priority!!.compareTo(interceptorElement.priority) }
            for (interceptor in element.interceptorElement!!) {
                when {
                //router
                    interceptor.type == 1 -> {
                        routerIt!!.addStatement("routerInterceptor(\$L.class)", interceptor.typeElement.asType())
                    }
                //request
                    interceptor.type == 2 -> {
                        requestIt!!.addStatement("requestInterceptor(\$L.class)", interceptor.typeElement.asType())
                    }
                //response
                    interceptor.type == 3 -> {
                        responseIt!!.addStatement("responseInterceptor(\$L.class)", interceptor.typeElement.asType())
                    }
                }
            }
        }
    }

    override fun builder() {
        methodSpecs.add(element.constructor!!.build())
        methodSpecs.add(router!!.build())
        methodSpecs.add(mapping!!.build())
        methodSpecs.add(routerIt!!.build())
        methodSpecs.add(responseIt!!.build())
        methodSpecs.add(requestIt!!.build())
    }
}