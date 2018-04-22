package sr.andromover.message;

import org.json.JSONException;
import org.json.JSONObject;

public class MoveMessage extends Message {

    private float x;
    private float y;

    public MoveMessage() {
        this(0.0f, 0.0f);
    }

    public MoveMessage(float x, float y) {
        messageType = "move";
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    protected JSONObject getMessageDetailsJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("x", x);
        json.put("y", y);
        return json;
    }
}
