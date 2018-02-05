package rd.zhang.aio.kotlin.compiler.base

import com.squareup.javapoet.*
import rd.zhang.aio.kotlin.compiler.handle.ApplicationElement
import rd.zhang.aio.kotlin.compiler.handle.ModuleElement
import rd.zhang.aio.kotlin.compiler.handle.NetworkingElement
import rd.zhang.aio.kotlin.compiler.handle.NormalElement
import rd.zhang.aio.kotlin.compiler.process.ApplicationElementProcess
import rd.zhang.aio.kotlin.compiler.process.ModuleElementProcess
import rd.zhang.aio.kotlin.compiler.process.NetElementProcess
import rd.zhang.aio.kotlin.compiler.process.NormalElementProcess
import java.util.*
import javax.annotation.processing.Messager
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


/**
 * Created by Richard on 2017/6/24.
 */

class ElementBuilder(private val tools: Tools, private val typeElement: TypeElement) {

    private var elementUtils: Elements? = tools.getElementUtils()
    private var typeUtils: Types? = tools.getTypeUtils()
    private var messager: Messager? = tools.getMessager()
    private var holders: MutableList<BaseElement> = ArrayList()
    private var injectBuilder: TypeSpec.Builder? = null
    private var injectClass: TypeSpec? = null
    private var methodSpecs: MutableList<MethodSpec> = ArrayList()
    private var fieldSpecs: MutableList<FieldSpec> = ArrayList()

    /**
     * 添加Holder
     *
     * @param holder
     */
    fun putHolder(holder: BaseElement) {
        this.holders.add(holder)
    }

    fun builderFile(): JavaFile {
        val packageName = elementUtils!!.getPackageOf(typeElement).qualifiedName.toString()
        return JavaFile.builder(packageName, generate()).build()
    }

    /**
     * 开始编译
     *
     * @return
     */
    private fun generate(): TypeSpec {

        for (holder in holders) {
            when (holder) {
                is NormalElement -> {
                    injectBuilder = TypeSpec.classBuilder(getClassName(holder.typeElement!!))
                            .superclass(ParameterizedTypeName.get(ClassName.get("rd.zhang.aio.kotlin.core.analysis", "AbstractAnalysisModel"),
                                    TypeName.get(typeElement.asType())))
                    NormalElementProcess(holder, methodSpecs, fieldSpecs, tools).run()
                }
                is ApplicationElement -> {
                    injectBuilder = TypeSpec.classBuilder("AppStartup")
                            .superclass(ClassName.get("rd.zhang.aio.kotlin.core.base", "BaseAppStartup"))
                    ApplicationElementProcess(holder, methodSpecs, fieldSpecs, tools).run()
                }
                is NetworkingElement -> {
                    injectBuilder = TypeSpec.classBuilder(getClassName(holder.typeElement))
                            .addSuperinterface(TypeName.get(holder.typeElement.asType()))
                    NetElementProcess(holder, methodSpecs, fieldSpecs, tools).run()
                }
                is ModuleElement -> {
                    injectBuilder = TypeSpec.classBuilder(holder.moduleName + "Module")
                            .superclass(ClassName.get("rd.zhang.aio.kotlin.core.base", "BaseAnalysisModule"))
                    ModuleElementProcess(holder, methodSpecs, fieldSpecs, tools).run()
                }
            }
        }

        injectClass = injectBuilder!!.addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpecs)
                .addJavadoc("This file generate by Aio \n do not edit!\n")
                .addMethods(methodSpecs).build()

        return injectClass!!
    }

    private fun getClassName(typeElement: TypeElement): String {
        val packageName = tools.getElementUtils()!!.getPackageOf(typeElement).qualifiedName.toString()
        val typeName = typeElement.qualifiedName.toString()

        var name = typeName.replace(packageName + ".", "")

        if (name.contains(".")) {
            name = name.replace(".", "$")
        }

        return "Aio$$$name$\$Injector"
    }


}
