package mc.recraftors.dumpster.recipes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class annotation for {@link RecipeJsonParser} implementations,
 * to indicate the targeted recipe type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetRecipeType {
    /**
     * The recipe parser's target type ID
     */
    String value();

    /**
     * The recipe parser's priority over the provided type ID.
     * Allows to overhaul other potential parser, in case of recipe modification.
     */
    int priority() default 1;

    /**
     * Indicates other recipe schema IDs that can be supported by
     * the parser. Allows for cumulative format support/multiple
     * parser acceptance for a same schema.
     */
    String[] supports() default {};
}
