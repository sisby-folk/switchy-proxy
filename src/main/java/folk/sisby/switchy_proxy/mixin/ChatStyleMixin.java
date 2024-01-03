package folk.sisby.switchy_proxy.mixin;

import eu.pb4.styledchat.config.ChatStyle;
import folk.sisby.switchy_proxy.SwitchyProxyPlayer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static folk.sisby.switchy_proxy.SwitchyProxy.proxyDisplayName;

@Mixin(ChatStyle.class)
public class ChatStyleMixin {
	@Unique private static Text proxyPlayer(ServerPlayerEntity player) {
		if (player instanceof SwitchyProxyPlayer spp) {
			Text proxyName = proxyDisplayName(spp);
			if (proxyName != null) {
				return proxyName;
			}
		}
		return player.getDisplayName();
	}

	@Redirect(method = "getChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxyChat(ServerPlayerEntity player) {
		return proxyPlayer(player);
	}

	@Redirect(method = "getSayCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxySay(ServerCommandSource source) {
		return source.getEntity() instanceof ServerPlayerEntity player ? proxyPlayer(player) : source.getDisplayName();
	}

	@Redirect(method = "getMeCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;getDisplayName()Lnet/minecraft/text/Text;"))
	public Text proxyMe(ServerCommandSource source) {
		return source.getEntity() instanceof ServerPlayerEntity player ? proxyPlayer(player) : source.getDisplayName();
	}
}
