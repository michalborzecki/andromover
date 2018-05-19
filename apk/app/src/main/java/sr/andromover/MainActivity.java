package sr.andromover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import sr.andromover.click.ButtonType;
import sr.andromover.connection.ConnectionManager;
import sr.andromover.connection.ConnectionManagerFactory;
import sr.andromover.connection.IpConnectionManager;
import sr.andromover.connection.data.ConnectionData;
import sr.andromover.message.ClickMessage;
import sr.andromover.message.Message;
import sr.andromover.message.MoveMessage;
import sr.andromover.move.MoveDetector;
import sr.andromover.move.MoveDetectorListener;

public class MainActivity extends Activity implements MoveDetectorListener {
    private MoveDetector moveDetector;
    private ConnectionManager connectionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON & WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main);

        ConnectionData connectionData = (ConnectionData)getIntent().getExtras().get(Constants.CONNECTION_DATA);

        Runnable errorCallback = () -> this.runOnUiThread(() ->  {
            new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.connection_error_title)
                .setMessage(R.string.connection_error_message)
                .setNeutralButton(R.string.button_ok, (dialogInterface, i) -> finish())
                .show();
        });

        connectionManager = ConnectionManagerFactory.create(connectionData, errorCallback);

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
        findViewById(R.id.leftButton).setOnClickListener(view -> onButtonClicked(ButtonType.Left));
        findViewById(R.id.rightButton).setOnClickListener(view -> onButtonClicked(ButtonType.Right));
    }

    private void onButtonClicked(ButtonType button) {
        sendMessage(new ClickMessage(button));
    }

    private void sendMessage(Message message) {
        connectionManager.sendMessage(message);
    }
}