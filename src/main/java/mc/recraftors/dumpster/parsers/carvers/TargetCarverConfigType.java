package mc.recraftors.dumpster.parsers.carvers;

import net.minecraft.world.gen.carver.CarverConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetCarverConfigType {
    Class<? extends CarverConfig> value();

    int priority() default 1;
}
