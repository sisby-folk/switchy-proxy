package folk.sisby.switchy_proxy.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import folk.sisby.switchy.api.exception.ModuleNotFoundException;
import folk.sisby.switchy.modules.FabricTailorModule;
import folk.sisby.switchy_proxy.SwitchyProxyProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(YggdrasilMinecraftSessionService.class)
public class YggdrasilMinecraftSessionServiceMixin {
	@ModifyVariable(method = "getTextures", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Iterables;getFirst(Ljava/lang/Iterable;Ljava/lang/Object;)Ljava/lang/Object;", remap = false), ordinal = 1, remap = false)
	public Property replaceTexturePropertyForBridges(Property original, final GameProfile profile) {
		if (profile instanceof SwitchyProxyProfile spp && spp.switchy_proxy$getMatchedPreset() != null) {
			try {
				String value = spp.switchy_proxy$getMatchedPreset().getModule(FabricTailorModule.ID, FabricTailorModule.class).skinValue;
				if (value != null) {
					return new Property("textures", value);
				}
			} catch (ModuleNotFoundException ignored) {
			}
		}
		return original;
	}
}
