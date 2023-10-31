package mc.recraftors.dumpster.parsers.carvers;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.RavineCarverConfig;

@TargetCarverConfigType(RavineCarverConfig.class)
public class RavineCarverConfigParser implements CarverJsonParser {
    private RavineCarverConfig config;

    @Override
    public boolean in(CarverConfig carver) {
        if (carver instanceof RavineCarverConfig c) {
            this.config = c;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        return ((Objectable)this.config).toJson();
    }
}
