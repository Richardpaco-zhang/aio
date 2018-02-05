package rd.zhang.android.aio.model;

import rd.zhang.aio.kotlin.annotation.GET;
import rd.zhang.aio.kotlin.annotation.GenerateHttp;
import rd.zhang.aio.kotlin.core.support.AioRequest;
import rd.zhang.android.aio.beans.UserInfo;

/**
 * Created by Richard on 2017/9/11.
 */
@GenerateHttp
public interface UserLogin {

    @GET(url = "", usingQi = true, usingPi = true)
    AioRequest<UserInfo, UserInfo> login();

}
