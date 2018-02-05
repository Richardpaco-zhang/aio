package rd.zhang.aio.kotlin.core.event;

/**
 * Created by Richard on 2017/8/24.
 */

public class StickyModel {

    private String appoint;
    private Object arg;

    public String getAppoint() {
        return appoint;
    }

    public StickyModel setAppoint(String appoint) {
        this.appoint = appoint;
        return this;
    }

    public Object getArg() {
        return arg;
    }

    public StickyModel setArg(Object arg) {
        this.arg = arg;
        return this;
    }
}
