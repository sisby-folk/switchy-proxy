package folk.sisby.switchy_proxy.mixin;

import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy_proxy.SwitchyProxyPlayer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PlayerEntity.class, priority = 1001)
public class PlayerEntityMixin implements SwitchyProxyPlayer {
	private SwitchyPreset switchy_proxy$matchedPreset;

	@Override
	public void switchy_proxy$setMatchedPreset(SwitchyPreset preset) {
		switchy_proxy$matchedPreset = preset;
	}

	@Override
	public SwitchyPreset switchy_proxy$getMatchedPreset() {
		return switchy_proxy$matchedPreset;
	}
}
