package com.furkany.speeder_cleaner.custom.Boost.tasks;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.furkany.speeder_cleaner.custom.RAMBooster;
import com.furkany.speeder_cleaner.custom.Boost.interfaces.CleanListener;
import com.furkany.speeder_cleaner.custom.Boost.utils.Constants;
import com.furkany.speeder_cleaner.custom.Boost.utils.ProcessInfo;
import com.furkany.speeder_cleaner.custom.Boost.utils.Utils;

import java.util.List;

public class CleanTask implements Runnable, Constants {

    private Context context;
    private List<ProcessInfo> appProcessInfos;
    private CleanListener listener;

    public CleanTask(Context context, List<ProcessInfo> appProcessInfos, CleanListener listener) {
        this.context = context;
        this.appProcessInfos = appProcessInfos;
        this.listener = listener;
    }


    @Override
    public void run() {
        if (RAMBooster.isDEBUG())
            Log.d(TAG, "Cleaner started...");
        listener.onStarted();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {

        }

        if (appProcessInfos != null) {
            killAppProcesses(appProcessInfos);
        }
        long availableRam = Utils.calculateAvailableRAM(context)/weight;
        long totalRam= Utils.calculateTotalRAM()/weight;

        listener.onFinished(availableRam,totalRam);
        if (RAMBooster.isDEBUG())
            Log.d(TAG, "Cleaner finished");
    }

    private void killAppProcesses(List<ProcessInfo> runningAppProcesses) {
        for (ProcessInfo processInfo : runningAppProcesses) {
            killBackgroundProcess(processInfo.getProcessName());
        }
    }

    private void killBackgroundProcess(String packageName) {

        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(packageName);
    }

}
