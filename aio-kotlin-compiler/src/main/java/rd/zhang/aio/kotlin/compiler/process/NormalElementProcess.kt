package rd.zhang.aio.kotlin.compiler.process

import com.squareup.javapoet.*
import rd.zhang.aio.kotlin.annotation.Danger
import rd.zhang.aio.kotlin.compiler.base.AutowiredHelp
import rd.zhang.aio.kotlin.compiler.base.BuildEventBus

import rd.zhang.aio.kotlin.compiler.base.ElementProcess
import rd.zhang.aio.kotlin.compiler.base.Tools
import rd.zhang.aio.kotlin.compiler.handle.NormalElement
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

/**
 * Created by Richard on 2017/9/8.
 */
class NormalElementProcess(var element: NormalElement, methodSpecs: MutableList<MethodSpec>,
                           fieldSpecs: MutableList<FieldSpec>, tools: Tools) : ElementProcess(methodSpecs, fieldSpecs, tools) {

    private var onInit: MethodSpec.Builder? = null
    private var onCreate: MethodSpec.Builder? = null
    private var onWired: MethodSpec.Builder? = null
    private var onFinish: MethodSpec.Builder? = null
    private var onExecute: MethodSpec.Builder? = null
    private var onUnbind: MethodSpec.Builder? = null

    override fun method() {
        onInit = MethodSpec.methodBuilder("onInit")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        onCreate = MethodSpec.methodBuilder("onCreate")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        onWired = MethodSpec.methodBuilder("onWired")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        onFinish = MethodSpec.methodBuilder("onFinish")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        onExecute = MethodSpec.methodBuilder("onExecute")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)

        onUnbind = MethodSpec.methodBuilder("onUnbind")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PUBLIC)
    }

    override fun process() {
        if (element.taskRootFinish) {
            onInit!!.addStatement("taskRootFinish()")
        }

        if (element.fullscreen) {
            onInit!!.addStatement("windowFeatureNoTitle()")
        }

        if (element.layout != 0) {
            onInit!!.addStatement("layout(\$L)", element.layout)
        }

        if (element.fullscreen) {
            onInit!!.addStatement("fullscreen()")
        }

        if (element.swipeBack) {
            onInit!!.addStatement("openSwipeBack()")
        }

        if (element.backKeyToPhone) {
            onInit!!.addStatement("backKeyToPhone()")
        }

        if (element.appointMap.isNotEmpty()) {
            onInit!!.addStatement("\$T bundle = host().getIntent().getExtras()", ClassName.get("android.os", "Bundle"))
            onInit!!.beginControlFlow("if(bundle != null)")
            for ((key, value) in element.appointMap) {
                val value1 = key.simpleName.toString() + "_" + value
                onInit!!.addStatement("Object \$L = bundle.get(\"\$L\");", value1, value)
                onInit!!.beginControlFlow("if(\$L != null)", value1)
                onInit!!.addStatement("host().set\$L((\$T)\$L)", key.simpleName.toString().substring(0, 1).toUpperCase() + key.simpleName.toString().substring(1), key.asType(), value1)
                onInit!!.endControlFlow()
            }
            onInit!!.endControlFlow()
        }

        if (element.eventMap.isNotEmpty() || element.stickyMap.isNotEmpty()) {
            onFinish!!.beginControlFlow("if(isEventMethodNull(\$L.class))", element.typeElement!!.asType())
            onFinish!!.addCode("try {\n")
            for ((key, value) in element.eventMap) {
                BuildEventBus.build(false, value, key as ExecutableElement, onFinish, element.typeElement)
            }
            for ((key, value) in element.stickyMap) {
                BuildEventBus.build(true, value, key as ExecutableElement, onFinish, element.typeElement)
            }
            onFinish!!.addCode("\n}catch(NoSuchMethodException e){e.printStackTrace();}\n")
            onFinish!!.endControlFlow()
            onFinish!!.addStatement("enabledEventBus(host())")
            onUnbind!!.addStatement("disableEventBus(host())")
        }

        if (element.findViewMap.isNotEmpty()) {
            for ((key, value) in element.findViewMap) {
                onCreate!!.addStatement("host().\$L = (\$T)findView(\$L)", key.simpleName, key.asType(), value)
            }
        }

        if (element.clickMap.isNotEmpty() || element.textChangeMap.isNotEmpty()) {
            for ((key, value) in element.clickMap) {
                var array = StringBuilder()
                value.value.forEach { array.append(it.toString() + ",") }
                var values = array.toString()
                values = values.removeRange(values.length - 1, values.length)

                val method = MethodSpec.methodBuilder("onClick")
                        .addAnnotation(Override::class.java)
                        .addModifiers(Modifier.PUBLIC)

                method.addStatement("host().\$L()", key.simpleName)

                val typeSpec = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ClassName.get("rd.zhang.aio.kotlin.core.listener", "ViewClickListener"))
                        .addMethod(method.build()).build()

                onCreate!!.addStatement("clicks(\$L,\$L)", typeSpec, values)
            }
            for ((key, value) in element.textChangeMap) {
                var array = StringBuilder()
                value.value.forEach { array.append(it.toString() + ",") }
                var values = array.toString()
                values = values.removeRange(values.length - 1, values.length)

                val method = MethodSpec.methodBuilder("onTextChange")
                        .addAnnotation(Override::class.java)
                        .addModifiers(Modifier.PUBLIC)

                method.addStatement("host().\$L()", key.simpleName)

                val typeSpec = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ClassName.get("rd.zhang.aio.kotlin.core.listener", "TextChangeListener"))
                        .addMethod(method.build()).build()

                onCreate!!.addStatement("changes(\$L,\$L)", typeSpec, values)
            }
        }

        if (element.permissions.isNotEmpty()) {
            permission(element.permissions)
        } else {
            onCreate!!.addStatement("asWired()")
        }

        if (element.autoWiredArray.size > 0) {
            for (wired in element.autoWiredArray) {
                var info = AutowiredHelp.build(tools, fieldSpecs, methodSpecs, wired)
                onWired!!.addStatement("host().\$L = \$L().get()", wired.simpleName, info.methodName)
                if (info.isPresenter) {
                    if (info.isLazyPresenter) {
                        onUnbind!!.beginControlFlow("if(host().\$L.init())", wired.simpleName)
                        onUnbind!!.addStatement("host().\$L.get().detachView()", wired.simpleName)
                        onUnbind!!.endControlFlow()
                    } else {
                        onUnbind!!.addStatement("host().\$L.detachView()", wired.simpleName)
                    }
                }
                onUnbind!!.addStatement("host().\$L = null", wired.simpleName)
                onUnbind!!.addStatement("\$L = null", info.newValueFieldName)
                onUnbind!!.addStatement("\$L = null", info.providerFieldName)
            }
        }

        if (element.methodArray.size > 0) {
            for (method in element.methodArray) {
                onExecute!!.addStatement("host().\$L()", method.simpleName)
            }
        }

        if (element.initMethods.size > 0) {
            for (method in element.initMethods) {
                onFinish!!.addStatement("host().\$L()", method.simpleName)
            }
        }

        if (element.onFinishArray.size > 0){
            for (method in element.onFinishArray){
                onUnbind!!.addStatement("host().\$L()", method.simpleName)
            }
        }

        onWired!!.addStatement("asContinue()")

        onFinish!!.addStatement("asLauncher()")

    }

    private fun permission(dangers: Array<out Danger>) {
        val builder = StringBuilder()
        for (d in dangers) {
            builder.append("Danger.$d,")
        }
        var values = builder.deleteCharAt(builder.length - 1).toString()

        val method = MethodSpec.methodBuilder("onPermissionGranted")
                .addAnnotation(Override::class.java)
                .addStatement("asWired()")
                .addModifiers(Modifier.PUBLIC)

        val method1 = MethodSpec.methodBuilder("onPermissionDenial")
                .addAnnotation(Override::class.java)
                .addParameter(ArrayTypeName.of(ClassName.get("rd.zhang.aio.kotlin.annotation", "Danger")), "permissions")
                .addModifiers(Modifier.PUBLIC)
                .beginControlFlow("if(host() instanceof android.app.Activity)")
                .addStatement("host().finish()")
                .endControlFlow()

        val typeSpec = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ClassName.get("rd.zhang.aio.kotlin.core.permission.PermissionUtils", "OnPermissionRequestResponseListener"))
                .addMethod(method.build())
                .addMethod(method1.build())
                .build()

        onCreate!!.addStatement("permissions(\$L,\$L)",
                typeSpec,
                values)
    }

    override fun builder() {
        methodSpecs.add(onInit!!.build())
        methodSpecs.add(onCreate!!.build())
        methodSpecs.add(onWired!!.build())
        methodSpecs.add(onFinish!!.build())
        methodSpecs.add(onExecute!!.build())
        methodSpecs.add(onUnbind!!.build())
    }
}
