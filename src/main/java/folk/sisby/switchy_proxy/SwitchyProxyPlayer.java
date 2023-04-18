package folk.sisby.switchy_proxy;

import folk.sisby.switchy.api.presets.SwitchyPreset;

public interface SwitchyProxyPlayer {
	void switchy_proxy$setMatchedPreset(SwitchyPreset preset);

	SwitchyPreset switchy_proxy$getMatchedPreset();
}
