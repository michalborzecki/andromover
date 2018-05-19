package sr.andromover.connection;

import sr.andromover.connection.data.ConnectionData;

public class ConnectionManagerFactory {

    public static ConnectionManager create(ConnectionData data, Runnable errorCallback) {
        switch (data.type()) {
            case IP:
                return new IpConnectionManager(data, errorCallback);
            case BLUETOOTH:
                return new BluetoothConnectionManager(data);
        }
        throw new IllegalArgumentException();
    }
}
