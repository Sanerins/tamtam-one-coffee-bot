package one.coffee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Test
@Timeout(5)
public @interface DBTest {
}
