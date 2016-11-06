package pro.documentum.util.crypto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyField {

    int order();

    int min() default Integer.MIN_VALUE;

    int max() default Integer.MAX_VALUE;

    String lengthField() default "";

}
