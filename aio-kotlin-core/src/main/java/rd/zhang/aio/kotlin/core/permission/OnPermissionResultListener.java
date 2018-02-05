package rd.zhang.aio.kotlin.core.permission;

import android.support.annotation.NonNull;

/**
 * Created by Richard on 2017/6/5.
 */

public interface OnPermissionResultListener {

    void onResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

}
