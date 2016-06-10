package dk.trustworks.personalassistant.nlp;

import dk.trustworks.personalassistant.model.ActionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 31/05/16.
 */
public class IntentOutcome {

    public ActionType action;
    public final Map<String, String> args;

    public IntentOutcome() {
        args = new HashMap<>();
    }

    public IntentOutcome(ActionType action) {
        this.action = action;
        args = new HashMap<>();
    }
}
