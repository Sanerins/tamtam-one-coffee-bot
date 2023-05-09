package one.coffee.sql.states;

public enum UserConnectionState {
    IN_PROGRESS,
    SUCCESSFUL,
    UNSUCCESSFUL;

    public static UserConnectionState fromId(long id) {
        for (UserConnectionState state : UserConnectionState.values()) {
            if (state.ordinal() == id) {
                return state;
            }
        }
        throw new IllegalArgumentException("No userConnectionState with id " + id);
    }
}
