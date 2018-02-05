package rd.zhang.aio.kotlin.compiler.bean;

/**
 * Created by Richard on 2017/9/9.
 */

public class WiredInfo {

    private String methodName;
    private String newValueFieldName;
    private String providerFieldName;
    private boolean isPresenter;
    private boolean isLazyPresenter;

    public String getNewValueFieldName() {
        return newValueFieldName;
    }

    public WiredInfo setNewValueFieldName(String newValueFieldName) {
        this.newValueFieldName = newValueFieldName;
        return this;
    }

    public String getProviderFieldName() {
        return providerFieldName;
    }

    public WiredInfo setProviderFieldName(String providerFieldName) {
        this.providerFieldName = providerFieldName;
        return this;
    }

    public boolean isLazyPresenter() {
        return isLazyPresenter;
    }

    public void setLazyPresenter(boolean lazyPresenter) {
        isLazyPresenter = lazyPresenter;
    }

    public String getMethodName() {
        return methodName;
    }

    public WiredInfo setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public boolean isPresenter() {
        return isPresenter;
    }

    public WiredInfo setPresenter(boolean presenter) {
        isPresenter = presenter;
        return this;
    }
}
