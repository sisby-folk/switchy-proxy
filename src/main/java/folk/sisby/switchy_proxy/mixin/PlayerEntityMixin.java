package folk.sisby.switchy_proxy.mixin;

import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy.modules.DrogtorModule;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import folk.sisby.switchy_proxy.SwitchyProxy;
import folk.sisby.switchy_proxy.SwitchyProxyPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = PlayerEntity.class, priority = 1001)
public class PlayerEntityMixin implements SwitchyProxyPlayer {
	private String switchy_proxy$matchedPreset;

	@ModifyArg(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
	private Text switchyProxy$replaceName(Text text) {
		String matchedPreset = switchy_proxy$getMatchedPreset();
		if (matchedPreset != null && (Object) this instanceof SwitchyPlayer sp) {
			SwitchyPresets presets = sp.switchy$getPresets();
			try {
				SwitchyPreset preset = presets.getPreset(matchedPreset);
				if (SwitchyProxy.StyledCompat) {
					try {
						Text nickname = preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).getText();
						if (nickname != null) {
							return nickname;
						}
					} catch (IllegalArgumentException ignored) {
					}
				}
				if (SwitchyProxy.DrogtorCompat) {
					try {
						Text nickname = preset.getModule(DrogtorModule.ID, DrogtorModule.class).getText();
						if (nickname != null) {
							return nickname;
						}
					} catch (IllegalArgumentException ignored) {
					}
				}
			} catch (IllegalArgumentException ignored) {

			}
		}
		return text;
	}

	@Override
	public void switchy_proxy$setMatchedPreset(String name) {
		switchy_proxy$matchedPreset = name;
	}

	@Override
	public String switchy_proxy$getMatchedPreset() {
		return switchy_proxy$matchedPreset;
	}
}
