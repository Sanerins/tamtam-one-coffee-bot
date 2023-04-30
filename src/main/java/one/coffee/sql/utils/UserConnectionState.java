package one.coffee.sql.utils;

public enum UserConnectionState {
    DEFAULT,
    IN_PROGRESS,
    SUCCESSFUL,
    UNSUCCESSFUL;

    public static UserConnectionState fromId(long id) {
        for (UserConnectionState state : UserConnectionState.values()) {
            if (state.ordinal() == id) {
                return state;
            }
        }
        return UserConnectionState.DEFAULT;
    }
}
