package com.example.myfirstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    // Add formation to string, String x_text, y_text, z_text, direction_text;
    TextView x, y, z, direction;
    private SensorManager sensorManager;
    private Sensor gravitySensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        x = findViewById(R.id.textView_X);
        y = findViewById(R.id.textView_Y);
        z = findViewById(R.id.textView_Z);
        direction = findViewById(R.id.textView_direction);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        checkSensors();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            x.setText(getString(R.string.x_accelerometer, sensorEvent.values[0]));
            y.setText(getString(R.string.y_accelerometer, sensorEvent.values[1]));
            z.setText(getString(R.string.z_accelerometer, sensorEvent.values[2]));
            direction.setText(getString(R.string.direction_accelerometer, getDirection(sensorEvent)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSensors();
    }

    private void checkSensors(){
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
            alterNoSensor();
        } else {
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            registerSensor(gravitySensor);
        }
    }

    private void registerSensor(Sensor sensor){
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterSensor(){
        sensorManager.unregisterListener(this, gravitySensor);
    }

    private String getDirection(SensorEvent sensorEvent){
        // Add directions
        int biggest = 0;
        float max = 0;
        for(int i = 0; i < sensorEvent.values.length; i++){
            if(Math.abs(sensorEvent.values[i]) > max){
                biggest = i;
                max = Math.abs(sensorEvent.values[i]);
            }
        }

        switch (biggest){
            case 0:
                if(sensorEvent.values[0] > 0){
                    return "LEFT";
                } else {
                    return "RIGHT";
                }
            case 1:
                if(sensorEvent.values[1] > 0){
                    return "UP";
                } else {
                    return "DOWN";
                }
            case 2:
                if(sensorEvent.values[2] > 0){
                    return "SCREEN UP";
                } else {
                    return "SCREEN DOWN";
                }
            default:
                return "ERROR";
        }
    }

    private void alterNoSensor(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Accelerometer.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }
}
