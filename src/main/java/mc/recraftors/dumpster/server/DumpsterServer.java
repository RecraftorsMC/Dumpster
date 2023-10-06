package mc.recraftors.dumpster.server;

import mc.recraftors.dumpster.utils.ConfigUtils;
import mc.recraftors.dumpster.utils.Utils;
import net.fabricmc.api.DedicatedServerModInitializer;

import java.io.IOException;

public class DumpsterServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        try {
            ConfigUtils.save();
        } catch (IOException e) {
            Utils.LOGGER.error("An error occurred trying to save {} config", Utils.MOD_ID, e);
        }
    }
}
