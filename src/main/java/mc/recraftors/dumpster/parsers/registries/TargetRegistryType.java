package mc.recraftors.dumpster.parsers.registries;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class annotation for {@link RegistryJsonParser} implementations,
 * to indicate the targeted recipe type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetRegistryType {
    /**
     * The registry parser's target type
     */
    Class<?> value();

    /**
     * The registry parser's priority over the provided type.
     * Class inheritance takes first priority in all conditions.
     */
    int priority() default 1;

    /**
     * Whether the parser should be considered as an addon to the
     * targeted class, in order to only add the provided data
     * to that of the original class.
     * <p>
     * To be used when adding properties via mixin or such.
     */
    boolean addon() default false;
}
