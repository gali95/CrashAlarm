package com.example.gali95.crashalarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.telephony.SmsManager;

/**
 * Created by gali95 on 29.10.17.
 */

public class CrashAnalyzer implements SensorEventListener {

    float prevX,prevY,prevZ;
    private boolean alarmStatus;
    private boolean fresh;
    private boolean properSettings;
    private boolean paused;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Context owner;

    private String SMSNumber;
    private String SMSMessage;

    private AlarmWaiter actualAlarm;

    private MediaPlayer alarmSound;

    private double alarmDuration;

    public CrashAnalyzer(Context owner)
    {
        if(owner==null)
        {
            return;
        }
        this.owner = owner;
        paused = true;
        InitSensor();
        InitSound();
    }

    private float rawToNormalized(float entry)
    {
        return entry;
    }

    private boolean shouldRaiseAlarm(float previousData,float newData)
    {
        float maxAllowableDifference = 20;
        return (Math.abs(previousData-newData)>maxAllowableDifference);
    }

    public String getSMSNumber() {
        return SMSNumber;
    }

    public void setSMSNumber(String SMSNumber) {
        this.SMSNumber = SMSNumber;
    }

    public String getSMSMessage() {
        return SMSMessage;
    }

    public void setSMSMessage(String SMSMessage) {
        this.SMSMessage = SMSMessage;
    }

    public double getAlarmDuration() {
        return alarmDuration;
    }

    public void setAlarmDuration(double alarmDuration) {
        this.alarmDuration = alarmDuration;
    }

    public void SetPaused(boolean val)
    {
        paused = val;
    }

    public boolean GetPaused()
    {
        return paused;
    }

    private void InitSensor()
    {
        mSensorManager = (SensorManager) owner.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_UI);
        properSettings = true;
    }

    private void InitSound()
    {
        alarmSound = MediaPlayer.create(owner, R.raw.beep01a);
        alarmSound.setLooping(true);
    }

    private void PlaySound()
    {
        alarmSound.start();
    }

    public void StopSound()
    {
        alarmSound.stop();
    }

    private void SendSMS(String number, String message)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
    }

    public void SendSMS()
    {
        SendSMS(SMSNumber,SMSMessage);
    }

    public void ActivateAlarm(double tiem)
    {
        if(actualAlarm != null) return;

        actualAlarm = new AlarmWaiter(this,tiem);
        PlaySound();
        (new Thread(actualAlarm)).start();
    }

    public boolean IsAlarmActivated()
    {
        if(actualAlarm != null) return true;
        else return false;
    }

    public void DeactivateAlarm()
    {
        if(actualAlarm == null) return;

        actualAlarm.Abort();
        actualAlarm = null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(actualAlarm != null) return;
        if(!properSettings) return;
        if(paused) return;

        float actualX = rawToNormalized(sensorEvent.values[0]);
        float actualY = rawToNormalized(sensorEvent.values[1]);
        float actualZ = rawToNormalized(sensorEvent.values[2]);

        if(fresh)
        {
            fresh = false;
        }
        else
        {
            if(shouldRaiseAlarm(prevX,actualX) || shouldRaiseAlarm(prevY,actualY) || shouldRaiseAlarm(prevZ,actualZ))
            {
                ActivateAlarm(alarmDuration);
            }
        }

        prevX = actualX;
        prevY = actualY;
        prevZ = actualZ;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
