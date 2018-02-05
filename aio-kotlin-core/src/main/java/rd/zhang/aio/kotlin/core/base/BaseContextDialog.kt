package rd.zhang.aio.kotlin.core.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

/**
 * Created by Richard on 2017/9/10.
 */
class BaseContextDialog : Dialog {

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {

    }

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) :
            super(context, cancelable, cancelListener) {

    }

}
