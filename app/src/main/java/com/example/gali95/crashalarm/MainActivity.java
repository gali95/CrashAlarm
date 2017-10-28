package com.example.gali95.crashalarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    ProgressBar pb;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensListenero mSensorEventListener;
    final private double modifier = 4;
    private MediaPlayer mp;

    class SensListenero implements SensorEventListener {

        private ProgressBar pbX,pbY,pbZ;
        private int prevX,prevY,prevZ;
        private boolean fresh;
        private double diffToBeep = 5;
        private Context cont;
        private MediaPlayer mp;

        public SensListenero(Context cont,ProgressBar pbX, ProgressBar pbY, ProgressBar pbZ)
        {
            this.pbX = pbX;
            this.pbY = pbY;
            this.pbZ = pbZ;
            this.cont = cont;
            mp = MediaPlayer.create(this.cont, R.raw.beep01a);
            fresh = true;
        }
        public void PlaySound()
        {
            mp.start();
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            //pbX.setProgress(pbX.getProgress()+2);
            int actualX = 50+(int)(modifier*sensorEvent.values[0]);
            int actualY = 50+(int)(modifier*sensorEvent.values[1]);
            int actualZ = 50+(int)(modifier*sensorEvent.values[2]);

            if(fresh)
            {
                fresh = false;
            }
            else
            {
                if(Math.abs(actualX-prevX) > diffToBeep) PlaySound();
                if(Math.abs(actualY-prevY) > diffToBeep) PlaySound();
                if(Math.abs(actualZ-prevZ) > diffToBeep) PlaySound();
            }

            prevX = actualX;
            prevY = actualY;
            prevZ = actualZ;

            pbX.setProgress(prevX);
            pbY.setProgress(prevY);
            pbZ.setProgress(prevZ);


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorEventListener = new SensListenero(this,(ProgressBar) findViewById(R.id.xRotPB),(ProgressBar) findViewById(R.id.yRotPB),(ProgressBar) findViewById(R.id.zRotPB));

        mSensorManager.registerListener(mSensorEventListener,mSensor,SensorManager.SENSOR_DELAY_UI);
        mp = MediaPlayer.create(this, R.raw.beep01a);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mp.start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
