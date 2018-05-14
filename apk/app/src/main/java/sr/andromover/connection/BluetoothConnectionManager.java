package sr.andromover.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.util.UUID;

import sr.andromover.connection.data.ConnectionData;
import sr.andromover.message.Message;

//TODO do it
public class BluetoothConnectionManager implements ConnectionManager {

    private String deviceName;

    public BluetoothConnectionManager(String deviceName) throws IOException {
        this.deviceName = deviceName;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceName);

        device.createRfcommSocketToServiceRecord(UUID.fromString("ff6d5857-0a31-4905-924e-95b18d5b0290"));


    }

    public BluetoothConnectionManager(ConnectionData connectionData) {

    }

    @Override
    public void sendMessage(Message message) {

    }
}

