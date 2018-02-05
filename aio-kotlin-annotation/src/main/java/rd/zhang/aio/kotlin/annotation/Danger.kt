package rd.zhang.aio.kotlin.annotation

/**
 * Created by Richard on 2017/6/5.
 */

enum class Danger(val permission: String, val permissionName: String) {

    WRITE_CONTACTS("android.permission.WRITE_CONTACTS", ""),
    GET_ACCOUNTS("android.permission.GET_ACCOUNTS", ""),
    READ_CONTACTS("android.permission.READ_CONTACTS", ""),
    READ_CALL_LOG("android.permission.READ_CALL_LOG", ""),
    READ_PHONE_STATE("android.permission.READ_PHONE_STATE", ""),
    CALL_PHONE("android.permission.CALL_PHONE", ""),
    WRITE_CALL_LOG("android.permission.WRITE_CALL_LOG", ""),
    USE_SIP("android.permission.USE_SIP", ""),
    PROCESS_OUTGOING_CALLS("android.permission.PROCESS_OUTGOING_CALLS", ""),
    ADD_VOICEMAIL("com.android.voicemail.permission.ADD_VOICEMAIL", ""),
    READ_CALENDAR("android.permission.READ_CALENDAR", ""),
    WRITE_CALENDAR("android.permission.WRITE_CALENDAR", ""),
    CAMERA("android.permission.CAMERA", "相机"),
    BODY_SENSORS("android.permission.BODY_SENSORS", ""),
    ACCESS_FINE_LOCATION("android.permission.ACCESS_FINE_LOCATION", ""),
    ACCESS_COARSE_LOCATION("android.permission.ACCESS_COARSE_LOCATION", ""),
    READ_EXTERNAL_STORAGE("android.permission.READ_EXTERNAL_STORAGE", ""),
    WRITE_EXTERNAL_STORAGE("android.permission.WRITE_EXTERNAL_STORAGE", ""),
    RECORD_AUDIO("android.permission.RECORD_AUDIO", ""),
    READ_SMS("android.permission.READ_SMS", ""),
    RECEIVE_WAP_PUSH("android.permission.RECEIVE_WAP_PUSH", ""),
    RECEIVE_MMS("android.permission.RECEIVE_MMS", ""),
    RECEIVE_SMS("android.permission.RECEIVE_SMS", ""),
    SEND_SMS("android.permission.SEND_SMS", ""),
    MANAGE_OVERLAY_PERMISSION("android.settings.action.MANAGE_OVERLAY_PERMISSION", "悬浮窗")

}
