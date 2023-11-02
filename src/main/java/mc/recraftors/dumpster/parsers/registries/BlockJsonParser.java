package mc.recraftors.dumpster.parsers.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mc.recraftors.dumpster.utils.InResult;
import net.minecraft.block.Block;

@TargetRegistryType(Block.class)
public class BlockJsonParser implements RegistryJsonParser {
    private Block block;

    @Override
    public InResult in(Object o) {
        if (o instanceof Block b) {
            this.block = b;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        if (block == null) return null;
        JsonObject o = new JsonObject();
        JsonArray states = new JsonArray();
        block.getStateManager().getStates().forEach(state -> {
            JsonObject b = new JsonObject();
            JsonObject material = new JsonObject();
            material.add("blocksLight", new JsonPrimitive(state.getMaterial().blocksLight()));
            material.add("blocksMovement", new JsonPrimitive(state.getMaterial().blocksMovement()));
            material.add("isBurnable", new JsonPrimitive(state.getMaterial().isBurnable()));
            material.add("isLiquid", new JsonPrimitive(state.getMaterial().isLiquid()));
            material.add("isReplaceable", new JsonPrimitive(state.getMaterial().isReplaceable()));
            material.add("isSolid", new JsonPrimitive(state.getMaterial().isSolid()));
            material.add("color", new JsonPrimitive(state.getMaterial().getColor().color));
            material.add("pistonBehaviour", new JsonPrimitive(state.getMaterial().getPistonBehavior().name()));
            b.add("material", material);
            JsonArray properties = new JsonArray();
            state.getProperties().forEach(property -> {
                JsonObject prop = new JsonObject();
                prop.add("name", new JsonPrimitive(property.getName()));
                prop.add("type", new JsonPrimitive(property.getType().toString()));
                JsonArray values = new JsonArray();
                property.getValues().forEach(v -> values.add(String.valueOf(v)));
                prop.add("values", values);
                properties.add(prop);
            });
            b.add("properties", properties);
            b.add("emitsRedstonePower", new JsonPrimitive(state.emitsRedstonePower()));
            b.add("exceedsCube", new JsonPrimitive(state.exceedsCube()));
            b.add("luminance", new JsonPrimitive(state.getLuminance()));
            b.add("hasBlockEntity", new JsonPrimitive(state.hasBlockEntity()));
            b.add("hasComparatorOutput", new JsonPrimitive(state.hasComparatorOutput()));
            b.add("hasRandomTicks", new JsonPrimitive(state.hasRandomTicks()));
            b.add("hasSidedTransparency", new JsonPrimitive(state.hasSidedTransparency()));
            b.add("isAir", new JsonPrimitive(state.isAir()));
            b.add("isOpaque", new JsonPrimitive(state.isOpaque()));
            b.add("isToolRequired", new JsonPrimitive(state.isToolRequired()));
            b.add("offsetType", new JsonPrimitive(state.getOffsetType().name()));
            b.add("renderType", new JsonPrimitive(state.getRenderType().name()));
            states.add(b);
        });
        o.add("blockStates", states);
        o.add("blastResistance", new JsonPrimitive(block.getBlastResistance()));
        o.add("canMobSpawnInside", new JsonPrimitive(block.canMobSpawnInside()));
        o.add("jumpVelocityMultiplier", new JsonPrimitive(block.getJumpVelocityMultiplier()));
        o.add("slipperiness", new JsonPrimitive(block.getSlipperiness()));
        o.add("translationKey", new JsonPrimitive(block.getTranslationKey()));
        o.add("velocityMultiplier", new JsonPrimitive(block.getVelocityMultiplier()));
        o.add("hasDynamicBounds", new JsonPrimitive(block.hasDynamicBounds()));
        return o;
    }
}
