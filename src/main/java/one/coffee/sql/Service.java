package one.coffee.sql;

import java.util.Optional;

public interface Service<T> {

    Optional<T> get(long id);

    Optional<T> save(T t);

    void delete(T t);

}
