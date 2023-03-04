package folk.sisby.switchy_proxy.mixin;

import eu.pb4.placeholders.api.TextParserUtils;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy.modules.StyledNicknamesCompat;
import folk.sisby.switchy_proxy.SwitchyProxyPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = PlayerEntity.class, priority = 999)
public class PlayerEntityMixin implements SwitchyProxyPlayer {
	private String switchy_proxy$matchedPreset;

	@ModifyArg(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
	private Text switchyProxy$replaceName(Text text) {
		if (switchy_proxy$getMatchedPreset() != null && (Object) this instanceof SwitchyPlayer sp) {
			// Clear immediately
			String matchedPreset = switchy_proxy$getMatchedPreset();
			switchy_proxy$setMatchedPreset(null);

			SwitchyPresets presets = sp.switchy$getPresets();
			try {
				SwitchyPreset preset = presets.getPreset(matchedPreset);
				try {
					String nickname = preset.getModule(StyledNicknamesCompat.ID, StyledNicknamesCompat.class).styled_nickname;
					if (nickname != null) {
						return TextParserUtils.formatText(nickname);
					}
				} catch (IllegalArgumentException ignored) {

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
