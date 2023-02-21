package one.coffee.sql.entities;

import one.coffee.sql.DB;
import one.coffee.sql.Utils;
import one.coffee.sql.tables.UserStatesTable;
import one.coffee.sql.tables.UsersTable;
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

    @Argument
    private final long id;
    @Argument
    private final StateType stateType;
    private final boolean isCreated;

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
            if (!DB.hasEntity(UserStatesTable.INSTANCE, this)) {
                throw new IllegalArgumentException("No UsetState with 'id' = " + id);
            }
        }

        this.isCreated = true;
    }

    @Override
    public boolean isCreated() {
        return isCreated;
    }

    @Override
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
        StringBuilder sqlValues = new StringBuilder(Utils.SIGNATURE_START);

        if (isCreated()) {
            sqlValues.append(id).append(Utils.ARGS_SEPARATOR);
        }

        // TODO Оптимизировать, чтобы ручками не вводить каждый аргумент. Сделать список из аннотированных элементов @Argument
        // и добавлять их. При таком подходе метод будет один на всех.
        sqlValues.append(stateType.ordinal());

        sqlValues.append(Utils.SIGNATURE_END);
        return sqlValues.toString();
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

            throw new IllegalArgumentException("StateType with 'id' = " + id + " not found!");
        }
    }

}
