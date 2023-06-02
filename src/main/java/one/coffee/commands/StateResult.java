package one.coffee.commands;

import one.coffee.ParentClasses.Result;
import one.coffee.callbacks.CallbackResult;

public class StateResult extends Result {

    public StateResult(ResultState resultState) {
        super(resultState);
    }

    public StateResult(ResultState resultState, String error) {
        super(resultState, error);
    }

    public CallbackResult toCallbackResult() {
        return new CallbackResult(resultState, error);
    }
}
