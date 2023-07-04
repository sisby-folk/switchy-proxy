package folk.sisby.switchy_proxy.modules;

import folk.sisby.switchy.api.SwitchySerializable;
import net.minecraft.nbt.NbtCompound;

public class ProxyModuleConfig implements SwitchySerializable {
	private boolean latchEnabled = false;

	public static final String KEY_LATCH = "latch";

	@Override
	public NbtCompound toNbt() {
		NbtCompound outCompound = new NbtCompound();
		outCompound.putBoolean(KEY_LATCH, latchEnabled);
		return outCompound;
	}

	@Override
	public void fillFromNbt(NbtCompound nbt) {
		latchEnabled = nbt.getBoolean(KEY_LATCH);
	}

	public boolean isLatchEnabled() {
		return latchEnabled;
	}

	public void setLatchEnabled(boolean latchEnabled) {
		this.latchEnabled = latchEnabled;
	}
}
