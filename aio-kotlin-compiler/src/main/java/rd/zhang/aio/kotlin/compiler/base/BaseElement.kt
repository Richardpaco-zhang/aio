package rd.zhang.aio.kotlin.compiler.base

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror


/**
 * Created by Richard on 2017/6/24.
 */

open class BaseElement(val element: Element, val tools: Tools) {

    fun getAnnotationValueAsType(annotationMirror: AnnotationMirror, key: String): TypeElement? {
        val annotationValue = getAnnotationValue(annotationMirror, key) ?: return null
        val typeMirror = annotationValue.value as TypeMirror ?: return null
        return tools.getTypeUtils()!!.asElement(typeMirror) as TypeElement
    }

    private fun getAnnotationValue(annotationMirror: AnnotationMirror, key: String): AnnotationValue? {
        for ((key1, value) in annotationMirror.elementValues) {
            if (key1.simpleName.toString() == key) {
                return value
            }
        }
        return null
    }

    fun getAnnotationMirror(typeElement: TypeElement, className: String): AnnotationMirror? {
        for (m in typeElement.annotationMirrors) {
            if (m.annotationType.toString() == className) {
                return m
            }
        }
        return null
    }

}
