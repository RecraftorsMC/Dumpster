package mc.recraftors.dumpster.parsers.features;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TargetFeatureConfigType {
    String value();

    int priority() default 1;

    String[] supports() default {};
}
