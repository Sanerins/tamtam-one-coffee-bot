package one.coffee.sql;

import one.coffee.sql.utils.SQLUtils;
import one.coffee.utils.StaticContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Dao<T extends Entity> {

    protected String shortName;
    protected List<Map.Entry<String /*argNames*/, String /*argTypes*/>> args;

    protected Dao() {
    }

    abstract public Optional<T> get(long id);

    abstract public void save(T t);

    abstract public void delete(T t);

    protected final void init() {
        if (shortName == null || shortName.trim().isEmpty()) {
            throw new IllegalArgumentException("Illegal 'shortName'! Got: " + shortName);
        }

        if (args.size() <= 1) {
            throw new IllegalArgumentException("Not enough 'args'! Got: " + args);
        }

        if (StaticContext.getIsRecreatingTablesNeeded().get()) {
            DB.dropTable(this);
            DB.createTable(this);
        }
    }

    // Возвращает полное название таблицы с указанием типов данных для каждого представленного поля.
    @Override
    public final String toString() {
        StringBuilder fullName = new StringBuilder(shortName);
        fullName.append(SQLUtils.TABLE_SIGNATURE_START);
        if (!args.isEmpty()) {
            for (Map.Entry<String, String> arg : args) {
                fullName.append(arg.getKey())
                        .append(SQLUtils.ARG_ATTRIBUTES_SEPARATOR)
                        .append(arg.getValue())
                        .append(SQLUtils.ARGS_SEPARATOR);
            }
            fullName.delete(fullName.length() - SQLUtils.ARGS_SEPARATOR.length(), fullName.length());
        }
        fullName.append(SQLUtils.TABLE_SIGNATURE_END);
        return fullName.toString();
    }

    // Формируется в порядке предоставления имён полей в конструкторе каждой таблицы.
    // Фактически результат будет равен 'tableName(argName1, argName2, ...)'.
    // Не учитывается поле 'id' каждой сущности, если она ещё не была создана (см. имплементации Entity::isCreated).
    public final String getSignature(Entity entity) {
        StringBuilder signatureName = new StringBuilder(getShortName());
        signatureName.append(SQLUtils.TABLE_SIGNATURE_START);
        if (entity.isCreated()) {
            signatureName.append(args.get(0).getKey())
                    .append(SQLUtils.ARGS_SEPARATOR);
        }

        for (Map.Entry<String, String> arg : args.subList(1, args.size())) {
            String argName = arg.getKey();
            signatureName.append(argName)
                    .append(SQLUtils.ARGS_SEPARATOR);
        }
        signatureName.delete(signatureName.length() - SQLUtils.ARGS_SEPARATOR.length(), signatureName.length());
        signatureName.append(SQLUtils.TABLE_SIGNATURE_END);
        return signatureName.toString();
    }

    public final String getShortName() {
        return shortName;
    }

}
