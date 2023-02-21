package one.coffee;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.util.Random;
import java.util.stream.Stream;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest
@ArgumentsSource(DBTest.UserList.class)
@Timeout(5)
public @interface DBTest {

    int nUsers();

    class UserList
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            int nUsers = extensionContext.getTestMethod().get().getAnnotation(DBTest.class).nUsers();
            return Stream.generate(() -> 0).limit(nUsers).map(i -> {
                long userId = System.currentTimeMillis() % (nUsers * 10) + 1;
                User user = new User(
                        userId,
                        "City" + userId,
                        UserState.DEFAULT.getStateId(),
                        -1
                );
                user.commit();
                return Arguments.of(user);
            });
        }
    }
}
