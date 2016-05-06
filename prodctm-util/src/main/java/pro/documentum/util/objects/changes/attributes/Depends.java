package pro.documentum.util.objects.changes.attributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Depends {

    Class<? extends IAttributeHandler>[] on() default {};

}
