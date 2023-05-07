package one.coffee.callbacks;

import one.coffee.ParentClasses.Result;

public class CallbackResult extends Result {
    public CallbackResult(ResultState resultState) {
        super(resultState);
    }

    public CallbackResult(ResultState resultState, String error) {
        super(resultState, error);
    }
}
