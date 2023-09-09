package folk.sisby.switchy_proxy.mixin;

import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy_proxy.SwitchyProxyPlayer;
import folk.sisby.switchy_proxy.SwitchyProxyProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin implements SwitchyProxyPlayer {
	private SwitchyPreset switchy_proxy$matchedPreset;
	private SwitchyPreset switchy_proxy$latchedPreset;
	private String switchy_proxy$proxiedContent;

	@Override
	public void switchy_proxy$setMatchedPreset(SwitchyPreset preset) {
		if (preset != null) switchy_proxy$setLatchedPreset(preset);
		switchy_proxy$matchedPreset = preset;
		if (((ServerPlayerEntity) (Object) this) instanceof SwitchyProxyProfile spp) spp.switchy_proxy$setMatchedPreset(preset);
	}

	@Override
	public void switchy_proxy$setProxiedContent(String content) {
		switchy_proxy$proxiedContent = content;
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
	public String switchy_proxy$getProxiedContent() {
		return switchy_proxy$proxiedContent;
	}

	@Override
	public SwitchyPreset switchy_proxy$getLatchedPreset() {
		return switchy_proxy$latchedPreset;
	}
}
