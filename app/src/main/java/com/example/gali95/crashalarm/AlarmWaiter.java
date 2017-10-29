package com.example.gali95.crashalarm;

/**
 * Created by gali95 on 29.10.17.
 */

public class AlarmWaiter implements Runnable{

    private double timeStart,timeActual;
    private boolean abort,abortSMS;
    private CrashAnalyzer ca;
    final private long waitStep=10;

    public AlarmWaiter(CrashAnalyzer ca,double duration)
    {
        this.ca = ca;
        this.timeStart = duration;
    }

    public void Abort()
    {
        abort = true;
    }

    @Override
    public void run() {
        try {
            timeActual = timeStart;
            while (timeActual > 0) {
                Thread.sleep(waitStep);
                timeActual -= (double)waitStep/1000;
                if (abort) {
                    abortSMS = true;
                    break;
                }
            }

            ca.StopSound();
            if (!abortSMS) {
                ca.SendSMS();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
