package mc.recraftors.dumpster.mixin.objectables.command;

import mc.recraftors.dumpster.utils.accessors.IStringable;
import net.minecraft.server.function.CommandFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandFunction.FunctionElement.class)
public abstract class FunctionElementMixin implements IStringable {
    @Shadow @Final private CommandFunction.LazyContainer function;

    @Override
    public String dumpster$stringify() {
        return ((IStringable)this.function).dumpster$stringify();
    }
}
