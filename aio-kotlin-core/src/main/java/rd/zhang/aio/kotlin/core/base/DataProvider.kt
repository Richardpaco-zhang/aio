package rd.zhang.aio.kotlin.core.base

import android.content.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import rd.zhang.aio.kotlin.core.bean.PostingEvent
import rd.zhang.aio.kotlin.core.callback.SingleTransport
import rd.zhang.aio.kotlin.core.crash.AppCrashHandler
import rd.zhang.aio.kotlin.core.event.EventModel
import rd.zhang.aio.kotlin.core.event.StickyModel
import rd.zhang.aio.kotlin.core.service.AioCoreService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * Created by Richard on 2017/9/9.
 */
object DataProvider {

    lateinit var startup: BaseAppStartup
    private var okHttp: OkHttpClient? = null
    val delivery: Handler = Handler(Looper.getMainLooper())
    //保存的所有已注册事件
    var eventTypes: MutableMap<Class<*>, List<EventModel>> = HashMap()
    //粘性事件key:事件类，value:事件实例
    var stickyEvents: MutableMap<Class<*>, StickyModel>? = null
    //注册的对象
    var registerObjs: MutableList<Any> = arrayListOf()
    //线程池
    private var threadPool: ExecutorService? = null
    private var realmBuilder: RealmConfiguration.Builder? = null
    private var shared: SharedPreferences? = null
    private var gson: Gson? = null
    private var isInitFinish = false
    private var serviceBinder: AioCoreService.ServiceBinder? = null
    private var serviceBinderGets: MutableList<SingleTransport<AioCoreService.ServiceBinder>> = arrayListOf()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (service is AioCoreService.ServiceBinder) {
                serviceBinder = service
                isInitFinish = true
                for (get in serviceBinderGets) {
                    get.get(serviceBinder)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBinder = null
        }
    }

    fun init(startup: BaseAppStartup) {
        if (startup.openCrash) {
            Thread.setDefaultUncaughtExceptionHandler(AppCrashHandler())
        }
        Realm.init(startup.application)
        var config = RealmConfiguration.Builder()
        config.schemaVersion(startup.dbVersion)
        config.deleteRealmIfMigrationNeeded()
        config.name(startup.dbName + ".realm")
        realmBuilder = config
        Realm.setDefaultConfiguration(realmBuilder?.build())
        DataProvider.startup = startup
        threadPool = if (startup.threadPoolSize == 1) {
            Executors.newSingleThreadExecutor()
        } else {
            Executors.newFixedThreadPool(startup.threadPoolSize)
        }
        stickyEvents = ConcurrentHashMap()
        if (startup.openService) {
            if (serviceBinder == null) {
                val intent = Intent(startup.application, AioCoreService::class.java)
                startup.application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun getServiceBinder(trans: SingleTransport<AioCoreService.ServiceBinder>) {
        if (isInitFinish) {
            trans.get(serviceBinder)
        } else {
            serviceBinderGets.add(trans)
        }
    }

    fun getGsonClient(): Gson {
        if (gson == null) {
            gson = Gson()
        }
        return gson!!
    }

    fun getShared(): SharedPreferences {
        if (shared == null) {
            shared = startup.application.getSharedPreferences(startup.dbName + "_shared", Context.MODE_PRIVATE)
        }
        return shared!!
    }

    fun getSharedEditor(): SharedPreferences.Editor = getShared().edit()

    fun getRealmInstance(): Realm {
        return Realm.getDefaultInstance()
    }

    fun getOkHttp(): OkHttpClient {
        if (okHttp == null) {
            var builder = OkHttpClient.Builder()
            var connectTimeout = (60 * 1000).toLong()
            var readTimeout = (60 * 1000).toLong()
            var writeTimeout = (60 * 1000).toLong()
            builder.retryOnConnectionFailure(true)
            if (startup != null) {
                connectTimeout = startup.connectTimeout * 1000
                readTimeout = startup.readTimeOut * 1000
                writeTimeout = startup.writeTimeOut * 1000
            }
            builder = OkHttpClient.Builder()
            builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            builder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            okHttp = builder.build()
        }
        return okHttp!!
    }

    fun getThreadPool(): ExecutorService = threadPool!!

    val postingThreadState = object : ThreadLocal<PostingThreadState>() {
        override fun initialValue(): PostingThreadState {
            return PostingThreadState()
        }
    }

    class PostingThreadState {
        val eventQueue: MutableList<PostingEvent> = ArrayList()
        var isPosting: Boolean = false
        var isMainThread: Boolean = false
    }
}