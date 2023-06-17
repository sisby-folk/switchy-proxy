package folk.sisby.switchy_proxy.mixin;

import eu.pb4.styledchat.config.ChatStyle;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.modules.DrogtorModule;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import folk.sisby.switchy_proxy.SwitchyProxyPlayer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatStyle.class)
public class ChatStyleMixin {
	private @Nullable Text proxyDisplayName(SwitchyProxyPlayer spp) {
		SwitchyPreset preset = spp.switchy_proxy$getMatchedPreset();
		if (preset != null) {
			spp.switchy_proxy$setMatchedPreset(null);

			if (preset.containsModule(StyledNicknamesModule.ID)) {
				Text nickname = preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).getText();
				if (nickname != null) {
					return nickname;
				}
			}
			if (preset.containsModule(DrogtorModule.ID)) {
				Text nickname = preset.getModule(DrogtorModule.ID, DrogtorModule.class).getText();
				if (nickname != null) {
					return nickname;
				}
			}
		}
		return null;
	}

	@Redirect(method = "getChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxyChat(ServerPlayerEntity player) {
		if (player instanceof SwitchyProxyPlayer spp) {
			Text proxyName = proxyDisplayName(spp);
			if (proxyName != null) {
				return proxyName;
			}
		}
		return player.getDisplayName();
	}

	@Redirect(method = "getSayCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxySay(ServerCommandSource source) {
		if (source.getPlayer() instanceof SwitchyProxyPlayer spp) {
			Text proxyName = proxyDisplayName(spp);
			if (proxyName != null) {
				return proxyName;
			}
		}
		return source.getDisplayName();
	}

	@Redirect(method = "getMeCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxyMe(ServerCommandSource source) {
		if (source.getPlayer() instanceof SwitchyProxyPlayer spp) {
			Text proxyName = proxyDisplayName(spp);
			if (proxyName != null) {
				return proxyName;
			}
		}
		return source.getDisplayName();
	}
}
