package rd.zhang.android.aio.core.http.process

import okhttp3.*
import rd.zhang.aio.kotlin.core.*
import rd.zhang.aio.kotlin.core.aop.InterceptorCallback
import rd.zhang.aio.kotlin.core.aop.RequestInterceptor
import rd.zhang.aio.kotlin.core.aop.ResponseInterceptor
import rd.zhang.aio.kotlin.core.base.DataProvider
import rd.zhang.aio.kotlin.core.function.fromJson
import rd.zhang.aio.kotlin.core.function.isConnectWeb
import rd.zhang.aio.kotlin.core.function.logi
import rd.zhang.aio.kotlin.core.function.toJson
import rd.zhang.aio.kotlin.core.http.RequestMethod
import rd.zhang.aio.kotlin.core.http.build.RequestImpl
import rd.zhang.aio.kotlin.core.http.ports.UploadStatus
import rd.zhang.aio.kotlin.core.listener.OnFailedListener
import rd.zhang.aio.kotlin.core.listener.OnOfflineListener
import rd.zhang.aio.kotlin.core.listener.OnStartListener
import rd.zhang.aio.kotlin.core.listener.OnSuccessListener
import rd.zhang.android.aio.core.listener.OnHttpExecuteListener
import top.zibin.luban.Luban
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * Created by Richard on 2017/8/23.
 */
