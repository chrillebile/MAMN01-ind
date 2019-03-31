package com.example.myfirstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    ImageView compass_img;
    TextView heading_text;
    private SensorManager sensorManager;
    private Sensor rotation, accelerometer, magnetic_field;
    private boolean sensorExist = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    int mAzimuth;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        compass_img = findViewById(R.id.imageView_compass);
        heading_text = findViewById(R.id.textView_heading);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //
        checkSensorExist();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        compass_img.setRotation(-mAzimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void checkSensorExist(){
        // Check if phone has a compass sensor
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){
            alterNoSensor();
        } else {
            rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorExist = registerSensor(rotation);
        }
    }

    private boolean registerSensor(Sensor sensor){
        return sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterSensor(Sensor sensor){
        sensorManager.unregisterListener(this, sensor);
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensor(rotation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        unregisterSensor(rotation);
    }

}
