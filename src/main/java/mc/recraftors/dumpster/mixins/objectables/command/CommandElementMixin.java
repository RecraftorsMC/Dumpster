package mc.recraftors.dumpster.mixins.objectables.command;

import com.mojang.brigadier.ParseResults;
import mc.recraftors.dumpster.utils.accessors.IStringable;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandFunction.CommandElement.class)
public abstract class CommandElementMixin implements IStringable {
    @Shadow @Final private ParseResults<ServerCommandSource> parsed;

    @Override
    public String dumpster$stringify() {
        return this.parsed.toString();
    }
}
