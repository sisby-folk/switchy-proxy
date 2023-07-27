package folk.sisby.switchy_proxy;

import folk.sisby.switchy.api.presets.SwitchyPreset;

public interface SwitchyProxyProfile {
	void switchy_proxy$setMatchedPreset(SwitchyPreset preset);

	void switchy_proxy$setProxiedContent(String content);

	void switchy_proxy$setLatchedPreset(SwitchyPreset preset);

	SwitchyPreset switchy_proxy$getMatchedPreset();

	String switchy_proxy$getProxiedContent();

	SwitchyPreset switchy_proxy$getLatchedPreset();
}
