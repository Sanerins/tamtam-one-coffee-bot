package one.coffee.sql.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public enum UserState {
    DEFAULT,
    WAITING,
    CHATTING;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static UserState fromId(long id) {
        for (UserState userState : values()) {
            if (userState.ordinal() == id) {
                return userState;
            }
        }
        LOG.warn("No UserState with id {}", id);
        return DEFAULT;
    }
}
