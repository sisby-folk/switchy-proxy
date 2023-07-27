package folk.sisby.switchy_proxy.mixin;

import eu.pb4.styledchat.config.ChatStyle;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import folk.sisby.switchy_proxy.SwitchyProxyProfile;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatStyle.class)
public class ChatStyleMixin {
	private @Nullable Text proxyDisplayName(SwitchyProxyProfile spp) {
		SwitchyPreset preset = spp.switchy_proxy$getMatchedPreset();
		if (preset != null) {
			spp.switchy_proxy$setMatchedPreset(null);
			if (preset.containsModule(StyledNicknamesModule.ID)) {
				return preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).getText();
			}
		}
		return null;
	}

	@Redirect(method = "getChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxyChat(ServerPlayerEntity player) {
		if (player.getGameProfile() instanceof SwitchyProxyProfile spp) {
			Text proxyName = proxyDisplayName(spp);
			if (proxyName != null) {
				return proxyName;
			}
		}
		return player.getDisplayName();
	}

	@Redirect(method = "getSayCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxySay(ServerCommandSource source) {
		ServerPlayerEntity player = source.getPlayer();
		if (player != null && player.getGameProfile() instanceof SwitchyProxyProfile spp) {
			Text proxyName = proxyDisplayName(spp);
			if (proxyName != null) {
				return proxyName;
			}
		}
		return source.getDisplayName();
	}

	@Redirect(method = "getMeCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxyMe(ServerCommandSource source) {
		ServerPlayerEntity player = source.getPlayer();
		if (player != null && player.getGameProfile() instanceof SwitchyProxyProfile spp) {
			Text proxyName = proxyDisplayName(spp);
			if (proxyName != null) {
				return proxyName;
			}
		}
		return source.getDisplayName();
	}
}
