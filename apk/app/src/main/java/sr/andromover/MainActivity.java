package sr.andromover;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import sr.andromover.move.MoveDetector;
import sr.andromover.move.MoveDetectorListener;

public class MainActivity extends Activity implements MoveDetectorListener {
    private MoveDetector moveDetector;

    @Override
    public void onMoveDetected(float x, float y) {
        Log.v("Move | ", "x: " + x + " y: " + y);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        moveDetector = new MoveDetector((SensorManager) getSystemService(SENSOR_SERVICE));
        moveDetector.registerListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        moveDetector.unregisterListener(this);
    }
}