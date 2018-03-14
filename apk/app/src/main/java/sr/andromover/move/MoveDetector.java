package sr.andromover.move;

import android.hardware.SensorManager;

import java.util.HashSet;
import java.util.Set;

public class MoveDetector implements SensorMoveReaderListener {
    private static final float MAX_SENSOR_VALUE = 6;
    private static final float MAX_MOVE_VALUE = 100;

    private Set<MoveDetectorListener> listeners = new HashSet<>();
    private SensorMoveReader sensorMoveReader;

    public MoveDetector(SensorManager sensorManager) {
        sensorMoveReader = new SensorMoveReader(sensorManager);
    }

    public void registerListener(MoveDetectorListener listener) {
        listeners.add(listener);
        // first listener
        if (listeners.size() == 1) {
            sensorMoveReader.registerListener(this);
        }
    }

    public void unregisterListener(MoveDetectorListener listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            sensorMoveReader.unregisterListener(this);
        }
    }

    @Override
    public void onSensorMove(float x, float y, float z) {
        float xMove = getXMoveFromSensorResults(x, y, z);
        float yMove = getYMoveFromSensorResults(x, y, z);
        notifyListeners(xMove, yMove);
    }

    private float getXMoveFromSensorResults(float x, float y, float z) {
        return Math.min(1, Math.abs(y) / MAX_SENSOR_VALUE) * MAX_MOVE_VALUE * Math.signum(y);
    }

    private float getYMoveFromSensorResults(float x, float y, float z) {
        return Math.min(1, Math.abs(x) / MAX_SENSOR_VALUE) * MAX_MOVE_VALUE * Math.signum(-x);
    }

    private void notifyListeners(float x, float y) {
        for (MoveDetectorListener listener : listeners) {
            listener.onMoveDetected(x, y);
        }
    }
}
