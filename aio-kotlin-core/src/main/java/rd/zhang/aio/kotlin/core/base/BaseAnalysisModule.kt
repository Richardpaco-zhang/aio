package rd.zhang.aio.kotlin.core.base

import rd.zhang.aio.kotlin.core.support.AioModuleLoader

/**
 * Created by Richard on 2017/9/11.
 */
abstract class BaseAnalysisModule {

    private var mapping: MutableMap<Class<*>, Class<*>> = hashMapOf()
    private var router: MutableMap<String, Class<*>> = hashMapOf()
    private var responseInter: MutableList<Class<*>> = arrayListOf()
    private var requestInter: MutableList<Class<*>> = arrayListOf()
    private var routerInter: MutableList<Class<*>> = arrayListOf()
    private var modules: MutableList<BaseAnalysisModule> = arrayListOf()

    fun loadModule(vararg module: AioModuleLoader): BaseAnalysisModule {
        module.forEach {
            modules.add(it.getModule())
        }
        return this
    }

    fun getRouterIt(): List<Class<*>> {
        if (routerInter.size == 0) {
            routerInter()
            modules.forEach {
                routerInter.addAll(it.getRouterIt())
            }
        }
        return routerInter
    }

    fun getResponseIt(): List<Class<*>> {
        if (responseInter.size == 0) {
            responseInter()
            modules.forEach {
                responseInter.addAll(it.getResponseIt())
            }
        }
        return responseInter
    }

    fun getRequestIt(): List<Class<*>> {
        if (requestInter.size == 0) {
            requestInter()
            modules.forEach {
                requestInter.addAll(it.getRequestIt())
            }
        }
        return requestInter
    }

    fun getRouterAddress(): MutableMap<String, Class<*>> {
        if (router.isEmpty()) {
            router()
            modules.forEach {
                router.putAll(it.getRouterAddress())
            }
        }
        return router
    }

    fun getAMapping(): MutableMap<Class<*>, Class<*>> {
        if (mapping.isEmpty()) {
            mapping()
            modules.forEach {
                mapping.putAll(it.getAMapping())
            }
        }
        return mapping
    }

    fun router(routerAddress: String, clazz: Class<*>) {
        router.put(routerAddress, clazz)
    }

    fun mapping(injector: Class<*>, clazz: Class<*>) {
        mapping.put(injector, clazz)
    }

    fun responseInterceptor(clazz: Class<*>) {
        responseInter.add(clazz)
    }

    fun requestInterceptor(clazz: Class<*>) {
        requestInter.add(clazz)
    }

    fun routerInterceptor(clazz: Class<*>) {
        routerInter.add(clazz)
    }

    abstract fun router()

    abstract fun mapping()

    abstract fun responseInter()

    abstract fun requestInter()

    abstract fun routerInter()

}