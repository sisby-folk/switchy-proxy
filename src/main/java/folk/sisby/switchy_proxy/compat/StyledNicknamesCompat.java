package folk.sisby.switchy_proxy.compat;

import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import net.minecraft.server.network.ServerPlayerEntity;

public class StyledNicknamesCompat {
	public static void update(ServerPlayerEntity player, SwitchyPreset preset) {
		preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).updateFromPlayer(player, null);
	}
	public static void apply(ServerPlayerEntity player, SwitchyPreset preset) {
		preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).applyToPlayer(player);
	}
}
