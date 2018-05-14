package sr.andromover.connection;

import sr.andromover.message.Message;

public interface ConnectionManager {
    void sendMessage(Message message);
}
