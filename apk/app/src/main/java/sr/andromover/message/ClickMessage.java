package sr.andromover.message;

import org.json.JSONException;
import org.json.JSONObject;

import sr.andromover.click.ButtonType;

public class ClickMessage extends Message {
    private ButtonType button;

    public ClickMessage() {
        this(ButtonType.None);
    }

    public ClickMessage(ButtonType button) {
        messageType = "click";
        this.button = button;
    }

    public ButtonType getButton() {
        return button;
    }

    public void setButton(ButtonType button) {
        this.button = button;
    }

    @Override
    protected JSONObject getMessageDetailsJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("button", button.toString());
        return json;
    }
}
