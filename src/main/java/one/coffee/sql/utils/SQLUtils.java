package one.coffee.sql.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class SQLUtils {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final int DEFAULT_ID = -1;
    public static final String TABLE_SIGNATURE_START = "(";
    public static final String TABLE_SIGNATURE_END = ")";
    public static final String ARG_ATTRIBUTES_SEPARATOR = " ";
    public static final String ARGS_SEPARATOR = ", ";
    public static final String STRING_QUOTER = "'";
}
