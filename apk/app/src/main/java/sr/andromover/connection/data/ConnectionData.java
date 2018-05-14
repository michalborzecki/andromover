package sr.andromover.connection.data;

import java.io.Serializable;
import java.util.function.Consumer;

import sr.andromover.connection.BluetoothConnectionManager;
import sr.andromover.connection.ConnectionManager;
import sr.andromover.connection.IpConnectionManager;

public class ConnectionData implements Serializable {

    private final String address;
    private final int port;
    private final Type type;

    public ConnectionData(String address, int port, Type type) {
        this.address = address;
        this.port = port;
        this.type = type;
    }

    public String address() {
        return address;
    }

    public int port() {
        return port;
    }

    public Type type() {
        return type;
    }

    public enum Type {
        IP, BLUETOOTH
    }
}
