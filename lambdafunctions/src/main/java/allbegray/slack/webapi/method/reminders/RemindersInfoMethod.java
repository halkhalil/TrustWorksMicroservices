package allbegray.slack.webapi.method.reminders;

import allbegray.slack.validation.Problem;
import allbegray.slack.validation.ValidationError;
import allbegray.slack.webapi.SlackWebApiConstants;
import allbegray.slack.webapi.method.AbstractMethod;

import java.util.List;
import java.util.Map;

public class RemindersInfoMethod extends AbstractMethod {


    private final String reminderId;

    public RemindersInfoMethod(String reminderId) {

        this.reminderId = reminderId;
    }

    @Override
    public String getMethodName() {
        return SlackWebApiConstants.REMINDERS_INFO;
    }

    @Override
    public void validate(List<ValidationError> errors) {
        if (reminderId == null) {
            addError(errors, "reminder", Problem.REQUIRED);
        }
    }

    @Override
    protected void createParameters(Map<String, String> parameters) {
        parameters.put("reminder", reminderId);
    }

}
