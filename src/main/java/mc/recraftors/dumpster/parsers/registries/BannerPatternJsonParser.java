package mc.recraftors.dumpster.parsers.registries;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.InResult;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

@TargetRegistryType(BannerPattern.class)
public class BannerPatternJsonParser implements RegistryJsonParser {
    private BannerPattern pattern;

    @Override
    public InResult in(Object o) {
        if (o instanceof BannerPattern b) {
            this.pattern = b;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        if (pattern == null) return null;
        JsonObject o = new JsonObject();
        RegistryKey<BannerPattern> key = RegistryKey.of(Registry.BANNER_PATTERN_KEY, Registry.BANNER_PATTERN.getId(pattern));
        o.add("bannerSprite", new JsonPrimitive(BannerPattern.getSpriteId(key, true).toString()));
        o.add("shieldSprite", new JsonPrimitive(BannerPattern.getSpriteId(key, false).toString()));
        return o;
    }
}
