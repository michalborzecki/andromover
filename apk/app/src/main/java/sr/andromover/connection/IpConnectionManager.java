package sr.andromover.connection;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import sr.andromover.connection.data.ConnectionData;
import sr.andromover.message.Message;

public class IpConnectionManager implements ConnectionManager {

    private final String address;
    private final int port;

    private Socket socket;

    private Runnable errorCallback;

    private OutputStream outputStream;

    private Executor networkTasksExecutor = Executors.newSingleThreadExecutor();

    public IpConnectionManager(ConnectionData connectionData, Runnable errorCallback) {
        this(connectionData.address(), connectionData.port());
        this.errorCallback = errorCallback;
    }


    private IpConnectionManager(String address, int port) {
        this.address = address;
        this.port = port;

        this.socket = new Socket();
        networkTasksExecutor.execute(this::connect);
    }

    private void connect() {
        try {
            this.socket.connect(new InetSocketAddress(this.address, this.port));
            outputStream = this.socket.getOutputStream();
        } catch (IOException e) {
            this.socket = null;
            errorCallback.run();
        }
    }

    public void sendMessage(Message message) {
        networkTasksExecutor.execute(() -> executeSendingMessage(message));
    }

    private void executeSendingMessage(Message message) {
        if (socket != null && socket.isConnected()) {
            try {
                byte[] messageJson = message.getMessageJSON().toString().getBytes();
                byte[] lenBytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(messageJson.length).array();
                outputStream.write(lenBytes);
                outputStream.write(messageJson);
                outputStream.flush();
            } catch (IOException e) {
                this.socket = null;
                errorCallback.run();
            }
        }

        Log.v("Message in connection", message.getMessageJSON().toString());
    }
}
