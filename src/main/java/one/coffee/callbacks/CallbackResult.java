package one.coffee.callbacks;

import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateResult;

public class CallbackResult extends Result {
    public CallbackResult(ResultState resultState) {
        super(resultState);
    }

    public CallbackResult(ResultState resultState, String error) {
        super(resultState, error);
    }

    public StateResult toStateResult() {
        return new StateResult(resultState, error);
    }
}
