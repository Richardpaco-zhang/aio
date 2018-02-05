package rd.zhang.aio.kotlin.compiler.base

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec


/**
 * Created by Richard on 2017/6/24.
 */

abstract class ElementProcess(var methodSpecs: MutableList<MethodSpec>, var fieldSpecs: MutableList<FieldSpec>, var tools: Tools) {

    /**
     * 启动
     */
    fun run() {
        method()
        process()
        builder()
    }

    abstract fun method()

    abstract fun process()

    abstract fun builder()


}
