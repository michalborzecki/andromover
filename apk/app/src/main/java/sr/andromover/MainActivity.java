package sr.andromover;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import sr.andromover.click.ButtonType;
import sr.andromover.message.ClickMessage;
import sr.andromover.message.Message;
import sr.andromover.message.MoveMessage;
import sr.andromover.move.MoveDetector;
import sr.andromover.move.MoveDetectorListener;

public class MainActivity extends Activity implements MoveDetectorListener {
    private MoveDetector moveDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON & WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main);

        addButtonsClickListeners();

        moveDetector = new MoveDetector((SensorManager) getSystemService(SENSOR_SERVICE));
        moveDetector.registerListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        moveDetector.unregisterListener(this);
    }

    @Override
    public void onMoveDetected(float x, float y) {
        sendMessage(new MoveMessage(x, y));
    }

    private void addButtonsClickListeners() {
        findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClicked(ButtonType.Left);
            }
        });
        findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClicked(ButtonType.Right);
            }
        });
    }

    private void onButtonClicked(ButtonType button) {
        sendMessage(new ClickMessage(button));
    }

    private void sendMessage(Message message) {
        // TODO sending message to server
        Log.v("Message", message.getMessageJSON().toString());
    }
}