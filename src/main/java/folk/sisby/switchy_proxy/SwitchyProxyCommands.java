package folk.sisby.switchy_proxy;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.exception.ModuleNotFoundException;
import folk.sisby.switchy.api.exception.PresetNotFoundException;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy.util.SwitchyCommand;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static folk.sisby.switchy.util.Feedback.*;
import static folk.sisby.switchy.util.SwitchyCommand.execute;

public class SwitchyProxyCommands implements SwitchyEvents.CommandInit {

	public static CompletableFuture<Suggestions> suggestPatterns(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		SwitchyPresets presets = ((SwitchyPlayer) context.getSource().getPlayer()).switchy$getPresets();
		try {
			String name = context.getArgument("preset", String.class);
			ProxyModule module = presets.getPreset(name).getModule(ProxyModule.ID, ProxyModule.class);
			CommandSource.suggestMatching(module.getTags().stream().map(ProxyTag::toString), builder);
		} catch (IllegalArgumentException ignored) {

		}
		return builder.buildFuture();
	}

	public void addProxy(ServerPlayerEntity player, SwitchyPresets presets, String name, String pattern) {
		try {
			SwitchyPreset preset = presets.getPreset(name);
			try {
				ProxyModule module = preset.getModule(ProxyModule.ID, ProxyModule.class);
				try {
					module.addTag(ProxyTag.parse(pattern));
					tellSuccess(player, "commands.switchy_proxy.add.success", literal(pattern), literal(name));
				} catch (IllegalArgumentException ignoredParseFail) {
					tellInvalid(player, "commands.switchy_proxy.add.fail.invalid", literal("text"));
				}
			} catch (ModuleNotFoundException ignored) {
				tellInvalidTry(player, "commands.switchy_proxy.add.fail.module", "commands.switchy.module.enable.command", literal(ProxyModule.ID.toString()));
			}
		} catch (PresetNotFoundException ignored) {
			tellInvalidTry(player, "commands.switchy_proxy.add.fail.preset", "commands.switchy.list.command");
		}
	}

	public void removeProxy(ServerPlayerEntity player, SwitchyPresets presets, String name, String pattern) {
		try {
			SwitchyPreset preset = presets.getPreset(name);
			try {
				ProxyModule module = preset.getModule(ProxyModule.ID, ProxyModule.class);
				try {
					module.removeTag(pattern);
					tellSuccess(player, "commands.switchy_proxy.remove.success", literal(pattern), literal(name));
				} catch (IllegalArgumentException ignoredMissingTag) {
					tellInvalid(player, "commands.switchy_proxy.remove.fail.pattern");
				}
			} catch (ModuleNotFoundException ignored) {
				tellInvalidTry(player, "commands.switchy_proxy.remove.fail.module", "commands.switchy.module.enable.command", literal(ProxyModule.ID.toString()));
			}
		} catch (PresetNotFoundException ignored) {
			tellInvalidTry(player, "commands.switchy_proxy.remove.fail.preset", "commands.switchy.list.command");
		}
	}

	@Override
	public void registerCommands(LiteralArgumentBuilder<ServerCommandSource> switchyArgument, BiConsumer<Text, Predicate<ServerPlayerEntity>> helpTextRegistry) {
		switchyArgument
				.then(CommandManager.literal("proxy")
						.then(SwitchyCommand.presetArgument(true)
								.then(CommandManager.literal("add")
										.then(CommandManager.argument("pattern", StringArgumentType.greedyString())
												.executes(c -> execute(c, (player, presets) -> addProxy(player, presets, c.getArgument("preset", String.class), c.getArgument("pattern", String.class)))))
								)
								.then(CommandManager.literal("remove")
										.then(CommandManager.argument("pattern", StringArgumentType.greedyString())
												.suggests(SwitchyProxyCommands::suggestPatterns)
												.executes(c -> execute(c, (player, presets) -> removeProxy(player, presets, c.getArgument("preset", String.class), c.getArgument("pattern", String.class)))))
								)
						)
				);
		List.of(
				helpText("commands.switchy_proxy.add.help", "commands.switchy_proxy.add.command","commands.switchy_proxy.placeholder.preset", "commands.switchy_proxy.placeholder.proxy"),
				helpText("commands.switchy_proxy.remove.help", "commands.switchy_proxy.remove.command","commands.switchy_proxy.placeholder.preset", "commands.switchy_proxy.placeholder.proxy")
		).forEach(t -> helpTextRegistry.accept(t, (p) -> true));
	}
}
