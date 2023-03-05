package folk.sisby.switchy_proxy;

import net.minecraft.nbt.NbtCompound;

public record ProxyTag(String prefix, String suffix) {
	public static final String PLACEHOLDER = "text";
	public static final String KEY_PREFIX = "prefix";
	public static final String KEY_SUFFIX = "suffix";

	public static ProxyTag parse(String pattern) throws IllegalStateException {
		if (!pattern.contains(PLACEHOLDER)) throw new IllegalArgumentException("Pattern is missing placeholder.");
		String pre = pattern.substring(0, pattern.indexOf(PLACEHOLDER));
		String suf = pattern.substring(pattern.indexOf(PLACEHOLDER) + PLACEHOLDER.length());
		if (pre.contains(PLACEHOLDER) || suf.contains(PLACEHOLDER)) throw new IllegalArgumentException("Too many placeholders!");
		if (pre.isEmpty() && suf.isEmpty()) throw new IllegalArgumentException("Suffix and Prefix can't be both empty");
		return new ProxyTag(pre, suf);
	}

	public NbtCompound toNbt() {
		NbtCompound outNbt = new NbtCompound();
		outNbt.putString(KEY_PREFIX, prefix);
		outNbt.putString(KEY_SUFFIX, suffix);
		return outNbt;
	}

	public static ProxyTag fromNbt(NbtCompound nbt) {
		String prefix = nbt.getString(KEY_PREFIX);
		String suffix = nbt.getString(KEY_SUFFIX);
		return new ProxyTag(prefix, suffix);
	}

	public boolean matches(String tester) {
		return tester.startsWith(prefix) && tester.endsWith(suffix);
	}

	public String strip(String tester) {
		return tester.substring(prefix.length(), tester.length()-suffix.length());
	}

	@Override
	public String toString() {
		return prefix + PLACEHOLDER + suffix;
	}
}
