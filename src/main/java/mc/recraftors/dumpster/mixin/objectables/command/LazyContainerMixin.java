package mc.recraftors.dumpster.mixin.objectables.command;

import mc.recraftors.dumpster.utils.accessors.IStringable;
import net.minecraft.server.function.CommandFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.Optional;

@Mixin(CommandFunction.LazyContainer.class)
public abstract class LazyContainerMixin implements IStringable {
    @Shadow private Optional<CommandFunction> function;

    @Override
    public String dumpster$stringify() {
        StringBuilder builder = new StringBuilder();
        this.function.ifPresent(
                commandFunction -> Arrays.stream(commandFunction.getElements())
                        .forEach(e -> builder.append(((IStringable)e).dumpster$stringify()).append("\n"))
        );
        return builder.toString();
    }
}
