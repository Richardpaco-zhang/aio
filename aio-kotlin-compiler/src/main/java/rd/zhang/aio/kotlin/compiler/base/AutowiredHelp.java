package rd.zhang.aio.kotlin.compiler.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

import rd.zhang.aio.kotlin.annotation.EmployHost;
import rd.zhang.aio.kotlin.annotation.Impl;
import rd.zhang.aio.kotlin.compiler.bean.WiredInfo;
import rd.zhang.aio.kotlin.compiler.handle.NetworkingElement;

/**
 * Created by Richard on 2017/8/20.
 */
public class AutowiredHelp {

    private static Tools tools;
    private static List<FieldSpec> fieldSpecs;
    private static List<MethodSpec> methodSpecs;

    private static List<NetworkingElement> httpElements = new ArrayList<>();

    public static void setHttpTypeElement(List<NetworkingElement> httpElements1) {
        httpElements = httpElements1;
    }

    public static WiredInfo build(Tools tools, List<FieldSpec> fieldSpecs, List<MethodSpec> methodSpecs, VariableElement element) {
        AutowiredHelp.tools = tools;
        AutowiredHelp.fieldSpecs = fieldSpecs;
        AutowiredHelp.methodSpecs = methodSpecs;
        return new Generate().build(element);
    }

    public static class Generate {

        private TypeMirror injectType, fieldType;
        private TypeName providerValueType;

        private MethodSpec.Builder methodValue = MethodSpec.methodBuilder("onNull")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        private boolean lazy = false, normal = false;

        private boolean needHost = false, isHttp = false;

        private WiredInfo wiredInfo;

        private FieldSpec fieldSpecValue, fieldSpec;

        private String providerValueStr, fromInstanceMethodName;

        public WiredInfo build(VariableElement element) {
            injectType = fieldType = element.asType();
            String mirrorStr = fieldType.toString();
            //Lazy<UserModel>
            if (mirrorStr.contains("rd.zhang.aio.kotlin.core.wried.Lazy")) {
                lazy = true;
                injectType.accept(new SimpleTypeVisitor6<Void, Void>() {
                    @Override
                    public Void visitDeclared(DeclaredType declaredType, Void aVoid) {
                        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                        if (!typeArguments.isEmpty()) {
                            injectType = typeArguments.get(0);
                        }
                        return null;
                    }
                }, null);
            } else {
                normal = true;
            }

            methodValue.returns(TypeName.get(injectType));

            providerValueType = TypeName.get(injectType);
            Element anElement = tools.getTypeUtils().asElement(injectType);
            EmployHost needAnjectHost = anElement.getAnnotation(EmployHost.class);
            Impl appoint = anElement.getAnnotation(Impl.class);
            if (appoint != null) {
                TypeElement typeElement = (TypeElement) tools.getTypeUtils().asElement(injectType);
                AnnotationMirror mirror = CompileUtils.getAnnotationMirror(typeElement, Impl.class.getCanonicalName());
                TypeElement typeElement1 = CompileUtils.getAnnotationValueAsType(tools, mirror, "value");
                try {
                    fromInstanceMethodName = (String) CompileUtils.getAnnotationValue(mirror, "instance").getValue();
                } catch (Exception e) {

                }
                providerValueType = TypeName.get(typeElement1.asType());
                if (typeElement1 != null) {
                    needAnjectHost = typeElement1.getAnnotation(EmployHost.class);
                }
            } else {
                for (NetworkingElement httpElement : httpElements) {
                    if (TypeName.get(httpElement.getTypeElement().asType()).equals(TypeName.get(injectType))) {
                        providerValueStr = CompileUtils.getInjectorPath(tools, injectType);
                        isHttp = true;
                        break;
                    }
                }
            }

            if (needAnjectHost != null) {
                this.needHost = true;
            }

            if (providerValueStr == null) {
                providerValueStr = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE).
                        matcher(providerValueType.toString()).replaceAll("");
            }

            wiredInfo = new WiredInfo();
            if (isPresenter(injectType)) {
                wiredInfo.setPresenter(true);
                if (lazy) {
                    wiredInfo.setLazyPresenter(true);
                }
            }

