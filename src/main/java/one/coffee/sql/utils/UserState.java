package one.coffee.sql.utils;

public enum UserState {
    DEFAULT,
    WAITING,
    CHATTING;

    public static UserState fromId(long id) {
        for (UserState userState : values()) {
            if (userState.ordinal() == id) {
                return userState;
            }
        }
        throw new IllegalArgumentException("No UserState with id " + id);
    }

}
