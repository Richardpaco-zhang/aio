package rd.zhang.aio.kotlin.compiler.handle

import rd.zhang.aio.kotlin.annotation.*
import rd.zhang.aio.kotlin.annotation.Function
import rd.zhang.aio.kotlin.compiler.base.BaseElement
import rd.zhang.aio.kotlin.compiler.base.Tools
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

/**
 * Created by Richard on 2017/9/8.
 */
class NormalElement(e: Element, t: Tools) : BaseElement(e, t) {

    var typeElement: TypeElement? = null
    var layout: Int = 0
    var fullscreen: Boolean = false
    var router: String = ""
    var swipeBack: Boolean = false
    var backKeyToPhone: Boolean = false
    var taskRootFinish: Boolean = false
    var initMethods: MutableList<Element> = arrayListOf()
    var permissions: Array<out Danger> = arrayOf()
    var findViewMap: MutableMap<Element, Int> = hashMapOf()
    var appointMap: MutableMap<Element, String> = hashMapOf()
    var autoWiredArray: MutableList<VariableElement> = arrayListOf()
    var eventMap: MutableMap<Element, Boolean> = hashMapOf()
    var stickyMap: MutableMap<Element, Boolean> = hashMapOf()
    var clickMap: MutableMap<Element, OnClick> = hashMapOf()
    var textChangeMap: MutableMap<Element, TextChange> = hashMapOf()
    var methodArray: MutableList<ExecutableElement> = arrayListOf()
    var onFinishArray: MutableList<ExecutableElement> = arrayListOf()

    init {
        this.typeElement = e as TypeElement

        var layout = this.typeElement!!.getAnnotation(Layout::class.java)
        if (layout != null) {
            this.layout = layout.value
        }

        var fullscreen = this.typeElement!!.getAnnotation(Fullscreen::class.java)
        if (fullscreen != null) {
            this.fullscreen = true
        }

        var function = this.typeElement!!.getAnnotation(Function::class.java)
        if (function != null) {
            this.router = function.router
            this.swipeBack = function.swipeBack
            this.backKeyToPhone = function.backKeyToPhone
            this.taskRootFinish = function.taskRootFinish
        }

        var permission = this.typeElement!!.getAnnotation(NeedPermission::class.java)
        if (permission != null) {
            this.permissions = permission.value
        }

        for (element in this.typeElement!!.enclosedElements) {
            if (element is VariableElement) {
                var appoint = element.getAnnotation(TypeExtras::class.java)
                if (appoint != null) {
                    appointMap.put(element, appoint.key)
                }
                var findView = element.getAnnotation(FindView::class.java)
                if (findView != null) {
                    findViewMap.put(element, findView.value)
                }
                var autowired = element.getAnnotation(Autowired::class.java)
                if (autowired != null) {
                    autoWiredArray.add(element)
                }
            } else if (element is ExecutableElement) {
                var initMethod = element.getAnnotation(OnInit::class.java)
                if (initMethod != null) {
                    initMethods.add(element)
                }
                var onEvent = element.getAnnotation(OnEvent::class.java)
                if (onEvent != null) {
                    eventMap.put(element, onEvent.mainThread)
                }
                var onSticky = element.getAnnotation(OnSticky::class.java)
                if (onSticky != null) {
                    stickyMap.put(element, onSticky.mainThread)
                }
                var onClick = element.getAnnotation(OnClick::class.java)
                if (onClick != null) {
                    clickMap.put(element, onClick)
                }
                var textChange = element.getAnnotation(TextChange::class.java)
                if (textChange != null) {
                    textChangeMap.put(element, textChange)
                }
                var launcher = element.getAnnotation(OnBegin::class.java)
                if (launcher != null) {
                    methodArray.add(element)
                }
                var onFinish = element.getAnnotation(OnFinish::class.java)
                if (onFinish != null) {
                    onFinishArray.add(element)
                }
            }
        }
    }


}
