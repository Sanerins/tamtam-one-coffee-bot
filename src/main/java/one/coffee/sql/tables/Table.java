package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.utils.Utils;
import one.coffee.sql.entities.Entity;

import java.util.List;
import java.util.Map;

public abstract class Table {

    protected String shortName;
    protected List<Map.Entry<String /*argNames*/, String /*argValues*/>> args;

    protected Table() {
    }

    protected final void init() {
        if (shortName == null || shortName.trim().isEmpty()) {
            throw new IllegalArgumentException("Illegal 'shortName'! Got: " + shortName);
        }

        if (args.size() <= 1) {
            throw new IllegalArgumentException("Not enough 'args'! Got: " + args);
        }

        DB.dropTable(this);
        DB.createTable(this);
    }

    // Returns full name of the table with specified types.
    @Override
    public final String toString() {
        StringBuilder fullName = new StringBuilder(shortName);
        fullName.append(Utils.SIGNATURE_START);
        if (!args.isEmpty()) {
            for (Map.Entry<String, String> arg : args) {
                fullName.append(arg.getKey())
                        .append(Utils.ARG_ATTRIBUTES_SEPARATOR)
                        .append(arg.getValue())
                        .append(Utils.ARGS_SEPARATOR);
            }
            fullName.delete(fullName.length() - Utils.ARGS_SEPARATOR.length(), fullName.length());
        }
        fullName.append(Utils.SIGNATURE_END);
        return fullName.toString();
    }

    // Формируется в порядке предоставления имён полей в конструкторе каждой таблицы.
    // Фактически результат будет равен 'tableName(argName1, argName2, ...)'.
    public final String getSignature(Entity entity) {
        StringBuilder signatureName = new StringBuilder(getShortName());
        signatureName.append(Utils.SIGNATURE_START);
        if (entity.isCreated()) {
            signatureName.append(args.get(0).getKey())
                    .append(Utils.ARGS_SEPARATOR);
        }

        for (Map.Entry<String, String> arg : args.subList(1, args.size())) {
            String argName = arg.getKey();
            signatureName.append(argName)
                    .append(Utils.ARGS_SEPARATOR);
        }
        signatureName.delete(signatureName.length() - Utils.ARGS_SEPARATOR.length(), signatureName.length());
        signatureName.append(Utils.SIGNATURE_END);
        return signatureName.toString();
    }

    public final String getShortName() {
        return shortName;
    }

}
