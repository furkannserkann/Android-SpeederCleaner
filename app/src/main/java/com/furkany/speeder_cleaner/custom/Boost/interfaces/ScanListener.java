package com.furkany.speeder_cleaner.custom.Boost.interfaces;

import com.furkany.speeder_cleaner.custom.Boost.utils.ProcessInfo;

import java.util.List;

public interface ScanListener {

    void onStarted();
    void onFinished(long availableRam, long totalRam, List<ProcessInfo> appsToClean);

}