class OkHttpProcess<T, E>(var build: RequestImpl.Build,
                          private var onStartListener: OnStartListener,
                          var onSuccessListener: OnSuccessListener<T>,
                          var onFailedListener: OnFailedListener<E>,
                          var onOfflineListener: OnOfflineListener?) {

    var executeListener: OnHttpExecuteListener? = null

    fun setExecuteListener(onHttpExecuteListener: OnHttpExecuteListener?): OkHttpProcess<T, E> {
        this.executeListener = onHttpExecuteListener
        return this
    }

    fun start() {
        onStartListener.onStart()
        if (build.method == RequestMethod.POST) {
            postRequest()
        } else if (build.method == RequestMethod.GET) {
            getRequest()
        }
    }

    private fun getRequest() {
        interceptorRequest()
    }

    private fun postRequest() {
        if (build.file != null) {
            var fileSize = build.file.size
            var uploadSize = 0
            for (i in 0 until fileSize) {
                DataProvider.getThreadPool().execute(UploadFile(i, build, onSuccessListener,
                        onFailedListener, onOfflineListener, executeListener, object : UploadStatus {
                    override fun plus() {
                        uploadSize++
                    }

                    override fun ok(): Boolean = uploadSize == fileSize

                }))
            }
        } else {
            interceptorRequest()
        }
    }

    private fun interceptorRequest() {
        RequestInterceptorFunction(build.requestInterceptor, HttpHelp.buildHttpRequest(build), object : InterceptorCallback<Request.Builder> {
            override fun next(t: Request.Builder?) {
                HttpCall(build, build.responseInterceptor, onSuccessListener,
                        onFailedListener, onOfflineListener, executeListener).beginAsyncCall(t)
            }

            override fun interrupt(throwable: Throwable?, customerCode: Int) {
                onFailedListener.onFailed(throwable, null, customerCode)
            }
        }).begin()
    }

    //上传图片
    class UploadFile<T, E>(private var index: Int, var build: RequestImpl.Build,
                           var onSuccessListener: OnSuccessListener<T>,
                           var onFailedListener: OnFailedListener<E>,
                           var onOfflineListener: OnOfflineListener?,
                           var executeListener: OnHttpExecuteListener?,
                           var uploadStatus: UploadStatus) : Runnable {

        override fun run() {
            var info = build.file.get(index)
            if (info.isCompress) {
                try {
                    var file = Luban.with(DataProvider.startup.application).load(info.file).get()
                    statUploadCall(file)
                } catch (e: IOException) {
                    e.printStackTrace()
                    statUploadCall(info.file)
                }
            } else {
                statUploadCall(info.file)
            }
        }

        private fun statUploadCall(file: File) {
            RequestInterceptorFunction(build.requestInterceptor, HttpHelp.buildRequestFile(build, index, file), object :
                    InterceptorCallback<Request.Builder> {
                override fun next(t: Request.Builder?) {

                    HttpCall(build, build.responseInterceptor, onSuccessListener,
                            onFailedListener, onOfflineListener, executeListener)
                            .beginSyncCallByUpload(t, uploadStatus)

                }

                override fun interrupt(throwable: Throwable?, customerCode: Int) {
                    onFailedListener.onFailed(throwable, null, customerCode)
                    executeListener?.onInterrupt()
                }
            }).begin()
        }

    }

    class HttpCall<T, E>(var build: RequestImpl.Build, var interceptor: Boolean, var onSuccessListener: OnSuccessListener<T>,
                         var onFailedListener: OnFailedListener<E>,
                         var onOfflineListener: OnOfflineListener?,
                         var executeListener: OnHttpExecuteListener?) {

        private var currentRetryCount = 0

        fun beginAsyncCall(builder: Request.Builder?) {
            DataProvider.getOkHttp().newCall(builder?.build()).enqueue(object : Callback {
                override fun onResponse(call: Call?, response: Response?) {
                    var location = response?.header("Location")
                    if (location != null && location != response?.request()?.url().toString() && response?.request()!!.method() == "POST") {
                        logi("Http Redirects $location")
                        DataProvider.getOkHttp().newCall(builder?.get()!!.url(location)!!.build()).enqueue(this)
                    } else {
                        ResponseCallback(build, interceptor, onSuccessListener,
                                onFailedListener, onOfflineListener, executeListener).success(response)
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    if (e is SocketTimeoutException && currentRetryCount < DataProvider.startup.retryCount) {
                        currentRetryCount++
                        DataProvider.getOkHttp().newCall(builder?.build()).enqueue(this)
                    } else {
                        call?.cancel()
                        currentRetryCount = DataProvider.startup.retryCount
                    }

                    if (currentRetryCount == DataProvider.startup.retryCount) {

                        ResponseCallback(build, interceptor, onSuccessListener,
                                onFailedListener, onOfflineListener, executeListener).failed(true, e)
                    }
                }
            })
        }

        fun beginSyncCallByUpload(builder: Request.Builder?, uploadStatus: UploadStatus) {
            var call = DataProvider.getOkHttp().newCall(builder?.build())
            try {
                var response = call.execute()
                if (response.isSuccessful) {
                    var location = response?.header("Location")
                    if (location != null && location != response?.request()?.url().toString() && response?.request()!!.method() == "POST") {
                        logi("Http redirects at $location")
                        beginSyncCallByUpload(builder?.get()!!.url(location), uploadStatus)
                    } else {
                        uploadStatus.plus()
                        ResponseCallback(build, interceptor, onSuccessListener,
                                onFailedListener, onOfflineListener, executeListener).success(uploadStatus.ok(), response)
                    }
                } else {
                    ResponseCallback(build, interceptor, onSuccessListener,
                            onFailedListener, onOfflineListener, executeListener).failed(uploadStatus.ok(), null)
                }
            } catch (e: IOException) {
                ResponseCallback(build, interceptor, onSuccessListener,
                        onFailedListener, onOfflineListener, executeListener).failed(uploadStatus.ok(), e)
            }
        }

    }

    class ResponseCallback<T, E>(var build: RequestImpl.Build, private var interceptor: Boolean, private var onSuccessListener: OnSuccessListener<T>,
                                 var onFailedListener: OnFailedListener<E>,
                                 private var onOfflineListener: OnOfflineListener?,
                                 private var executeListener: OnHttpExecuteListener?) {

        fun success(response: Response?) {
            success(true, response)
        }

        fun success(done: Boolean, response: Response?) {
            ResponseInterceptorFunction(interceptor, response, object : InterceptorCallback<Response> {
                override fun next(t: Response) {
                    if (t?.code()!! >= 200 && t?.code() < 300) {
                        formatSuccess(t, done)
                    } else {
                        formatFailed(done, t)
                    }
                }

                override fun interrupt(throwable: Throwable?, customerCode: Int) {
                    onFailedListener.onFailed(throwable, null, customerCode)
                }
            }).begin()
        }

        fun failed(done: Boolean, e: IOException?) {
            if (e !is SocketException || e == null) {
                when (e) {
                    is TimeoutException -> sendFailedCode(e, REQUEST_TIMEOUT)
                    is FileNotFoundException -> sendFailedCode(e, NO_SUCH_FILE_OR_DIRECTORY)
                    else -> {
                        if (!isConnectWeb()) {
                            sendFailedCode(e, NO_NET_CONNECT)
                        } else {
                            sendFailedCode(e, OTHER_ERROR)
                        }
                    }
                }
            } else {
                if (onOfflineListener != null) {
                    DataProvider.delivery.post { onOfflineListener?.offline() }
                } else {
                    sendFailedCode(e, NO_NET_CONNECT)
                }
            }
            checkUploadDone(done, false)
        }

        private fun sendFailedCode(e: IOException?, code: Int) {
            DataProvider.delivery.post {
                onFailedListener.onFailed(e, null, code)
            }
        }

        fun formatSuccess(response: Response, done: Boolean) {
            var data: Any? = null
            try {
                data = if (build.isList) {
                    rd.zhang.aio.kotlin.core.http.HttpHelp.toArray(response.body()!!.charStream()!!, build.success)
                } else {
                    if (build.success.canonicalName == "java.io.File") {

                    } else {
                        fromJson(response.body()?.charStream()!!, build.success)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (data == null) {
                DataProvider.delivery.post {
                    onFailedListener.onFailed(IllegalArgumentException("出现错误!"), null, OTHER_ERROR)
                }
            } else {
                DataProvider.delivery.post {
                    onSuccessListener.onSuccess(data as T, done)
                    checkUploadDone(done, false)
                }
            }
        }

        fun formatFailed(done: Boolean, response: Response) {
            var data: Any? = null
            try {
                data = fromJson(response.body()?.charStream()!!, build.failed)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (data == null) {
                DataProvider.delivery.post {
                    onFailedListener.onFailed(IllegalArgumentException("出现错误!"), null, OTHER_ERROR)
                }
            } else {
                DataProvider.delivery.post {
                    onFailedListener.onFailed(null, data as E, response.code())
                    checkUploadDone(done, true)
                }
            }
        }

        private fun checkUploadDone(done: Boolean, failed: Boolean) {
            DataProvider.delivery.postDelayed({
                if (done) {
                    if (failed) {
                        executeListener?.onFailed()
                    } else {
                        executeListener?.onSuccess()
                    }
                }
            }, 100)
        }
    }

    class ResponseInterceptorFunction(private var responseInterceptor: Boolean,
                                      private var response: Response?,
                                      var callback: InterceptorCallback<Response>) {

        fun begin() {
            var interceptor = DataProvider.startup.getResponseIt()
            var index = 0
            if (interceptor.isNotEmpty() && responseInterceptor) {
                getResponseInterceptor(interceptor[index])?.process(response, object : InterceptorCallback<Response> {
                    override fun next(t: Response?) {
                        index++
                        if (index < interceptor.size) {
                            getResponseInterceptor(interceptor[index])?.process(t, this)
                        } else {
                            callback.next(t)
                        }
                    }

                    override fun interrupt(throwable: Throwable?, customerCode: Int) {
                        callback.interrupt(throwable, customerCode)
                    }
                })
            } else {
                callback.next(response)
            }
        }

        fun getResponseInterceptor(clazz: Class<*>): ResponseInterceptor? {
            var instance = clazz.newInstance()
            if (instance != null) {
                if (instance is ResponseInterceptor) {
                    return instance
                }
            }
            return null
        }

    }

    class RequestInterceptorFunction(private var requestInterceptor: Boolean,
                                     private var builder: Request.Builder?,
                                     var callback: InterceptorCallback<Request.Builder>) {
        fun begin() {
            var interceptor = DataProvider.startup.getRequestIt()
            var index = 0
            if (interceptor.isNotEmpty() && requestInterceptor) {
                getRequestInterceptor(interceptor[0])?.process(builder, object :
                        InterceptorCallback<Request.Builder> {

                    override fun next(t: Request.Builder?) {
                        index++
                        if (index < interceptor.size) {
                            getRequestInterceptor(interceptor[index])?.process(t, this)
                        } else {
                            callback.next(t)
                        }
                    }

                    override fun interrupt(throwable: Throwable?, customerCode: Int) {
                        callback.interrupt(throwable, customerCode)
                    }
                })
            } else {
                callback.next(builder)
            }
        }

        fun getRequestInterceptor(clazz: Class<*>): RequestInterceptor? {
            var instance = clazz.newInstance()
            if (instance != null) {
                if (instance is RequestInterceptor) {
                    return instance
                }
            }
            return null
        }
    }

    class HttpHelp {

        companion object {
            fun buildHttpRequest(build: RequestImpl.Build): Request.Builder {
                var request_builder = Request.Builder()
                var baseIp = DataProvider.startup.baseIp + build.url
                if (build.replaces.size > 0) {
                    for ((key, value) in build.replaces) {
                        baseIp = baseIp.replace("$" + key, value)
                    }
                }
                if (build.method == RequestMethod.POST) {
                    if (build.json != null) {
                        var jsonStr: String? = if (build.json is String) {
                            build.json as String
                        } else {
                            toJson(build.json)
                        }
                        logi("Request json $jsonStr")
                        var requestBody = RequestBody.create(MediaType.parse("application/json"), jsonStr)
                        request_builder.post(requestBody)
                    }
                }
                logi("Http ${build.method} at $baseIp")
                return request_builder.url(baseIp)
            }

            fun buildRequestFile(build: RequestImpl.Build, index: Int, file: File): Request.Builder {
                var info = build.file.get(index)
                var name = info.name
                var request_builder = buildHttpRequest(build)
                var multipartRequestBody = MultipartBody.Builder()
                multipartRequestBody.setType(MultipartBody.FORM)
                var requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                multipartRequestBody.addFormDataPart(build.upFilePath, name, requestFile)
                request_builder.post(multipartRequestBody.build())
                return request_builder
            }
        }


    }

}

