package com.mobile.pos.util;

public class Waiter extends Thread
{
    private long lastUsed;
    private long period;
    private boolean stop;

    public Waiter(long period)
    {
        this.period = period;
        stop = false;
    }

    public void run()
    {
        long idle = 0;
        this.touch();
        do {
            idle = System.currentTimeMillis()-lastUsed;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            if (idle > period) {
                stopThread();
            }
        } while (!stop);
    }

    public boolean isStop() {
        return stop;
    }

    public synchronized void touch()
    {
        lastUsed = System.currentTimeMillis();
    }

    public synchronized void forceInterrupt()
    {
        this.interrupt();
    }

    public void stopThread()
    {
        stop = true;
    }

    public synchronized void setPeriod(long period)
    {
        this.period = period;
    }
}