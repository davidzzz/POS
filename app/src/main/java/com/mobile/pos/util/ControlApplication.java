package com.mobile.pos.util;

import android.app.Application;

public class ControlApplication extends Application
{
    private Waiter waiter;

    public ControlApplication(){
        onCreate();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        waiter = new Waiter(10*60*1000);
        waiter.start();
    }

    public void touch()
    {
        waiter.touch();
    }

    public boolean isStop() {
        return waiter.isStop();
    }
}