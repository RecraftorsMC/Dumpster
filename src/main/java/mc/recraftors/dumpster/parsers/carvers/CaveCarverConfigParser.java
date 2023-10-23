package mc.recraftors.dumpster.parsers.carvers;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.Objectable;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CaveCarverConfig;

@TargetCarverConfigType(CaveCarverConfig.class)
public class CaveCarverConfigParser implements CarverJsonParser {
    private CaveCarverConfig config;

    @Override
    public boolean in(CarverConfig carver) {
        if (carver instanceof CaveCarverConfig c) {
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
