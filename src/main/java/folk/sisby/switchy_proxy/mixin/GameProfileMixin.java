package folk.sisby.switchy_proxy.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import folk.sisby.switchy.api.exception.ModuleNotFoundException;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.modules.FabricTailorModule;
import folk.sisby.switchy_proxy.SwitchyProxyProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameProfile.class, remap = false)
public class GameProfileMixin implements SwitchyProxyProfile {
	@Unique private SwitchyPreset switchy_proxy$matchedPreset;

	@Override
	public void switchy_proxy$setMatchedPreset(SwitchyPreset preset) {
		switchy_proxy$matchedPreset = preset;
	}

	@Inject(method = "getProperties", at = @At("RETURN"), cancellable = true)
	public void replaceTexturePropertyForBridges(CallbackInfoReturnable<PropertyMap> cir) {
		if (switchy_proxy$matchedPreset != null) {
			try {
				FabricTailorModule module = switchy_proxy$matchedPreset.getModule(FabricTailorModule.ID, FabricTailorModule.class);
				if (module.skinValue != null) {
					PropertyMap newMap = new PropertyMap();
					newMap.putAll(cir.getReturnValue());
					newMap.removeAll("textures");
					newMap.put("textures", new Property("textures", module.skinValue, module.skinSignature));
					cir.setReturnValue(newMap);
				}
			} catch (ModuleNotFoundException ignored) {
			}
		}
	}
}
