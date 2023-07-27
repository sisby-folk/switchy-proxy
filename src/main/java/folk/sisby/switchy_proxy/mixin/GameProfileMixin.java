package folk.sisby.switchy_proxy.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import folk.sisby.switchy.api.exception.ModuleNotFoundException;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.modules.FabricTailorModule;
import folk.sisby.switchy_proxy.SwitchyProxyProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameProfile.class, remap = false)
public class GameProfileMixin implements SwitchyProxyProfile {
	private SwitchyPreset switchy_proxy$matchedPreset;
	private SwitchyPreset switchy_proxy$latchedPreset;

	@Override
	public void switchy_proxy$setMatchedPreset(SwitchyPreset preset) {
		if (preset != null) switchy_proxy$setLatchedPreset(preset);
		switchy_proxy$matchedPreset = preset;
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
	public SwitchyPreset switchy_proxy$getLatchedPreset() {
		return switchy_proxy$latchedPreset;
	}

	@Inject(method = "getProperties", at = @At("RETURN"), cancellable = true)
	public void replaceTexturePropertyForBridges(CallbackInfoReturnable<PropertyMap> cir) {
		if (switchy_proxy$getMatchedPreset() != null) {
			try {
				String value = switchy_proxy$getMatchedPreset().getModule(FabricTailorModule.ID, FabricTailorModule.class).skinValue;
				if (value != null) {
					PropertyMap newMap = new PropertyMap();
					newMap.putAll(cir.getReturnValue());
					newMap.put("textures", new Property("textures", value));
					cir.setReturnValue(newMap);
				}
			} catch (ModuleNotFoundException ignored) {
			}
		}
	}
}
