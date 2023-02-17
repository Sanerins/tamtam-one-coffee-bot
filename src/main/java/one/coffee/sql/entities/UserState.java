package one.coffee.sql.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class UserState implements Entity {

    public static final UserState DEFAULT = new UserState(StateType.DEFAULT);
    public static final UserState WAITING = new UserState(StateType.WAITING);
    public static final UserState CHATTING = new UserState(StateType.CHATTING);

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private StateType stateType;

    public enum StateType {
        DEFAULT(0),
        WAITING(1),
        CHATTING(2);

        private final long id;

        StateType(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public static StateType fromId(long id) {
            for (StateType stateType : values()) {
                if (stateType.getId() == id) {
                    return stateType;
                }
            }

            LOG.warn("State with id {} not found!", id);
            return DEFAULT;
        }
    }

    public UserState(StateType stateType) {
        Objects.requireNonNull(stateType, "StateType can't be null!");

        this.stateType = stateType;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    @Override
    public String toString() {
        return "UserState{" +
                "stateType=" + stateType.getId() +
                '}';
    }

    @Override
    public String sqlValues() {
        return String.format("(%d)", stateType.getId());
    }
}
