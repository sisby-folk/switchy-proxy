package folk.sisby.switchy_proxy.mixin;

import com.mojang.authlib.GameProfile;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy_proxy.SwitchyProxyProfile;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameProfile.class)
public class GameProfileMixin implements SwitchyProxyProfile {
	private SwitchyPreset switchy_proxy$matchedPreset;
	private SwitchyPreset switchy_proxy$latchedPreset;

	@Override
	public void switchy_proxy$setMatchedPreset(SwitchyPreset preset) {
		if (preset != null) switchy_proxy$setLatchedPreset(preset);
		switchy_proxy$matchedPreset = preset;
	}

	@Override
	public void switchy_proxy$setLatchedPreset(SwitchyPreset preset) {
		switchy_proxy$latchedPreset = preset;
	}

	@Override
	public SwitchyPreset switchy_proxy$getMatchedPreset() {
		return switchy_proxy$matchedPreset;
	}

	@Override
	public SwitchyPreset switchy_proxy$getLatchedPreset() {
		return switchy_proxy$latchedPreset;
	}
}
