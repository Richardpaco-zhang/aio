package rd.zhang.aio.kotlin.core

/**
 * Created by Richard on 2017/9/10.
 */
//code
var OTHER_ERROR_BUT_SUCCESS = -99999
val OTHER_ERROR = -10000 //其他错误
val REQUEST_TIMEOUT = -10001 //请求超时
val NO_NET_CONNECT = -10002 //无网络连接
val NO_SUCH_FILE_OR_DIRECTORY = -10003//没有找到文件或者文件夹

//net work
val NETWORK_NONE = 0//没有网络连接
val NETWORK_WIFI = 1//wifi连接
val NETWORK_2G = 2 //手机网络数据连接类型
val NETWORK_3G = 3
val NETWORK_4G = 4
val NETWORK_MOBILE = 5