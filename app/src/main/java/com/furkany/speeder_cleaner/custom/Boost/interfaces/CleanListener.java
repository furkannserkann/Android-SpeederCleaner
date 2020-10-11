package com.furkany.speeder_cleaner.custom.Boost.interfaces;


public interface CleanListener {
    void onStarted();
    void onFinished(long availableRam, long totalRam);
}
