package sr.andromover.message;


import org.json.JSONException;
import org.json.JSONObject;

public abstract class Message {
    protected String messageType = "undefined";

    protected abstract JSONObject getMessageDetailsJSON() throws JSONException;

    public JSONObject getMessageJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", messageType);
            json.put("details", getMessageDetailsJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
