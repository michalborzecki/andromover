package sr.andromover.move;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.HashSet;
import java.util.Set;

public class SensorMoveReader implements SensorEventListener {
    private Set<SensorMoveReaderListener> listeners = new HashSet<>();
    private SensorManager sensorManager;

    public SensorMoveReader(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public void registerListener(SensorMoveReaderListener listener) {
        listeners.add(listener);
        // first listener
        if (listeners.size() == 1) {
            sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    public void unregisterListener(SensorMoveReaderListener listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            sensorManager.unregisterListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            );
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            notifyListeners(x, y, z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    private void notifyListeners(float x, float y, float z) {
        for (SensorMoveReaderListener listener : listeners) {
            listener.onSensorMove(x, y, z);
        }
    }
}
