package sr.andromover.connection;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import sr.andromover.connection.data.ConnectionData;
import sr.andromover.message.Message;

public class IpConnectionManager implements ConnectionManager {

    private final String address;
    private final int port;

    private Socket socket;

    private Runnable errorCallback;

    private OutputStreamWriter writer;

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
            OutputStream outputStream = this.socket.getOutputStream();
            writer = new OutputStreamWriter(outputStream);
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
                writer.write(message.getMessageJSON().toString());
                writer.flush();
            } catch (IOException e) {
                this.socket = null;
                errorCallback.run();
            }
        }

        Log.v("Message in connection", message.getMessageJSON().toString());
    }
}
