package rd.zhang.aio.kotlin.core.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rd.zhang.aio.kotlin.annotation.Danger;


/**
 * Created by Richard on 2017/6/5.
 */
public class PermissionUtils {

    private Activity activity;

    private Danger[] permissions;

    public final int CODE_MULTI_PERMISSION = 100;

    public interface OnPermissionRequestResponseListener {
        void onPermissionGranted();

        void onPermissionDenial(Danger[] permissions);
    }

    public PermissionUtils(Activity activity) {
        this.activity = activity;
    }

    /**
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public void requestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults, OnPermissionRequestResponseListener permissionGrant) {
        if (activity == null) {
            return;
        }
        if (requestCode == CODE_MULTI_PERMISSION) {
            requestMultiResult(activity, permissions, grantResults, permissionGrant);
            return;
        }
        if (requestCode < 0 || requestCode >= this.permissions.length) {
            Toast.makeText(activity, "illegal requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGrant.onPermissionGranted();
        } else {
            openSettingActivity(activity, "Result" + this.permissions[requestCode].getPermission(), permissionGrant);
        }
    }

    private void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, OnPermissionRequestResponseListener permissionGrant) {
        if (activity == null) {
            return;
        }
        Map<String, Integer> perms = new HashMap<>();
        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }
        if (notGranted.size() == 0) {
            /**
             * 权限申请都同意
             */
            permissionGrant.onPermissionGranted();
        } else {
            openSettingActivity(activity, "权限必须开启!", permissionGrant);
        }
    }

    private void openSettingActivity(final Activity activity, String message, final OnPermissionRequestResponseListener permissionGrant) {
        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
                permissionGrant.onPermissionDenial(null);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionGrant.onPermissionDenial(permissions);
            }
        });
    }

    public void requestPermissions(final Danger[] permissions, final OnPermissionRequestResponseListener listener) {
        this.permissions = permissions;
        final List<String> permissionsList = getNoGrantedPermission(permissions, activity, false);
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(permissions, activity, true);
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                    CODE_MULTI_PERMISSION);
        } else if (shouldRationalePermissionsList.size() > 0) {
            for (Danger permission : permissions) {
                showMessageOKCancel(activity, "需要打开" + permission.getPermissionName() + "权限",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                                        CODE_MULTI_PERMISSION);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onPermissionDenial(permissions);
                            }
                        });
            }
        } else {
            listener.onPermissionGranted();
        }
    }


    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("去打开", okListener)
                .setNegativeButton("取消", cancelListener)
                .create()
                .show();

    }


    private ArrayList<String> getNoGrantedPermission(Danger[] requestPermissions, Activity activity, boolean isShouldRationale) {
        ArrayList<String> permissions = new ArrayList<>();
        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i].getPermission();
            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "请打开权限!", Toast.LENGTH_SHORT)
                        .show();
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }

                } else {
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                }
            }
        }

        return permissions;
    }

    public static boolean checkFloatWindowPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return checkOps(activity);
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean checkOps(Activity activity) {
        try {
            Object object = activity.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = activity.getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject1);
            return m == AppOpsManager.MODE_ALLOWED || !RomUtils.isDomesticSpecialRom();
        } catch (Exception ignore) {
        }
        return false;
    }

}
