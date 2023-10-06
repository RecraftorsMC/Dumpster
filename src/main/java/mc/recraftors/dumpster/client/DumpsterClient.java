package mc.recraftors.dumpster.client;

import mc.recraftors.dumpster.utils.ConfigUtils;
import mc.recraftors.dumpster.utils.Utils;
import net.fabricmc.api.ClientModInitializer;

import java.io.IOException;

public class DumpsterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        try {
            ConfigUtils.save();
        } catch (IOException e) {
            Utils.LOGGER.error("An error occurred trying to save {} config", Utils.MOD_ID, e);
        }
    }
}
