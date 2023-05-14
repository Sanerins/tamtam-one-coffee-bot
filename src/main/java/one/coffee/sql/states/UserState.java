package one.coffee.sql.states;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public enum UserState {
    DEFAULT(0),
    WAITING(1),
    CHATTING(2),
    PROFILE_DEFAULT(3),
    PROFILE_CHANGE_NAME(4),
    PROFILE_CHANGE_CITY(5),
    PROFILE_CHANGE_DESCRIPTION(6);


    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final long id;

    UserState(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public static UserState fromId(long id) {
        for (UserState userState : values()) {
            if (userState.getId() == id) {
                return userState;
            }
        }
        LOG.warn("No UserState with id {}", id);
        return DEFAULT;
    }

}
