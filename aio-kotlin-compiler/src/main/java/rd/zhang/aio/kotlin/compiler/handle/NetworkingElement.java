package rd.zhang.aio.kotlin.compiler.handle;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import rd.zhang.aio.kotlin.compiler.base.BaseElement;
import rd.zhang.aio.kotlin.compiler.base.Tools;

/**
 * Created by Richard on 2017/8/23.
 */
public class NetworkingElement extends BaseElement {

    private TypeElement typeElement;


    public NetworkingElement(Element element, Tools tools) {
        super(element, tools);
        this.typeElement = (TypeElement) element;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }
}
