package one.coffee.ParentClasses;

public class Result {
    protected ResultState resultState;

    /**
     * Value is null if {@link #resultState resultState} != {@link ResultState#ERROR ERROR}
     */
    protected String error;

    public Result(ResultState resultState) {
        this.resultState = resultState;
    }

    public Result(ResultState resultState, String error) {
        this.resultState = resultState;
        this.error = error;
    }

    public ResultState getResultState() {
        return resultState;
    }

    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public enum ResultState {
        SUCCESS(0),
        ERROR(1),
        SERVICE_UNAVAILABLE(2);

        private final byte state;

        ResultState(byte state) {
            this.state = state;
        }

        ResultState(int state) {
            this.state = (byte) state;
        }

        public byte getState() {
            return state;
        }
    }
}
