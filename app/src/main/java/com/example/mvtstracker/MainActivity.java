package com.example.mvtstracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private LocationManager locationManager;
    private TextView locationText;
    private Button updateButton;
    private int updateCount = 0; // Counter for location updates

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private TextView sensorDataText;
    private float[] lastAccel = new float[3];
    private float[] lastGyro = new float[3];
    private boolean shakeDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        locationText = findViewById(R.id.location_text);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        sensorDataText = findViewById(R.id.sensor_data_text);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this, accelerometer, 20000000);
        sensorManager.registerListener(this, gyroscope, 20000000);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String msg = "Permission non attribue";
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            Log.d(TAG, "Permission octroyée");
            requestLocationUpdate();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccel, 0, event.values.length);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            System.arraycopy(event.values, 0, lastGyro, 0, event.values.length);
        }

        updateSensorDataDisplay();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    // not used
    }

    private void requestLocationUpdate() {
        Log.d(TAG, "Requesting location update");

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "GPS provider is not enabled");
        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCount++;
                Log.d(TAG, "Location changed: " + location.toString() + ", Update count: ");
                Log.d(TAG, "Compteur d'update: " + updateCount);

                String info = "Latitude: " + location.getLatitude() +
                        "\nLongitude: " + location.getLongitude() +
                        "\nAltitude: " + location.getAltitude() +
                        "\nSpeed: " + location.getSpeed() +
                        "\nBearing: " + location.getBearing();
                locationText.setText(info);
                //updateLocationInfo(location);
            }
        };

        try {
            // Request update from GPS provider
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

            // Additionally, request update from network provider as a fallback
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            Log.e(TAG, "Permission issue: " + e.getMessage());
        }
    }


    private void updateSensorDataDisplay() {
        String data = "Acceleration:\nX: " + lastAccel[0] + "\nY: " + lastAccel[1] + "\nZ: " + lastAccel[2] +
                "\nAngular Velocity:\nX: " + lastGyro[0] + "\nY: " + lastGyro[1] + "\nZ: " + lastGyro[2] +
                "\nShake Detected: " + shakeDetected;
        sensorDataText.setText(data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions granted");
            requestLocationUpdate();
        } else {
            Log.d(TAG, "Permissions denied");
        }
    }
}
