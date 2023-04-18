package folk.sisby.switchy_proxy.compat;

import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import folk.sisby.switchy_proxy.SwitchyProxy;
import net.minecraft.server.network.ServerPlayerEntity;

public class StyledNicknamesCompat {
	public static void update(ServerPlayerEntity player, SwitchyPreset preset) {
		SwitchyProxy.LOGGER.info("EEEE");
		preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).updateFromPlayer(player, null);
	}
	public static void apply(ServerPlayerEntity player, SwitchyPreset preset) {
		SwitchyProxy.LOGGER.info("FFFF");
		preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).applyToPlayer(player);
	}
}
