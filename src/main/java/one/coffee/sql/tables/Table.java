package one.coffee.sql.tables;

import one.coffee.sql.DB;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Table {

    protected String shortName;
    protected List<Map.Entry<String /*argNames*/, String /*argValues*/>> args;
    private static final String SIGNATURE_OPEN_BRACKET = "(";
    private static final String SIGNATURE_CLOSE_BRACKET = ")";
    private static final String ARG_ATTRIBUTES_SEPARATOR = " ";
    private static final String ARGS_SEPARATOR = ",";

    protected Table() {
    }

    protected void init() {
        DB.dropTableFor(shortName);
        DB.createTableFor(this);
    }

    // Returns full name of the table with specified types
    @Override
    public String toString() {
        StringBuilder fullName = new StringBuilder(shortName);
        fullName.append(SIGNATURE_OPEN_BRACKET);
        if (!args.isEmpty()) {
            for (Map.Entry<String, String> arg : args) {
                fullName.append(arg.getKey()).append(ARG_ATTRIBUTES_SEPARATOR).append(arg.getValue()).append(ARGS_SEPARATOR);
            }
            fullName.deleteCharAt(fullName.length() - 1);
        }
        fullName.append(SIGNATURE_CLOSE_BRACKET);
        return fullName.toString();
    }

    // Without column `id`
    public String signature() {
        StringBuilder signatureName = new StringBuilder(shortName);
        signatureName.append(SIGNATURE_OPEN_BRACKET);
        if (!args.isEmpty()) {
            for (Map.Entry<String, String> arg : args) {
                String argName = arg.getKey();
                if (!argName.equals("id")) {
                    signatureName.append(argName).append(ARGS_SEPARATOR);
                }
            }
            signatureName.deleteCharAt(signatureName.length() - 1);
        }
        signatureName.append(SIGNATURE_CLOSE_BRACKET);
        return signatureName.toString();
    }

    public String getShortName() {
        return shortName;
    }
}