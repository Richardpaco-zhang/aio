package rd.zhang.aio.kotlin.core.bean;
/**
 * Created by Richard on 2017/8/24.
 */

public class PostingEvent {
    private String appoint;
    private Object arg;

    public String getAppoint() {
        return appoint;
    }

    public PostingEvent setAppoint(String appoint) {
        this.appoint = appoint;
        return this;
    }

    public Object getArg() {
        return arg;
    }

    public PostingEvent setArg(Object arg) {
        this.arg = arg;
        return this;
    }
}
