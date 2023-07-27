package folk.sisby.switchy_proxy.mixin;

import eu.pb4.styledchat.config.ChatStyle;
import folk.sisby.switchy_proxy.SwitchyProxyProfile;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.HashMap;
import java.util.Map;

import static folk.sisby.switchy_proxy.SwitchyProxy.proxyDisplayName;

@Mixin(ChatStyle.class)
public class ChatStyleMixin {
	@ModifyArgs(method = "getChat", at = @At(value = "INVOKE", target = "Leu/pb4/placeholders/api/Placeholders;parseText(Leu/pb4/placeholders/api/node/TextNode;Leu/pb4/placeholders/api/PlaceholderContext;Ljava/util/regex/Pattern;Ljava/util/Map;)Lnet/minecraft/text/Text;"))
	public void proxyChat(Args args, ServerPlayerEntity player, Text message) {
		if (player.getGameProfile() instanceof SwitchyProxyProfile spp) {
			Map<String, Text> placeholders = new HashMap<>(args.get(3));
			if (proxyDisplayName(spp, true, t -> placeholders.put("player", t))) {
				args.set(3, placeholders);
			}
		}
	}

	@ModifyArgs(method = "getSayCommand", at = @At(value = "INVOKE", target = "Leu/pb4/placeholders/api/Placeholders;parseText(Lnet/minecraft/text/Text;Ljava/util/regex/Pattern;Ljava/util/Map;)Lnet/minecraft/text/Text;"))
	public void proxySay(Args args, ServerCommandSource source, Text message) {
		ServerPlayerEntity player = source.method_44023();
		if (player != null && player.getGameProfile() instanceof SwitchyProxyProfile spp) {
			Map<String, Text> placeholders = new HashMap<>(args.get(2));
			if (proxyDisplayName(spp, true, t -> {
				placeholders.put("player", t);
				placeholders.put("displayName", t);
			})) {
				args.set(2, placeholders);
			}
		}
	}

	@ModifyArgs(method = "getMeCommand", at = @At(value = "INVOKE", target = "Leu/pb4/placeholders/api/Placeholders;parseText(Lnet/minecraft/text/Text;Ljava/util/regex/Pattern;Ljava/util/Map;)Lnet/minecraft/text/Text;"))
	public void proxyMe(Args args, ServerCommandSource source, Text message) {
		ServerPlayerEntity player = source.method_44023();
		if (player != null && player.getGameProfile() instanceof SwitchyProxyProfile spp) {
			Map<String, Text> placeholders = new HashMap<>(args.get(2));
			if (proxyDisplayName(spp, true, t -> {
				placeholders.put("player", t);
				placeholders.put("displayName", t);
			})) {
				args.set(2, placeholders);
			}
		}
	}
}
