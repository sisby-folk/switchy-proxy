package folk.sisby.switchy_proxy.compat;

import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.modules.DrogtorModule;
import folk.sisby.switchy_proxy.SwitchyProxy;
import net.minecraft.server.network.ServerPlayerEntity;

public class DrogtorCompat {
	public static void update(ServerPlayerEntity player, SwitchyPreset preset) {
		SwitchyProxy.LOGGER.info("BBBBB");
		preset.getModule(DrogtorModule.ID, DrogtorModule.class).updateFromPlayer(player, null);
	}
	public static void apply(ServerPlayerEntity player, SwitchyPreset preset) {
		SwitchyProxy.LOGGER.info("CCCCC");
		preset.getModule(DrogtorModule.ID, DrogtorModule.class).applyToPlayer(player);
	}
}
