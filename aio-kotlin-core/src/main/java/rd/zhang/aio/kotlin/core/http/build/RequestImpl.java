package rd.zhang.aio.kotlin.core.http.build;

import java.util.LinkedHashMap;

import rd.zhang.aio.kotlin.core.http.FilePorts;
import rd.zhang.aio.kotlin.core.http.RequestMethod;
import rd.zhang.aio.kotlin.core.http.ports.OkHttpCall;
import rd.zhang.aio.kotlin.core.listener.OnFailedListener;
import rd.zhang.aio.kotlin.core.listener.OnOfflineListener;
import rd.zhang.aio.kotlin.core.listener.OnStartListener;
import rd.zhang.aio.kotlin.core.listener.OnSuccessListener;
import rd.zhang.aio.kotlin.core.support.AioRequest;
import rd.zhang.android.aio.core.http.impl.OkHttpCallImpl;

/**
 * Created by Richard on 2017/8/23.
 */
public class RequestImpl<T, E> implements AioRequest<T, E> {

    private Build build;

    public RequestImpl(Build build) {
        this.build = build;
    }

    @Override
    public OkHttpCall listener(OnStartListener onStartListener, OnSuccessListener<T> onSuccessListener,
                               OnFailedListener<E> onFailedListener) {
        return new OkHttpCallImpl<T, E>().setRequestInfo(build, onStartListener,
                onSuccessListener, onFailedListener, null);
    }

    @Override
    public OkHttpCall listener(OnStartListener onStartListener, OnSuccessListener<T> onSuccessListener,
                               OnFailedListener<E> onFailedListener, OnOfflineListener offlineListener) {
        return new OkHttpCallImpl<T, E>().setRequestInfo(build, onStartListener, onSuccessListener,
                onFailedListener, offlineListener);
    }

    public static class Build {

        public RequestMethod method;
        public String url;
        public Class success, failed;
        public Object json;
        public Object tag;
        public boolean isList;
        public String upFilePath;
        public FilePorts file;
        public boolean multipart;
        public boolean requestInterceptor;
        public boolean responseInterceptor;
        public LinkedHashMap<String, String> replaces = new LinkedHashMap<>();

        public Build isList() {
            isList = true;
            return this;
        }

        public Build upFilePath(String path) {
            this.upFilePath = path;
            return this;
        }


        public Build tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Build requestInterceptor(boolean requestInterceptor) {
            this.requestInterceptor = requestInterceptor;
            return this;
        }

        public Build responseInterceptor(boolean responseInterceptor) {
            this.responseInterceptor = responseInterceptor;
            return this;
        }

        public Build url(String url) {
            this.url = url;
            return this;
        }

        public Build success(Class clazz) {
            this.success = clazz;
            return this;
        }

        public Build failed(Class clazz) {
            this.failed = clazz;
            return this;
        }

        public Build upJson(Object obj) {
            this.json = obj;
            return this;
        }

        public Build upFile(String upFilePath, FilePorts filePorts) {
            this.file = filePorts;
            this.upFilePath = upFilePath;
            return this;
        }

        public Build replace(String key, Object value) {
            replaces.put(key, String.valueOf(value));
            return this;
        }

        public Build method(int type) {
            switch (type) {
                case 1:
                    this.method = RequestMethod.GET;
                    break;
                case 2:
                    this.method = RequestMethod.POST;
                    break;
                case 3:
                    this.method = RequestMethod.DELETE;
                    break;

            }
            return this;
        }

        public Build multipart(boolean multipart) {
            this.multipart = multipart;
            return this;
        }

        public RequestImpl build() {
            return new RequestImpl(this);
        }

    }
}
