package one.coffee.bot;

public class UpdateResult {

    private UpdateState updateState;

    /**
     * Value is null if {@link #updateState updateState} != {@link UpdateState#ERROR ERROR}
     */
    private String error;

    public UpdateResult(UpdateState updateState) {
        this.updateState = updateState;
    }

    public UpdateResult(UpdateState updateState, String error) {
        this.updateState = updateState;
        this.error = error;
    }

    public UpdateState getUpdateState() {
        return updateState;
    }

    public void setUpdateState(UpdateState updateState) {
        this.updateState = updateState;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public enum UpdateState {
        SUCCESS(0),
        ERROR(1),
        SERVICE_UNAVAILABLE(2);

        private final byte state;

        UpdateState(byte state) {
            this.state = state;
        }

        UpdateState(int state) {
            this.state = (byte) state;
        }

        public byte getState() {
            return state;
        }
    }
}
