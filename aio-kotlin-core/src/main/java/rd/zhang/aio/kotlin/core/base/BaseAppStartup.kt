package rd.zhang.aio.kotlin.core.base

import android.app.Application
import rd.zhang.aio.kotlin.core.support.AioModuleLoader

/**
 * Created by Richard on 2017/9/8.
 */

abstract class BaseAppStartup {

    var application: Application
    var dbName: String
    var dbVersion: Long
    var dbPassphrase: String
    var readTimeOut: Long
    var writeTimeOut: Long
    var connectTimeout: Long
    var retryCount: Int
    var debug: Boolean
    var threadPoolSize: Int
    var openService: Boolean
    var notifyWaiting: Int
    var baseIp: String
    var openCrash: Boolean = false
    var openUpgrade: Boolean = false

    var mapping: MutableMap<Class<*>, Class<*>> = hashMapOf()
    var router: MutableMap<String, Class<*>> = hashMapOf()
    var responseInter: MutableList<Class<*>> = arrayListOf()
    var requestInter: MutableList<Class<*>> = arrayListOf()
    var routerInter: MutableList<Class<*>> = arrayListOf()

    private var modules: BaseAnalysisModule? = null

    fun openCrashReport(): BaseAppStartup {
        openCrash = true
        return this
    }

    fun openAppUpgrade(): BaseAppStartup {
        openUpgrade = true
        return this
    }

    constructor(application: Application,
                dbName: String,
                dbVersion: Long,
                dbPassphrase: String,
                readTimeOut: Long,
                writeTimeOut: Long,
                connectTimeout: Long,
                retryCount: Int,
                debug: Boolean,
                threadPoolSize: Int,
                openService: Boolean,
                notifyWaiting: Int,
                baseIp: String) {
        this.dbName = dbName
        this.dbPassphrase = dbPassphrase
        this.dbVersion = dbVersion
        this.application = application
        this.readTimeOut = readTimeOut
        this.writeTimeOut = writeTimeOut
        this.connectTimeout = connectTimeout
        this.retryCount = retryCount
        this.debug = debug
        this.threadPoolSize = threadPoolSize
        this.openService = openService
        this.notifyWaiting = notifyWaiting
        this.baseIp = baseIp
    }

    fun installModule(module: AioModuleLoader): BaseAppStartup {
        this.modules = module.getModule()
        return this
    }

    fun getRouterIt(): List<Class<*>> {
        if (routerInter.size == 0) {
            routerInter()
            if (modules != null)
                routerInter.addAll(modules!!.getRouterIt())
        }
        return routerInter
    }

    fun getResponseIt(): List<Class<*>> {
        if (responseInter.size == 0) {
            responseInter()
            if (modules != null)
                responseInter.addAll(modules!!.getResponseIt())

        }
        return responseInter
    }

    fun getRequestIt(): List<Class<*>> {
        if (requestInter.size == 0) {
            requestInter()
            if (modules != null)
                requestInter.addAll(modules!!.getRequestIt())

        }
        return requestInter
    }

    fun getRouterAddress(): MutableMap<String, Class<*>> {
        if (router.isEmpty()) {
            router()
            if (modules != null)
                router.putAll(modules!!.getRouterAddress())

        }
        return router
    }

    fun getAMapping(): MutableMap<Class<*>, Class<*>> {
        if (mapping.isEmpty()) {
            mapping()
            if (modules != null)
                mapping.putAll(modules!!.getAMapping())

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
