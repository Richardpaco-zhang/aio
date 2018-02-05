package rd.zhang.aio.kotlin.compiler.handle;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import rd.zhang.aio.kotlin.annotation.Module;
import rd.zhang.aio.kotlin.compiler.base.BaseElement;
import rd.zhang.aio.kotlin.compiler.base.Tools;

/**
 * Created by Richard on 2017/8/23.
 */
public class ModuleElement extends BaseElement {

    private TypeElement typeElement;
    private List<NormalElement> normalElements;
    private List<InterceptorElement> interceptorElements;
    private String moduleName;

    public ModuleElement(Element element, Tools tools) {
        super(element, tools);
        this.typeElement = (TypeElement) element;
        Module module = this.typeElement.getAnnotation(Module.class);
        if (module != null) {
            this.moduleName = module.value();
        }
    }

    public List<NormalElement> getNormalElements() {
        return normalElements;
    }

    public List<InterceptorElement> getInterceptorElements() {
        return interceptorElements;
    }

    public String getModuleName() {
        return moduleName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void putNormalElements(List<NormalElement> normalElements) {
        this.normalElements = normalElements;
    }

    public void putInterceptorElement(List<InterceptorElement> interceptorElements) {
        this.interceptorElements = interceptorElements;
    }
}
