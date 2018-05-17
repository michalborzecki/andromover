package sr.andromover;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sr.andromover.connection.data.ConnectionData;

public class MenuActivity extends Activity {

    private Button internetButton;
    private Button bluetoothButton;

    private View.OnClickListener internetButtonListener = view -> {

        final View dialogView = getLayoutInflater().inflate(R.layout.internet_connection_dialog, null);

        new AlertDialog.Builder(MenuActivity.this)
                .setView(dialogView)
                .setTitle(R.string.server_ip_input_title)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    EditText ipInput = dialogView.findViewById(R.id.ipInput);
                    EditText portInput = dialogView.findViewById(R.id.portInput);

                    String ipAddress = ipInput.getText().toString();
                    String port = portInput.getText().toString();
                    if(!ipAddress.isEmpty() && !port.isEmpty()) {
                        startWorking(ipAddress, Integer.parseInt(port));
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    };

    private View.OnClickListener bluetoothButtonListener = view -> {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

        List<BluetoothDevice> devicesList = new LinkedList<>();
        devicesList.addAll(devices);

        String[] items = new String[devicesList.size()];
        for(int i = 0; i < devicesList.size(); i++) {
            items[i] = devicesList.get(i).getName();
        }


        new AlertDialog.Builder(MenuActivity.this)
                .setTitle(R.string.bluetooth_title)
                .setItems(items, (dialogInterface, i) -> System.out.println(items[i])).show();

    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_menu);

        internetButton = findViewById(R.id.internetButton);
        bluetoothButton = findViewById(R.id.bluetoothButton);

        internetButton.setOnClickListener(internetButtonListener);
        bluetoothButton.setOnClickListener(bluetoothButtonListener);
    }

    private void startWorking(String ipAddress, int portNumber) {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra(Constants.CONNECTION_DATA, new ConnectionData(ipAddress, portNumber, ConnectionData.Type.IP));
        startActivity(myIntent);
    }



}
