package one.coffee.commands;

import one.coffee.ParentClasses.Result;

public class StateResult extends Result {

    public StateResult(ResultState resultState) {
        super(resultState);
    }

    public StateResult(ResultState resultState, String error) {
        super(resultState, error);
    }
}
