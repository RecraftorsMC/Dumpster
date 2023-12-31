package mc.recraftors.dumpster.mixin.objectables.command;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.ParseResults;
import mc.recraftors.dumpster.utils.accessors.IStringable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ParseResults.class, remap = false)
public abstract class ParseResultsMixin implements IStringable {
    @Shadow @Final private ImmutableStringReader reader;

    @Override
    public String dumpster$stringify() {
        return this.reader.getString();
    }
}
