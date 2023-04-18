package folk.sisby.switchy_proxy.mixin;

import eu.pb4.styledchat.config.ChatStyle;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.modules.DrogtorModule;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import folk.sisby.switchy_proxy.SwitchyProxyPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ChatStyle.class, remap = false)
public class ChatStyleMixin {

	@ModifyArgs(
			method = "getChat",
			at = @At(value = "INVOKE", target = "Leu/pb4/placeholders/api/Placeholders;parseText(Leu/pb4/placeholders/api/node/TextNode;Leu/pb4/placeholders/api/PlaceholderContext;Ljava/util/regex/Pattern;Ljava/util/Map;)Lnet/minecraft/text/Text;")
	)
	public void getChat(Args args, ServerPlayerEntity player, Text message) {
		if (player instanceof SwitchyProxyPlayer spp) {
			SwitchyPreset preset = spp.switchy_proxy$getMatchedPreset();
			if (preset != null) {
				Map<String, Text> placeholders = new HashMap<>(args.get(3));
				if (preset.containsModule(StyledNicknamesModule.ID)) {
					Text nickname = preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).getText();
					if (nickname != null) {
						placeholders.put("player", nickname);
						args.set(3, placeholders);
						return;
					}
				}
				if (preset.containsModule(DrogtorModule.ID)) {
					Text nickname = preset.getModule(DrogtorModule.ID, DrogtorModule.class).getText();
					if (nickname != null) {
						placeholders.put("player", nickname);
						args.set(3, placeholders);
					}
				}
			}
		}
	}
}
