package one.coffee;

import one.coffee.sql.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.utils.SQLUtils;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest
@ArgumentsSource(DBTest.UserList.class)
@Timeout(5)
public @interface DBTest {

    int nUsers();

    @Component
    class UserList
            implements ArgumentsProvider {

        @Autowired
        protected UserService userService;

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            int nUsers = extensionContext.getTestMethod().get().getAnnotation(DBTest.class).nUsers();
            List<User> users = new ArrayList<>();
            for (int i = 0; i < nUsers; ++i) {
                long id = i + 1;
                User user = new User(
                        id,
                        "City" + id,
                        UserState.DEFAULT,
                        SQLUtils.NO_ID,
                        null
                );
                userService.save(user);
                users.add(user);
            }
            return Stream.of(Arguments.of(users));
        }
    }

}
