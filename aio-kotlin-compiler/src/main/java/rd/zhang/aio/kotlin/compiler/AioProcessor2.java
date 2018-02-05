package rd.zhang.aio.kotlin.compiler;

import com.google.auto.service.AutoService;

import org.omg.PortableInterceptor.Interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import rd.zhang.aio.kotlin.annotation.Analysis;
import rd.zhang.aio.kotlin.annotation.GenerateHttp;
import rd.zhang.aio.kotlin.annotation.Module;
import rd.zhang.aio.kotlin.compiler.base.AutowiredHelp;
import rd.zhang.aio.kotlin.compiler.base.ElementBuilder;
import rd.zhang.aio.kotlin.compiler.base.Tools;
import rd.zhang.aio.kotlin.compiler.base.TypeKt;
import rd.zhang.aio.kotlin.compiler.handle.ApplicationElement;
import rd.zhang.aio.kotlin.compiler.handle.InterceptorElement;
import rd.zhang.aio.kotlin.compiler.handle.ModuleElement;
import rd.zhang.aio.kotlin.compiler.handle.NetworkingElement;
import rd.zhang.aio.kotlin.compiler.handle.NormalElement;

@AutoService(Processor.class)
public class AioProcessor2 extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;
    private Filer filer;
    private Tools tools;

    /**
     * 存放支持的注解
     */
    private Map<String, ElementBuilder> builderMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        builderMap = new TreeMap<>();
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();
        tools = new Tools().setElementUtils(elementUtils).setMessager(messager).setTypeUtils(typeUtils);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Interceptor.class.getCanonicalName());
        annotations.add(Analysis.class.getCanonicalName());
        annotations.add(GenerateHttp.class.getCanonicalName());
        annotations.add(Module.class.getCanonicalName());
        annotations.add(rd.zhang.aio.kotlin.annotation.Interceptor.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        ApplicationElement applicationElement = null;
        ModuleElement moduleElement = null;
        List<NormalElement> normalElements = new ArrayList<>();
        List<NetworkingElement> httpElements = new ArrayList<>();
        List<InterceptorElement> interceptorElements = new ArrayList<>();

        for (Element element : roundEnvironment.getElementsAnnotatedWith(Module.class)) {
            moduleElement = new ModuleElement(element, tools);
            getElementBuilder(element).putHolder(moduleElement);
            break;
        }

        for (Element element1 : roundEnvironment.getElementsAnnotatedWith(Analysis.class)) {

            switch (verificationType(element1)) {
                case 1:
                    NormalElement normalElement = new NormalElement(element1, tools);
                    normalElements.add(normalElement);
                    getElementBuilder(element1).putHolder(normalElement);
                    break;

                case 3:
                    applicationElement = new ApplicationElement(element1, tools);
                    getElementBuilder(element1).putHolder(applicationElement);
                    break;
            }
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(GenerateHttp.class)) {
            NetworkingElement element1 = new NetworkingElement(element, tools);
            getElementBuilder(element).putHolder(element1);
            httpElements.add(element1);
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(rd.zhang.aio.kotlin.annotation.Interceptor.class)) {
            InterceptorElement element1 = new InterceptorElement(element, tools);
            interceptorElements.add(element1);
        }

        if (applicationElement != null) {
            applicationElement.putNormalElements(normalElements);
            applicationElement.putInterceptorElement(interceptorElements);
        }

        if (moduleElement != null){
            moduleElement.putNormalElements(normalElements);
            moduleElement.putInterceptorElement(interceptorElements);
        }


        AutowiredHelp.setHttpTypeElement(httpElements);

        /**
         * 生成文件
         */
        for (ElementBuilder builder : builderMap.values()) {
            try {
                builder.builderFile().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private int verificationType(Element element) {
        if (element.getKind() == ElementKind.CLASS) {
            TypeMirror mirror = tools.getTypeUtils().directSupertypes(element.asType()).get(0);
            if (mirror.toString().equals("rd.zhang.aio.kotlin.core.support.AioApplication")) {
                return TypeKt.getApp();
            }
            return TypeKt.getNormal();
        }
        return TypeKt.getNONE();
    }

    /**
     * 添加到BuilderMap里
     *
     * @param element
     * @return
     */
    private ElementBuilder getElementBuilder(Element element) {
        TypeElement typeElement = (TypeElement) element;
        String fullName = typeElement.getQualifiedName().toString();
        ElementBuilder support = builderMap.get(fullName);
        if (support == null) {
            support = new ElementBuilder(tools, typeElement);
            builderMap.put(fullName, support);
        }
        return support;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
