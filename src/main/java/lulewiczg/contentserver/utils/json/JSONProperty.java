package lulewiczg.contentserver.utils.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate if field should be serialized to JSON. Should be used
 * with {@link JSONModel}.
 *
 * @author Grzegorz
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONProperty {

    String propertyName();

    boolean quoted() default false;
}
