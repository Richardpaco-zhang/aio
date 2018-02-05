package rd.zhang.aio.kotlin.core.service;

import android.app.Activity;

import java.util.Stack;

/**
 * Author: Richard paco
 * Date: 2017/11/22
 */

public class RouterActivityManager {

    private static Stack<Activity> activityStack;
    private static RouterActivityManager instance;

    public static RouterActivityManager getActivityManager() {
        if (instance == null) {
            instance = new RouterActivityManager();
        }
        return instance;
    }

    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    public Activity currentActivity() {
        if (activityStack == null || activityStack.empty()) {
            return null;
        }
        return activityStack.lastElement();
    }

    public void popAllActivityExceptOne(Activity destroy) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(destroy.getClass())) {
                break;
            }
            destroyActivity(activity);
        }
    }

    public void destroyActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.finish();
        if (activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
    }

}
