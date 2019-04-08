package com.example.myfirstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    ImageView compass_img;
    TextView heading_text;
    private SensorManager sensorManager;
    private Sensor[] sensors;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        compass_img = findViewById(R.id.imageView_compass);
        heading_text = findViewById(R.id.textView_heading);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // Check if phone can run compass.
        checkSensorExist();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int mAzimuth = 0;
        // Check if we are using the roatation sensor or acc and magnetic.
        if(sensors.length == 1) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
                mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
            }
        } else {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length);
                mLastAccelerometerSet = true;
            } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
                mLastMagnetometerSet = true;
            }

            if (mLastMagnetometerSet && mLastAccelerometerSet){
                SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
                SensorManager.getOrientation(rMat, orientation);
                mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
            }
        }

        mAzimuth = Math.round(mAzimuth);

        // Updater the UI
        updateUI(mAzimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSensorExist();
    }

    private void checkSensorExist(){
        // Check if phone has a compass sensor
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){
            //Check if phone instead ha an magnetic field and accelrometer.
            if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null ||
                    sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null){
                alterNoSensor();
            } else {
                sensors = new Sensor[2];
                sensors[0] = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensors[1] = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                registerSensor(sensors[0]);
                registerSensor(sensors[1]);
            }
        } else {
            sensors = new Sensor[1];
            sensors[0] = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            registerSensor(sensors[0]);
        }
    }

    private boolean registerSensor(Sensor sensor){
        return sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterSensors(){
        for (Sensor sensor : sensors) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    private void updateUI(int mAzimuth){
        compass_img.setRotation(-mAzimuth);
        heading_text.setText(getString(R.string.heading_compass, mAzimuth, getDirectionLetter(mAzimuth)));
    }

    private String getDirectionLetter(int mAzimuth){
        if (mAzimuth >= 350 || mAzimuth <= 10) {
            extra();
            return "N";
        }
        if (mAzimuth > 280)
            return "NW";
        if (mAzimuth > 260)
            return "W";
        if (mAzimuth > 190)
            return "SW";
        if (mAzimuth > 170)
            return "S";
        if (mAzimuth > 100)
            return "SE";
        if (mAzimuth > 80)
            return "E";
        return "NE";
    }

    private void extra(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        this.getWindow().getDecorView().setBackgroundColor(getColor(R.color.pink));
    }

    private void alterNoSensor(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }
}