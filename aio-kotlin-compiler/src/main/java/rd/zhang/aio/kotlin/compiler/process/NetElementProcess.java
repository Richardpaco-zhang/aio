package rd.zhang.aio.kotlin.compiler.process;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

import rd.zhang.aio.kotlin.annotation.Appoint;
import rd.zhang.aio.kotlin.annotation.DELETE;
import rd.zhang.aio.kotlin.annotation.GET;
import rd.zhang.aio.kotlin.annotation.Multipart;
import rd.zhang.aio.kotlin.annotation.POST;
import rd.zhang.aio.kotlin.annotation.UpFile;
import rd.zhang.aio.kotlin.annotation.UpJson;
import rd.zhang.aio.kotlin.compiler.base.ElementProcess;
import rd.zhang.aio.kotlin.compiler.base.Tools;
import rd.zhang.aio.kotlin.compiler.handle.NetworkingElement;

/**
 * Created by Richard on 2017/8/23.
 */

public class NetElementProcess extends ElementProcess {

    private NetworkingElement element;

    public NetElementProcess(NetworkingElement element, List<MethodSpec> methodSpecs, List<FieldSpec> fieldSpecs, Tools tools) {
        super(methodSpecs, fieldSpecs, tools);
        this.element = element;
    }

    @Override
    public void method() {

    }

    @Override
    public void process() {
        for (Element element : this.element.getTypeElement().getEnclosedElements()) {
            if (element instanceof ExecutableElement) {
                GET get = element.getAnnotation(GET.class);
                if (get != null) {
                    buildMethod(1, (ExecutableElement) element, get.url(), get.usingPi(), get.usingQi());
                    continue;
                }

                POST post = element.getAnnotation(POST.class);
                if (post != null) {
                    buildMethod(2, (ExecutableElement) element, post.url(), post.usingPi(), post.usingQi());
                    continue;
                }

                DELETE delete = element.getAnnotation(DELETE.class);
                if (delete != null) {
                    buildMethod(3, (ExecutableElement) element, delete.url(), delete.usingPi(), delete.usingQi());
                    continue;
                }
            }
        }
    }

    private void buildMethod(int type, ExecutableElement element, String value, boolean response, boolean request) {
        checkReturnType(element.getReturnType());

        final TypeMirror[] tType = new TypeMirror[1];
        final TypeMirror[] eType = new TypeMirror[1];
        final boolean[] isList = {false};

        element.getReturnType().accept(new SimpleTypeVisitor6<Void, Void>() {
            @Override
            public Void visitDeclared(DeclaredType declaredType, Void aVoid) {
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                if (!typeArguments.isEmpty()) {
                    tType[0] = typeArguments.get(0);
                    eType[0] = typeArguments.get(1);
                }
                return null;
            }
        }, null);

        if (tType[0].toString().startsWith("java.util.List<")) {
            tType[0].accept(new SimpleTypeVisitor6<Void, Void>() {
                @Override
                public Void visitDeclared(DeclaredType declaredType, Void aVoid) {
                    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                    if (!typeArguments.isEmpty()) {
                        tType[0] = typeArguments.get(0);
                    }
                    return null;
                }
            }, null);
            isList[0] = true;
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder(element.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(element.getReturnType()));

        builder.addCode("return new $T.Build()", ClassName.get("rd.zhang.aio.kotlin.core.http.build", "RequestImpl"));

        builder.addCode(".success($T.class)\n   .failed($T.class)\n   .url(\"$L\")\n   .method($L)",
                tType[0],
                eType[0],
                value,
                type);

        if (isList[0]) {
            builder.addCode(".isList()");
        }

        if (type == 2) {
            Multipart multipart = element.getAnnotation(Multipart.class);
            if (multipart != null) {
                builder.addCode("\n   .multipart(true)");
            }
        }
        builder.addCode("\n   .requestInterceptor($L)\n   .responseInterceptor($L)",
                request, response);


        for (VariableElement parameter : element.getParameters()) {
            builder.addParameter(TypeName.get(parameter.asType()), parameter.getSimpleName().toString());
            Appoint path = parameter.getAnnotation(Appoint.class);
            if (path != null) {
                builder.addCode("\n   .replace(\"$L\",$L)", path.value(), parameter.getSimpleName().toString());
            }
            if (type == 2) {
                UpJson upJson = parameter.getAnnotation(UpJson.class);
                UpFile upFile = parameter.getAnnotation(UpFile.class);
                if (upJson != null && upFile == null) {
                    builder.addCode("\n   .upJson($L)", parameter.getSimpleName());
                }
                if (upJson == null && upFile != null) {

                    if (getTools().getTypeUtils().isSameType(parameter.asType(),
                            getTools().getElementUtils().getTypeElement("rd.zhang.aio.kotlin.core.http.FilePorts").asType())) {
                        builder.addCode("\n   .upFile(\"$L\",$L)", upFile.value(), parameter.getSimpleName());
                    }

                }
            }
        }

        builder.addCode(".build();\n");

        getMethodSpecs().add(builder.build());
    }

    /**
     * 检查返回类型
     *
     * @param mirror
     */
    private void checkReturnType(TypeMirror mirror) {
        if (!mirror.toString().startsWith("rd.zhang.aio.kotlin.core.support.AioRequest<")) {
            throw new IllegalArgumentException("请以rd.zhang.aio.kotlin.core.support.AioRequest<SuccessType,FailedBean>规定您的请求方法!");
        }
    }

    @Override
    public void builder() {

    }
}
