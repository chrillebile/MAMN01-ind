package com.example.myfirstapp;

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
            x.setText("X: " + sensorEvent.values[0]);
            y.setText("Y: " + sensorEvent.values[1]);
            z.setText("Z: " + sensorEvent.values[2]);
            direction.setText(getDirection());
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

    private boolean registerSensor(Sensor sensor){
        return sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterSensor(){
        sensorManager.unregisterListener(this, gravitySensor);
    }

    private String getDirection(){
        // Add directions
        String direct = "Up";

        return direct;
    }

    private void alterNoSensor(){

    }
}
