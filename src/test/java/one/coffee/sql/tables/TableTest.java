package one.coffee.sql.tables;

import one.coffee.sql.DB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.TypeDescriptor;

public abstract class TableTest {

    @AfterEach
    void cleanupTable() {
        //DB.cleanupTable(getTable());
    }

    protected abstract Table getTable();

}
