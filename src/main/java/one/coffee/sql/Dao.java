package one.coffee.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(long id) throws SQLException;
    List<T> getAll();
    void save(T t);
    void delete(T t) throws SQLException;

}
