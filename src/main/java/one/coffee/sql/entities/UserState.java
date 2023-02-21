package one.coffee.sql.entities;

import one.coffee.sql.tables.UserStatesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@CommitOnCreate
public class UserState
        implements Entity {

    public static final UserState DEFAULT = new UserState(StateType.DEFAULT);
    public static final UserState WAITING = new UserState(StateType.WAITING);
    public static final UserState CHATTING = new UserState(StateType.CHATTING);

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final long id;
    private final StateType stateType;

    public UserState(StateType stateType) {
        this(-1, stateType);
    }

    public UserState(long id, StateType stateType) {
        this.stateType = stateType;

        if (id <= 0) {
            commit();
            this.id = UserStatesTable.getUserStateByStateType(stateType).getId();
        } else {
            this.id = id;
        }
    }

    public long getId() {
        return id;
    }

    public long getStateId() {
        return stateType.ordinal();
    }

    @Override
    public String toString() {
        return "UserState{" +
                "stateType=" + stateType +
                '}';
    }

    @Override
    public String sqlArgValues() {
        return String.format("(%d)", stateType.ordinal());
    }

    @Override
    public void commit() {
        UserStatesTable.putUserState(this);
    }

    public enum StateType {
        DEFAULT,
        WAITING,
        CHATTING;

        public static StateType fromId(long id) {
            for (StateType stateType : values()) {
                if (stateType.ordinal() == id) {
                    return stateType;
                }
            }

            LOG.warn("State with id {} not found!", id);
            return DEFAULT;
        }
    }

}