            buildGenreInjectMethodByValue();

            buildInjectValueStam(element);

            return wiredInfo.setMethodName("inject" + fieldSpec.name)
                    .setNewValueFieldName(fieldSpecValue.name)
                    .setProviderFieldName(fieldSpec.name);
        }

        private boolean isPresenter(TypeMirror mirror) {
            List<? extends TypeMirror> mirrors = ((TypeElement) tools.getTypeUtils().asElement(mirror)).getInterfaces();
            for (TypeMirror mirror1 : mirrors) {
                if (mirror1.toString().equals("rd.zhang.aio.kotlin.core.support.AioPresenter")) {
                    return true;
                }
            }
            return false;
        }

        private void buildGenreInjectMethodByValue() {
            methodValue.addCode("$T _pValue = ", injectType);
            if (needHost) {
                methodValue.addCode("new $L(host())", providerValueStr);
            } else if (fromInstanceMethodName != null) {
                methodValue.addCode("$L.$L", providerValueStr, fromInstanceMethodName);
            } else {
                methodValue.addCode("new $L()", providerValueStr);
            }
            methodValue.addCode(";\n");
            if (wiredInfo.isPresenter()) {
                methodValue.addStatement("_pValue.attachView(host())");
            }
            methodValue.addStatement("return _pValue");
        }

        private void buildInjectValueStam(VariableElement element) {
            ParameterizedTypeName providerType = ParameterizedTypeName.get(ClassName.get("rd.zhang.aio.kotlin.core.wried", "Provider"), TypeName.get(element.asType()));
            ParameterizedTypeName providerValue = ParameterizedTypeName.get(ClassName.get("rd.zhang.aio.kotlin.core.wried", "NewValue"), TypeName.get(injectType));

            fieldSpec = FieldSpec.builder(providerType, getInjectName(element) + element.getSimpleName() + "Provider", Modifier.PRIVATE).build();
            fieldSpecValue = FieldSpec.builder(providerValue, getInjectName(element) + element.getSimpleName() + "Check", Modifier.PRIVATE).build();
            fieldSpecs.add(fieldSpecValue);
            fieldSpecs.add(fieldSpec);
            String name = "inject" + fieldSpec.name;

            MethodSpec.Builder method = MethodSpec.methodBuilder("get")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(element.asType()));

            if (lazy) {
                if (isHttp) {
                    method.addStatement("return $T.create($T.check($L.class,$L))", ClassName.get("rd.zhang.aio.kotlin.core.wried", "LazyProvider"),
                            ClassName.get("rd.zhang.aio.kotlin.core.wried", "HttpProvider"), injectType, fieldSpecValue.name);
                } else {
                    method.addStatement("return $T.create($L)", ClassName.get("rd.zhang.aio.kotlin.core.wried", "LazyProvider"), fieldSpecValue.name);
                }
            } else if (normal) {
                if (isHttp) {
                    method.addStatement("return $T.check($L.class,$L).get()", ClassName.get("rd.zhang.aio.kotlin.core.wried", "HttpProvider"), injectType, fieldSpecValue.name);
                } else {
                    method.addStatement("return $L.get()", fieldSpecValue.name);
                }
            } else {
                throw new IllegalArgumentException("Not Support!");
            }

            TypeSpec typeSpec = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(providerType)
                    .addMethod(method.build()).build();

            TypeSpec typeSpecValue = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(providerValue)
                    .addMethod(methodValue.build()).build();

            MethodSpec methodSpec = MethodSpec.methodBuilder(name)
                    .addModifiers(Modifier.PRIVATE)
                    .returns(providerType)
                    .beginControlFlow("if($L == null)", fieldSpecValue.name)
                    .addStatement("$L = $L", fieldSpecValue.name, typeSpecValue)
                    .endControlFlow()
                    .beginControlFlow("if($L == null)", fieldSpec.name)
                    .addStatement("$L = $L", fieldSpec.name, typeSpec)
                    .endControlFlow()
                    .addStatement("return $L", fieldSpec.name)
                    .build();

            methodSpecs.add(methodSpec);
        }

        public static String getInjectName(Element element) {
            return "_";
        }
    }


}
