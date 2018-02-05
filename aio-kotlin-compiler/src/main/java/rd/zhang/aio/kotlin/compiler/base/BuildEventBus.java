package rd.zhang.aio.kotlin.compiler.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

import rd.zhang.aio.kotlin.annotation.Appoint;

/**
 * Created by Richard on 2017/8/24.
 */
public class BuildEventBus {

    public static void build(boolean sticky, boolean main, ExecutableElement executableElement, MethodSpec.Builder method, TypeElement typeElement) {
        List<? extends VariableElement> elementList = executableElement.getParameters();
        if (elementList.size() > 1 || elementList.size() == 0) {
            throw new IllegalArgumentException("event param max size must = 1 !");
        }

        VariableElement parameter = elementList.get(0);

        Appoint appoint = parameter.getAnnotation(Appoint.class);

        TypeMirror mirror = parameter.asType();

        mirror.accept(new SimpleTypeVisitor6<Void, Void>() {
            @Override
            public Void visitDeclared(DeclaredType declaredType, Void aVoid) {
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                if (!typeArguments.isEmpty()) {
                    throw new IllegalArgumentException("event not support gens!");
                }
                return null;
            }
        }, null);

        method.addStatement("addEventMethods(new $T().setMethod($T.class.getMethod(\"$L\",$T.class))\n" +
                        "   .setMainThread($L).setSticky($L).setParamType($T.class).setAppoint($L))",
                ClassName.get("rd.zhang.aio.kotlin.core.event", "EventModel"),
                typeElement.asType(),
                executableElement.getSimpleName(),
                parameter.asType(),
                main,
                sticky,
                parameter.asType(),
                appoint == null ? "null" : "\"" + appoint.value() + "\"");
    }

}
