package sr.andromover.connection;

import sr.andromover.connection.data.ConnectionData;

public class ConnectionManagerFactory {

    public static ConnectionManager create(ConnectionData data) {
        switch (data.type()) {
            case IP:
                return new IpConnectionManager(data);
            case BLUETOOTH:
                return new BluetoothConnectionManager(data);
        }
        throw new IllegalArgumentException();
    }
}
