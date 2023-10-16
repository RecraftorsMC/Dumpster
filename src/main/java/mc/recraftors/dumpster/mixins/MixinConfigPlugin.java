package mc.recraftors.dumpster.mixins;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        // ignored
    }

    @Override
    public String getRefMapperConfig() {
        return null; // ignored
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String[] s = mixinClassName.split("\\.");
        String s1 = s[s.length-1];
        if (Objects.equals(s1, "ClientPlayerEntityMixin") || Objects.equals(s1, "ClientCommandsInjector")) {
            Optional<ModContainer> mc = FabricLoader.getInstance().getAllMods().stream().filter(c -> c.getMetadata().getId().equals("minecraft")).findFirst();
            return mc.map(modContainer -> !modContainer.getMetadata().getVersion().getFriendlyString().equals("1.19.2")).orElse(true);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // ignored
    }

    @Override
    public List<String> getMixins() {
        return null; // ignored
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // ignored
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // ignored
    }
}
