package rd.zhang.aio.kotlin.core.event;

import java.lang.reflect.Method;

/**
 * Created by Richard on 2017/8/24.
 */

public class EventModel {

    private Method method;
    private Class paramType;
    private String appoint;
    private boolean sticky;
    private boolean mainThread;

    public String getAppoint() {
        return appoint;
    }

    public EventModel setAppoint(String appoint) {
        this.appoint = appoint;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public EventModel setMethod(Method method) {
        this.method = method;
        return this;
    }

    public Class getParamType() {
        return paramType;
    }

    public EventModel setParamType(Class paramType) {
        this.paramType = paramType;
        return this;
    }

    public boolean isSticky() {
        return sticky;
    }

    public EventModel setSticky(boolean sticky) {
        this.sticky = sticky;
        return this;
    }

    public boolean isMainThread() {
        return mainThread;
    }

    public EventModel setMainThread(boolean mainThread) {
        this.mainThread = mainThread;
        return this;
    }
}
