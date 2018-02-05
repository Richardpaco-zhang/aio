package rd.zhang.aio.kotlin.compiler.base;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Richard on 2017/8/17.
 */
public class CompileUtils {

    public static String getInjectorPath(Tools tools, TypeElement typeElement) {
        String packageName = tools.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        String typeName = typeElement.getQualifiedName().toString();

        String name = typeName.replace(packageName + ".", "");

        if (name.contains(".")) {
            name = name.replace(".", "$");
        }
        return packageName + ".Aio$$" + name + "$$Injector";
    }

    public static String getInjectorPath(Tools tools, TypeMirror typeMirror) {
        return getInjectorPath(tools, (TypeElement) tools.getTypeUtils().asElement(typeMirror));
    }

    public static TypeElement getAnnotationValueAsType(Tools tools, AnnotationMirror annotationMirror, String key) {
        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, key);
        if (annotationValue == null) {
            return null;
        }
        TypeMirror typeMirror = (TypeMirror) annotationValue.getValue();
        if (typeMirror == null) {
            return null;
        }
        return (TypeElement) tools.getTypeUtils().asElement(typeMirror);
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static AnnotationMirror getAnnotationMirror(TypeElement typeElement, String className) {
        for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(className)) {
                return m;
            }
        }
        return null;
    }


}
