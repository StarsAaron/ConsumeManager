package com.rujian.consumemanager;

import android.app.Activity;
import android.app.Application;
import java.util.ArrayList;
import static android.os.Process.killProcess;
import static android.os.Process.myPid;

public class Myapplication extends Application {
    private static ArrayList<Activity> activityList = null;

    public Myapplication() {
        if (activityList == null) {
            activityList = new ArrayList<>();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("---------application start");
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void finishAllActivity() {
        //结束Activity
        for (Activity activity : activityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        killProcess(myPid());
    }
}
