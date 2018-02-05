package rd.zhang.aio.kotlin.compiler.base

import javax.annotation.processing.Messager
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Created by Richard on 2017/6/24.
 */

class Tools {

    private var elementUtils: Elements? = null
    private var typeUtils: Types? = null
    private var messager: Messager? = null

    fun getElementUtils(): Elements? {
        return elementUtils
    }

    fun setElementUtils(elementUtils: Elements): Tools {
        this.elementUtils = elementUtils
        return this
    }

    fun getTypeUtils(): Types? {
        return typeUtils
    }

    fun setTypeUtils(typeUtils: Types): Tools {
        this.typeUtils = typeUtils
        return this
    }

    fun getMessager(): Messager? {
        return messager
    }

    fun setMessager(messager: Messager): Tools {
        this.messager = messager
        return this
    }
}
