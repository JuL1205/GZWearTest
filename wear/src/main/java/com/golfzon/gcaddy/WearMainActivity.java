package com.golfzon.gcaddy;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class WearMainActivity extends Activity {

    private TextView mTextView;
    private TextView mTextView2;

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mGyroscope;

    SensorEventListener mListener = new SensorEventListener() {
        float[] maxAccelValue = null;
        float[] maxGyroValue = null;
        @Override
        public void onSensorChanged(SensorEvent event) {
            for(int i = 0 ; i < event.values.length ; i++){
                if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    Log.e("test", "TYPE_ACCELEROMETER i = "+i+"/"+event.values[i]);
                } else{
                    Log.e("test", "TYPE_GYROSCOPE i = "+i+"/"+event.values[i]);
                }
            }

            setText(event.sensor.getType(), event.values);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        private void setText(int sensorType, float[] values){
            for(int i = 0 ; i < values.length ; i++){
                if(sensorType == Sensor.TYPE_ACCELEROMETER){
                    if(maxAccelValue == null){
                        maxAccelValue = new float[values.length];
                        for(float item : maxAccelValue){
                            item = -100;
                        }
                    }

                    if(values[i] > maxAccelValue[i]){
                        maxAccelValue[i] = values[i];
                        for(float item : maxAccelValue){
                            item = -100;
                        }
                    }
                } else{
                    if(maxGyroValue == null){
                        maxGyroValue = new float[values.length];
                    }

                    if(values[i] > maxGyroValue[i]){
                        maxGyroValue[i] = values[i];
                    }
                }
            }


            StringBuilder sb = new StringBuilder();

            if(sensorType == Sensor.TYPE_ACCELEROMETER){
                for (int i = 0 ; i < maxAccelValue.length ; i++){
                    sb.append(i).append("/").append(maxAccelValue[i]).append("\n");
                }
                sb.append(isAccelerating(maxAccelValue));
                mTextView.setText(sb.toString());
            } else{
                for (int i = 0 ; i < maxGyroValue.length ; i++){
                    sb.append(i).append("/").append(maxGyroValue[i]).append("\n");
                }
                sb.append(isAccelerating(maxGyroValue));
                mTextView2.setText(sb.toString());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_wear_main);

        mTextView = (TextView) findViewById(R.id.tv_text);
        mTextView2 = (TextView) findViewById(R.id.tv_text2);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(mListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mListener, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(mListener);

        super.onStop();
    }

    private static final int ACCELERATION_THRESHOLD = 13;

    private double isAccelerating(float[] values) {
        float ax = values[0];
        float ay = values[1];
        float az = values[2];

        final double magnitude = Math.sqrt(ax * ax + ay * ay + az * az);
        return magnitude;
    }
}
